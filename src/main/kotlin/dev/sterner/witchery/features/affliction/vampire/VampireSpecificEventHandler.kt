package dev.sterner.witchery.features.affliction.vampire

import dev.sterner.witchery.api.event.VampireEvent
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.block.sacrificial_circle.SacrificialBlock
import dev.sterner.witchery.data_attachment.WitcheryAttributes
import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment

import dev.sterner.witchery.data_attachment.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.features.misc.BloodPoolHandler
import dev.sterner.witchery.mixin.DamageSourcesInvoker
import dev.sterner.witchery.payload.SpawnBloodParticlesS2CPayload
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryDamageSources
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Chicken
import net.minecraft.world.entity.monster.Blaze
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.network.PacketDistributor

object VampireSpecificEventHandler {

    const val RESPAWN_BLOOD_AMOUNT = 900
    private const val RESPAWN_FOOD_LEVEL = 10
    private const val BLOOD_HEALING_THRESHOLD = 75
    private const val BLOOD_HEALING_AMOUNT = 1f
    private const val NIGHT_VISION_DURATION = 20 * 15
    private const val SPEED_BOOST_DURATION = 20 * 4
    private const val BLOOD_DRAIN_TICK_RATE = 20
    private const val SUN_DAMAGE_AMOUNT = 2f


