package dev.sterner.witchery.handler.vampire

import com.mojang.blaze3d.platform.ScreenManager.clamp
import dev.architectury.event.EventResult
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.event.events.common.TickEvent
import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.RenderUtils
import dev.sterner.witchery.api.VillagerTransfix
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.api.multiblock.MultiBlockStructure.StructurePiece
import dev.sterner.witchery.block.sacrificial_circle.SacrificialBlock
import dev.sterner.witchery.data.BloodPoolHandler
import dev.sterner.witchery.mixin.DamageSourcesInvoker
import dev.sterner.witchery.payload.SpawnBloodParticlesS2CPayload
import dev.sterner.witchery.payload.VampireAbilityUseC2SPayload
import dev.sterner.witchery.platform.WitcheryAttributes
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.getData
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.setData
import dev.sterner.witchery.registry.*
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Chicken
import net.minecraft.world.entity.monster.Blaze
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks
import java.util.function.Consumer
import kotlin.math.ceil

object VampireEventHandler {

    private val overlay = Witchery.id("textures/gui/ability_hotbar_selection.png")
    private val sun = Witchery.id("textures/gui/vampire_abilities/sun_")
    private var bloodTransferAmount = 10
    private var bloodThreshold = 75
    private const val DAMAGE_DISTRIBUTION = 0.75f

    /**
     * When the vampire-player gets hurt, this will handles damage distribution between hearts and blood.
     * The current distribution is declared by damageDistribution
     * If the player is level 0, aka a human, this will just return the default value, obviously
     */
    fun handleHurt(player: LivingEntity, damageSource: DamageSource, original: Float): Float {

        if (player !is Player) {
            return original
        }

        val vampData = getData(player)
        if (vampData.vampireLevel < 1) {
            return original
        }

        val bloodData = BloodPoolLivingEntityAttachment.getData(player)
        if (bloodData.bloodPool > 0) {
            val bloodPerHealthPoint = bloodThreshold
            val maxBloodAbsorbableDamage = bloodData.bloodPool / bloodPerHealthPoint

            val absorbableDamage = minOf(original * DAMAGE_DISTRIBUTION, maxBloodAbsorbableDamage.toFloat())
            val bloodRequired = (absorbableDamage * bloodPerHealthPoint).toInt()

            BloodPoolLivingEntityAttachment.setData(player, bloodData.copy(bloodPool = bloodData.bloodPool - bloodRequired))

            return original * (1 - DAMAGE_DISTRIBUTION)
        }

        return original
    }

    /**
     * Tick human players blood up naturally.
     * Source of the vampireTicks calls
     */
    fun tick(player: Player?) {
        if (player is ServerPlayer) {
            val vampData = getData(player)
            if (vampData.vampireLevel < 1) {
                val humanBloodData = BloodPoolLivingEntityAttachment.getData(player)
                if (humanBloodData.bloodPool < humanBloodData.maxBlood) {
                    if (player.tickCount % 1000 == 0) {
                        BloodPoolLivingEntityAttachment.increaseBlood(player, 10)
                    }
                }

                return
            }

            if (player.isAlive) {
                vampireTick(player, vampData)
            }
        }
    }