    @JvmStatic
    fun tick(player: Player?) {
        if (player !is ServerPlayer) return

        if (player.isAlive && AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.VAMPIRISM) > 0) {

            VampireLeveling.increaseNightTicker(player)

            vampireTick(player)
        }
    }


    private fun vampireTick(player: ServerPlayer) {
        handleSunExposure(player)

        handleBloodHealing(player)

        applyActiveEffects(player)
    }

    /**
     * Handles blood-based healing for vampires
     */
    private fun handleBloodHealing(player: ServerPlayer) {
        val bloodData = BloodPoolLivingEntityAttachment.getData(player)

        if (bloodData.bloodPool >= BLOOD_HEALING_THRESHOLD && player.level().random.nextBoolean()) {
            if (player.health < player.maxHealth && player.health > 0) {
                BloodPoolHandler.decreaseBlood(player, BLOOD_HEALING_THRESHOLD)
                player.heal(BLOOD_HEALING_AMOUNT)
            }
        }
    }

    /**
     * Applies active vampire ability effects
     */
    private fun applyActiveEffects(player: ServerPlayer) {
        val playerData = AfflictionPlayerAttachment.getData(player)

        if (playerData.hasNightVision()) {
            player.addEffect(MobEffectInstance(MobEffects.NIGHT_VISION, NIGHT_VISION_DURATION))
        }

        if (playerData.hasSpeedBoost()) {
            player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, SPEED_BOOST_DURATION))
        }
    }

    private fun handleSunExposure(player: ServerPlayer) {
        val isInSunlight = player.level().canSeeSky(player.blockPosition()) && player.level().isDay
        val currentData = AfflictionPlayerAttachment.getData(player)
        val currentSunTick = currentData.getInSunTick()
        val maxInSunTicks = (player.getAttribute(WitcheryAttributes.VAMPIRE_SUN_RESISTANCE)?.value ?: 0.0).toInt()


        if (isInSunlight && !player.isCreative && !player.isSpectator) {
            if (currentSunTick < maxInSunTicks) {
                val newData = AfflictionPlayerAttachment.smartUpdate(player) {
                    incrementInSunTick(1, maxInSunTicks)
                        .withMaxInSunTickClient(maxInSunTicks)
                }

                val newSunTick = newData.getInSunTick()

                if (newSunTick >= maxInSunTicks) {
                    handleSunDamage(player, newData)
                }
            } else {
                handleSunDamage(player, currentData)
            }
        } else {
            decreaseSunTick(player)
        }
    }


    /**
     * Decreases sun tick counter when not in sunlight
     */
    private fun decreaseSunTick(player: ServerPlayer) {
        val currentData = AfflictionPlayerAttachment.getData(player)

        if (currentData.getInSunTick() > 0) {
            AfflictionPlayerAttachment.smartUpdate(player) {
                decrementInSunTick(2)
            }
        }
    }

    /**
     * Applies sun damage based on vampire level and blood pool
     */
    private fun handleSunDamage(player: ServerPlayer, affData: AfflictionPlayerAttachment.Data) {
        val sunDamageSource = (player.level().damageSources() as DamageSourcesInvoker)
            .invokeSource(WitcheryDamageSources.IN_SUN)
        val bloodData = BloodPoolLivingEntityAttachment.getData(player)
        val vampireLevel = affData.getLevel(AfflictionTypes.VAMPIRISM)

        val event = VampireEvent.SunDamage(player)
        NeoForge.EVENT_BUS.post(event)

        if (event.isCanceled()) {
            return
        }

        when {
            vampireLevel < 5 -> {
                player.hurt(sunDamageSource, Float.MAX_VALUE)
            }

            bloodData.bloodPool >= BLOOD_HEALING_THRESHOLD -> {
                if (player.tickCount % BLOOD_DRAIN_TICK_RATE == 0) {
                    player.hurt(sunDamageSource, SUN_DAMAGE_AMOUNT)
                    playSunDamageEffects(player)
                }
            }

            else -> {
                player.hurt(sunDamageSource, Float.MAX_VALUE)
            }
        }
    }


    /**
     * Plays sun damage visual and sound effects
     */
    private fun playSunDamageEffects(player: ServerPlayer) {
        player.level().playSound(
            null,
            player.x,
            player.y,
            player.z,
            SoundEvents.FIRE_EXTINGUISH,
            SoundSource.PLAYERS,
            0.5f,
            1.0f
        )
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
            player,
            SpawnBloodParticlesS2CPayload(player, player.position().add(0.5, 0.5, 0.5))
        )
    }


    @JvmStatic
    fun respawn(oldPlayer: Player, newPlayer: Player, alive: Boolean) {
        val data = AfflictionPlayerAttachment.getData(newPlayer) // Get from NEW player now

        if (data.getLevel(AfflictionTypes.VAMPIRISM) > 0) {
            val oldBloodData = BloodPoolLivingEntityAttachment.getData(oldPlayer)

            newPlayer.foodData.foodLevel = RESPAWN_FOOD_LEVEL

            BloodPoolLivingEntityAttachment.setData(
                newPlayer,
                BloodPoolLivingEntityAttachment.Data(oldBloodData.maxBlood, RESPAWN_BLOOD_AMOUNT)
            )

            val maxInSunTicks =
                (newPlayer.getAttribute(WitcheryAttributes.VAMPIRE_SUN_RESISTANCE)?.value ?: 0.0).toInt()

            AfflictionPlayerAttachment.smartUpdate(newPlayer) {
                withInSunTick(0, maxInSunTicks)
                    .withMaxInSunTickClient(maxInSunTicks)
            }
        }
    }

    @JvmStatic
    fun onKillEntity(livingEntity: LivingEntity, damageSource: DamageSource) {
        if (damageSource.entity !is ServerPlayer) {
            return
        }

        val player = damageSource.entity as ServerPlayer

        if (livingEntity is Chicken) {

            if (player.mainHandItem.`is`(WitcheryItems.ARTHANA.get()) || player.offhandItem.`is`(WitcheryItems.WINE_GLASS.get())) {
                if (hasNearbySacrificialCircle(livingEntity)) {
                    performChickenBloodRitual(player, livingEntity)
                }
            }
        }

        if (livingEntity is Blaze) {
            VampireLeveling.increaseKilledBlazes(player)
        }
    }

    @JvmStatic
    fun resetNightCount(livingEntity: LivingEntity) {
        if (livingEntity is Player && AfflictionPlayerAttachment.getData(livingEntity)
                .getLevel(AfflictionTypes.VAMPIRISM) == 3
        ) {
            VampireLeveling.resetNightCounter(livingEntity)
        }
    }


    //--------------
    //---HELPERS
    //--------------

    /**
     * Checks if there's a sacrificial circle nearby
     */
    private fun hasNearbySacrificialCircle(entity: LivingEntity): Boolean {
        val possibleSkull = BlockPos.betweenClosedStream(entity.boundingBox.inflate(2.0))

        for (skullPos in possibleSkull) {
            val skullState = entity.level().getBlockState(skullPos)
            if (skullState.`is`(WitcheryBlocks.SACRIFICIAL_CIRCLE.get())) {
                return true
            }
        }

        return false
    }

    /**
     * Performs the chicken blood ritual
     */
    private fun performChickenBloodRitual(player: Player, chicken: Chicken) {
        player.offhandItem.shrink(1)

        val stackCopy = player.offhandItem.copy()
        if (!stackCopy.isEmpty) {
            Containers.dropItemStack(chicken.level(), player.x, player.y, player.z, stackCopy)
        }

        val bloodWine = WitcheryItems.WINE_GLASS.get().defaultInstance
        bloodWine.set(WitcheryDataComponents.CHICKEN_BLOOD.get(), true)
        bloodWine.set(WitcheryDataComponents.BLOOD.get(), chicken.uuid)
        player.setItemInHand(InteractionHand.OFF_HAND, bloodWine)
    }

    /**
     * Creates a sacrificial circle structure
     */
    fun makeSacrificialCircle(player: Player, blockPos: BlockPos) {
        val pieces = SacrificialBlock.STRUCTURE.get().structurePieces

        pieces.forEach { piece ->
            piece.place(blockPos, player.level())
        }

        player.level().setBlockAndUpdate(
            blockPos,
            WitcheryBlocks.SACRIFICIAL_CIRCLE.get().defaultBlockState()
        )

        player.level().getBlockEntity(blockPos)?.let { blockEntity ->
            if (blockEntity is MultiBlockComponentBlockEntity) {
                blockEntity.corePos = blockPos
            }
        }
    }

}