    /**
     * The main tick, handles sun exposure, blood healing and ability effects applying.
     * If the player's vampire level is less than five, the sun will instantly kill it after the 5-second buffer.
     * If the player is level 5 or higher it will drain blood and damage the player.
     */
    private fun vampireTick(player: ServerPlayer, vampData: VampirePlayerAttachment.Data) {
        val isInSunlight = player.level().canSeeSky(player.blockPosition()) && player.level().isDay
        val sunDamageSource = (player.level().damageSources() as DamageSourcesInvoker).invokeSource(WitcheryDamageSources.IN_SUN)
        val bloodData = BloodPoolLivingEntityAttachment.getData(player)

        if (isInSunlight) {
            VampireAbilities.increaseInSunTick(player)
            val maxInSunTicks = player.getAttribute(WitcheryAttributes.VAMPIRE_SUN_RESISTANCE)?.value ?: 0.0
            val data = getData(player)
            setData(player, data.copy(maxInSunTickClient = maxInSunTicks.toInt()))

            if (getData(player).inSunTick >= maxInSunTicks) {
                if (vampData.vampireLevel < 5) {
                    player.hurt(sunDamageSource, Float.MAX_VALUE)
                } else {
                    if (bloodData.bloodPool >= bloodThreshold) {
                        if (player.tickCount % 20 == 0) {
                            player.hurt(sunDamageSource, 2f)
                            player.level().playSound(null, player.x, player.y, player.z, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS , 0.5f, 1.0f)
                            WitcheryPayloads.sendToPlayers(player.level(), SpawnBloodParticlesS2CPayload(player, player.position().add(0.5, 0.5, 0.5)))
                        }
                    } else {
                        player.hurt(sunDamageSource, Float.MAX_VALUE)
                    }
                }
            }
        } else {
            val decreaseAmount = (player.getAttribute(WitcheryAttributes.VAMPIRE_SUN_RESISTANCE)?.value?.div(100)) ?: 1.0
            VampireAbilities.decreaseInSunTick(player, (ceil(decreaseAmount)).toInt())
        }

        if (bloodData.bloodPool >= 75 && player.level().random.nextBoolean()) {
            if (player.health < player.maxHealth && player.health > 0) {
                BloodPoolLivingEntityAttachment.decreaseBlood(player, 75)
                player.heal(1f)
            }
        }

        if (getData(player).isNightVisionActive) {
            player.addEffect(MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 2))
        }
        if (getData(player).isSpeedBoostActive) {
            player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 2))
        }
    }


    /**
     * This is where the vampire-player can use its abilities on other entities, like draining blood and transfix them.
     */
    @JvmStatic
    fun interactEntityWithAbility(player: Player?, entity: Entity?, interactionHand: InteractionHand?): EventResult? {
        if (player is ServerPlayer && entity is LivingEntity) {
            val playerData = getData(player)
            val playerBloodData = BloodPoolLivingEntityAttachment.getData(player)
            if (playerData.abilityIndex == VampireAbility.DRINK_BLOOD.ordinal) {
                return vampireDrinkBloodAbility(player, entity, playerBloodData)
            }
            if (playerData.abilityIndex == VampireAbility.TRANSFIX.ordinal) {
                return vampireTransfixAbility(player, entity)
            }
        }
        return EventResult.pass()
    }

    /**
     * Handles the vampire transfix ability, allowing a vampire player to mesmerize or transfix a Villager.
     *
     * @param player the vampire player using the ability.
     * @param entity the target entity, which must be a Villager for this ability to apply.
     * @return an {@link EventResult} indicating whether the action should proceed, interrupt, or pass.
     */
    private fun vampireTransfixAbility(player: ServerPlayer, entity: LivingEntity): EventResult? {
        if (entity is Villager) {
            val transfixVillager = entity as VillagerTransfix
            transfixVillager.setTransfixedLookVector(player.eyePosition)
            if (getData(player).vampireLevel >= 8) {
                transfixVillager.setMesmerized(player.uuid)
            }
            return EventResult.interruptFalse()
        }
        return EventResult.pass()
    }

    /**
     * Handles the vampire drink blood ability, allowing a vampire player to drain blood from a target entity.
     * The ability considers various conditions, such as the blood pool of the player and target, and applies
     * specific effects like leveling up or harming the target under certain circumstances.
     *
     * @param player the vampire player using the ability.
     * @param entity the target entity from which blood is being drained.
     * @param playerBloodData the blood pool data of the vampire player.
     * @return an {@link EventResult} indicating whether the action should proceed, interrupt, or pass.
     */
    private fun vampireDrinkBloodAbility(
        player: ServerPlayer,
        entity: LivingEntity,
        playerBloodData: BloodPoolLivingEntityAttachment.Data
    ): EventResult? {
        val targetData = BloodPoolLivingEntityAttachment.getData(entity)
        val quality = BloodPoolHandler.BLOOD_PAIR[entity.type] ?: 0

        if (playerBloodData.bloodPool < playerBloodData.maxBlood && targetData.bloodPool >= 0 && targetData.maxBlood > 0) {

            if (quality == 0 && player.level().random.nextFloat() < 0.25f) {
                player.addEffect(MobEffectInstance(MobEffects.POISON, 200, 0))
            }

            val targetHalfBlood = targetData.maxBlood / 2

            if (targetData.bloodPool <= targetHalfBlood && !player.isShiftKeyDown && entity is Villager) {
                VampireLeveling.increaseVillagersHalfBlood(player, entity)

                val cageStates = entity.level().getBlockStates(entity.boundingBox.inflate(2.0, 2.0, 2.0))
                val bars = cageStates.filter { it.`is`(Blocks.IRON_BARS) || it.`is`(BlockTags.SLABS) }.count().toInt()
                if (bars >= 15 + 9) {
                    VampireLeveling.increaseTrappedVillagers(player, entity)
                }
            }

            if (targetData.bloodPool <= targetHalfBlood && !player.isShiftKeyDown) {
                return EventResult.pass()
            }

            player.level().playSound(null, entity.x, entity.y, entity.z, SoundEvents.HONEY_DRINK, SoundSource.PLAYERS)
            val np = entity.position().add(0.5, 0.5, 0.5)
            WitcheryPayloads.sendToPlayers(player.level(), SpawnBloodParticlesS2CPayload(player, np))

            val attribute = player.getAttribute(WitcheryAttributes.VAMPIRE_DRINK_SPEED)?.value?.toInt() ?: 0
            val modifiedAmount = bloodTransferAmount + attribute

            BloodPoolLivingEntityAttachment.decreaseBlood(entity, modifiedAmount)
            BloodPoolLivingEntityAttachment.increaseBlood(player, modifiedAmount)

            val shouldHurt = when {
                entity is Villager && entity is VillagerTransfix && !entity.isSleeping && !entity.isTransfixed() -> true
                targetData.bloodPool < targetHalfBlood -> true
                else -> false
            }

            if (shouldHurt) {
                if (entity is Villager && getData(player).villagersHalfBlood.contains(entity.uuid)) {
                    VampireLeveling.removeVillagerHalfBlood(player, entity)
                    VampireLeveling.removeTrappedVillager(player, entity)
                }
                entity.hurt(player.damageSources().playerAttack(player), 2f)
            }

            if (targetData.bloodPool <= 0) {
                if (entity is Player) {
                    entity.hurt(player.damageSources().playerAttack(player), 2f)
                } else {
                    entity.kill()
                }
            }
        }
        return EventResult.interruptFalse()
    }

    /**
     * The Sun-exposure meter, the blood pool meter and the blood sense and ability hotbar is rendered here.
     */
    @JvmStatic
    fun renderHud(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {

        val client = Minecraft.getInstance()
        val player = client.player ?: return


        val isNotVamp = getData(player).vampireLevel <= 0

        if (isNotVamp) {
            return
        }

        val abilityIndex = getData(player).abilityIndex
        val size = VampireAbilities.getAbilities(player)

        val y = guiGraphics.guiHeight() - 18 - 5
        val x = guiGraphics.guiWidth() / 2 - 36 - 18 * 4 - 5

        drawBloodSense(guiGraphics)

        val bl2 = client.gameMode!!.canHurtPlayer()
        if (!bl2) {
            return
        }

        drawSun(guiGraphics, player)
        drawBatFormHud(guiGraphics, player)

        val bl = player.isShiftKeyDown

        for (i in size.indices) {
            var name = size[i].serializedName
            if (size[i] == VampireAbility.TRANSFIX && bl) {
                name = "night_vision"
            }
            guiGraphics.blit(Witchery.id("textures/gui/vampire_abilities/${name}.png"), x - (25 * i) + 4, y + 4, 16, 16, 0f,0f,16, 16,16, 16)
        }

        if (abilityIndex != -1) {
            guiGraphics.blit(overlay, x - (25 * abilityIndex), y, 24, 23, 0f,0f,24, 23,24, 23)
        }
    }

    // /vampire_abilities/
    private fun drawBatFormHud(guiGraphics: GuiGraphics, player: LocalPlayer) {
        if (TransformationPlayerAttachment.isBat(player)) {
            val maxTicks = TransformationPlayerAttachment.getData(player).maxBatTimeClient
            val currentTicks = maxTicks - TransformationPlayerAttachment.getData(player).batFormTicker
            val bl = player.armorValue > 0
            val y = guiGraphics.guiHeight() - 36 - 10 - if (bl) 10 else 0
            val x = guiGraphics.guiWidth() / 2 - 18 * 4 - 11
            RenderUtils.innerRenderBat(guiGraphics ,maxTicks, currentTicks, y, x)
        }
    }
    /**
     * Draws the sun-exposure HUD
     */
    private fun drawSun(guiGraphics: GuiGraphics, player: Player) {
        val y = guiGraphics.guiHeight() - 36 - 18 - 2
        val x = guiGraphics.guiWidth() / 2 - 8
        val raw = getData(player).inSunTick
        val maxSunTicks = getData(player).maxInSunTickClient
        val sunTick = if (maxSunTicks > 0) {
            clamp((raw.toFloat() / maxSunTicks * 4).toInt(), 0, 4)
        } else {
            0
        }

        if (raw > 1) {
            RenderUtils.blitWithAlpha(guiGraphics.pose(), sun.withSuffix("${sunTick}.png"), x, y, 0f, 0f, 16, 16, 16, 16)
        }
    }

    /**
     * Draws the crosshair entity's blood pool
     */
    private fun drawBloodSense(guiGraphics: GuiGraphics) {
        val x = guiGraphics.guiWidth() / 2 + 13
        val y = guiGraphics.guiHeight() / 2 + 9
        val target = Minecraft.getInstance().crosshairPickEntity
        if (target is LivingEntity && BloodPoolLivingEntityAttachment.getData(target).maxBlood > 0) {
            RenderUtils.innerRenderBlood(guiGraphics,target, y, x)
        }
    }

    /**
     * Resets the sun-exposure ticks when the player dies, to prevent infinity death.
     * Sets the blood to 900, which is 3 full blood drops.
     * Also sets the food to 10 to let us handle exhaustion in our FoodData mixin.
     */
    private fun respawn(oldPlayer: ServerPlayer, newPlayer: ServerPlayer, b: Boolean) {
        if (getData(oldPlayer).vampireLevel > 0) {
            val oldBloodData = BloodPoolLivingEntityAttachment.getData(oldPlayer)
            newPlayer.foodData.foodLevel = 10
            BloodPoolLivingEntityAttachment.setData(newPlayer, BloodPoolLivingEntityAttachment.Data(oldBloodData.maxBlood, 900))
            val data = getData(oldPlayer).copy(inSunTick = 0)
            setData(newPlayer, data)
        }
    }

    /**
     * One of two functions which is used to trigger abilities, this is for when there is no block interaction
     * VampireEventHandler.rightClickBlockAbility
     */
    private fun clientRightClickAbility(player: Player?, interactionHand: InteractionHand?) {
        if (player == null || interactionHand == InteractionHand.OFF_HAND) {
            return
        }

        val playerData = getData(player)
        NetworkManager.sendToServer(VampireAbilityUseC2SPayload(playerData.abilityIndex))
    }

    /**
     * One of two functions which is used to trigger abilities, this is for when there is a block interaction.
     * Together with VampireEventHandler.clientRightClickAbility
     */
    private fun rightClickBlockAbility(player: Player?, interactionHand: InteractionHand?, blockPos: BlockPos?, direction: Direction?): EventResult? {

        if (player == null || interactionHand == InteractionHand.OFF_HAND) {
            return EventResult.pass()
        }

        val playerData = getData(player)
        if (parseAbilityFromIndex(player, playerData.abilityIndex)) {
            return EventResult.interruptTrue()
        }

        return EventResult.pass()
    }

    /**
     * Helper function to trigger abilities used
     */
    fun parseAbilityFromIndex(player: Player, abilityIndex: Int): Boolean {
        if (abilityIndex == VampireAbility.TRANSFIX.ordinal && player.isShiftKeyDown) {
            VampireAbilities.toggleNightVision(player)
            return true
        } else if (abilityIndex == VampireAbility.SPEED.ordinal) {
            VampireAbilities.toggleSpeedBoost(player)
            return true
        } else if (abilityIndex == VampireAbility.BAT_FORM.ordinal) {
            val isBta = TransformationPlayerAttachment.isBat(player)
            if (isBta) {
                TransformationPlayerAttachment.removeForm(player)
            } else {
                TransformationPlayerAttachment.setBatForm(player)
            }
            return true
        }
        return false
    }

    /**
     * Used by the Creatures of the Night Ritual, killing a chicken over a sacrificial circle yields blood to a wine glass
     */
    private fun killChicken(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is Chicken && damageSource?.entity is Player) {
            val player = damageSource.entity as Player

            if (player.mainHandItem.`is`(WitcheryItems.ARTHANA.get()) && player.offhandItem.`is`(WitcheryItems.WINE_GLASS.get())) {

                val possibleSkull = BlockPos.betweenClosedStream(livingEntity.boundingBox.inflate(2.0))

                var shouldPerform = false
                for (skullPos in possibleSkull) {
                    val skullState = livingEntity.level().getBlockState(skullPos)

                    if (skullState.`is`(WitcheryBlocks.SACRIFICIAL_CIRCLE.get())) {
                        shouldPerform = true
                        break
                    }
                }

                if (shouldPerform) {
                    player.offhandItem.shrink(1)
                    val stackCopy = player.offhandItem.copy()
                    if (!stackCopy.isEmpty) {
                        Containers.dropItemStack(livingEntity.level(), player.x, player.y, player.z, stackCopy)
                    }

                    val bloodWine = WitcheryItems.WINE_GLASS.get().defaultInstance
                    bloodWine.set(WitcheryDataComponents.CHICKEN_BLOOD.get(), true)
                    bloodWine.set(WitcheryDataComponents.BLOOD.get(), livingEntity.uuid)
                    player.setItemInHand(InteractionHand.OFF_HAND, bloodWine)
                }
            }
        }

        return EventResult.pass()
    }

    /**
     * Converts the skull and chalk to a sacrificial structure
     */
    fun makeSacrificialCircle(player: Player, blockPos: BlockPos): EventResult? {
        val pieces = SacrificialBlock.STRUCTURE.get().structurePieces

        pieces.forEach(Consumer { s: StructurePiece ->
            s.place(
                blockPos,
                player.level()
            )
        })

        player.level().setBlockAndUpdate(
            blockPos,
            WitcheryBlocks.SACRIFICIAL_CIRCLE.get().defaultBlockState()
        )

        if (player.level().getBlockEntity(blockPos) is MultiBlockComponentBlockEntity) {
            (player.level().getBlockEntity(blockPos) as MultiBlockComponentBlockEntity).corePos = blockPos
        }

        return EventResult.pass()
    }

    /**
     * If the vampire-player is level 3 this will count towards the requirement to level up to level 4
     */
    private fun tickNightsCount(player: Player?) {
        if (player is ServerPlayer) {
            VampireLeveling.increaseNightTicker(player)
        }
    }

    /**
     * If the player dies the night counter will reset
     */
    private fun resetNightCount(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is Player && getData(livingEntity).vampireLevel == 3) {
            VampireLeveling.resetNightCounter(livingEntity)
        }

        return EventResult.pass()
    }

    /**
     * When a blaze is killed is this tries to level up a vampire-player
     */
    private fun killBlaze(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is Blaze && damageSource?.entity is ServerPlayer) {
            val player = damageSource.entity as ServerPlayer
            VampireLeveling.increaseKilledBlazes(player)
        }

        return EventResult.pass()
    }

    fun registerEvents() {
        TickEvent.PLAYER_PRE.register(VampireEventHandler::tickNightsCount)
        EntityEvent.LIVING_DEATH.register(VampireEventHandler::resetNightCount)
        InteractionEvent.INTERACT_ENTITY.register(VampireEventHandler::interactEntityWithAbility)
        InteractionEvent.CLIENT_RIGHT_CLICK_AIR.register(VampireEventHandler::clientRightClickAbility)
        InteractionEvent.RIGHT_CLICK_BLOCK.register(VampireEventHandler::rightClickBlockAbility)
        EntityEvent.LIVING_DEATH.register(VampireEventHandler::killChicken)
        EntityEvent.LIVING_DEATH.register(VampireEventHandler::killBlaze)
        TickEvent.PLAYER_PRE.register(VampireEventHandler::tick)
        PlayerEvent.PLAYER_CLONE.register(VampireEventHandler::respawn)
    }
}