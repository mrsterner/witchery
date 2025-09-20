package dev.sterner.witchery.handler.affliction

import dev.architectury.event.EventResult
import dev.sterner.witchery.api.event.VampireEvent
import dev.sterner.witchery.api.interfaces.VillagerTransfix
import dev.sterner.witchery.data.BloodPoolReloadListener
import dev.sterner.witchery.handler.BloodPoolHandler
import dev.sterner.witchery.payload.SpawnBloodParticlesS2CPayload
import dev.sterner.witchery.platform.WitcheryAttributes
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks

object AfflictionHandler {

    private const val DAMAGE_DISTRIBUTION = 0.75f

    private const val POISON_CHANCE_BAD_BLOOD = 0.15f
    private const val POISON_EFFECT_DURATION = 200
    private const val VILLAGER_DAMAGE_AMOUNT = 2f

    private const val CAGE_DETECTION_RANGE = 2.0
    private const val MIN_CAGE_BARS = 24
    private const val BLOOD_HEALING_THRESHOLD = 75
    private const val BLOOD_TRANSFER_AMOUNT_BASE = 10

    /**
     * When the vampire-player gets hurt, this handles damage distribution between hearts and blood.
     * The current distribution is declared by damageDistribution
     * If the player is level 0, aka a human, this will just return the default value
     */
    fun handleHurt(player: LivingEntity, damageSource: DamageSource, original: Float): Float {
        if (player !is Player) return original

        val level = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.VAMPIRISM)

        if (level < 1) return original

        val bloodData = BloodPoolLivingEntityAttachment.getData(player)

        if (bloodData.bloodPool > 0) {
            val bloodPerHealthPoint = BLOOD_HEALING_THRESHOLD
            val maxBloodAbsorbableDamage = bloodData.bloodPool / bloodPerHealthPoint

            val absorbableDamage = minOf(original * DAMAGE_DISTRIBUTION, maxBloodAbsorbableDamage.toFloat())
            val bloodRequired = (absorbableDamage * bloodPerHealthPoint).toInt()

            BloodPoolLivingEntityAttachment.setData(
                player,
                bloodData.copy(bloodPool = bloodData.bloodPool - bloodRequired)
            )

            return original * (1 - DAMAGE_DISTRIBUTION)
        }

        return original
    }

    fun vampireDrinkBloodAbility(
        player: ServerPlayer,
        entity: LivingEntity,
        playerBloodData: BloodPoolLivingEntityAttachment.Data
    ): EventResult? {
        val targetData = BloodPoolLivingEntityAttachment.getData(entity)
        val quality = BloodPoolReloadListener.BLOOD_PAIR[entity.type] ?: 0

        if (playerBloodData.bloodPool >= playerBloodData.maxBlood ||
            targetData.bloodPool < 0 ||
            targetData.maxBlood <= 0
        ) {
            return EventResult.interruptFalse()
        }

        val eventResult = VampireEvent.ON_BLOOD_DRINK.invoker().invoke(player, entity)
        if (eventResult == EventResult.interruptFalse()) {
            return EventResult.interruptFalse()
        }

        if (quality == 0 && player.level().random.nextFloat() < POISON_CHANCE_BAD_BLOOD) {
            player.addEffect(MobEffectInstance(MobEffects.POISON, POISON_EFFECT_DURATION, 0))
        }

        val targetHalfBlood = targetData.maxBlood / 2

        if (targetData.bloodPool <= targetHalfBlood && !player.isShiftKeyDown && entity is Villager) {
            handleVillagerHalfBlood(player, entity)
        }

        if (targetData.bloodPool <= targetHalfBlood && !player.isShiftKeyDown) {
            return EventResult.pass()
        }

        playBloodDrinkingEffects(player, entity)

        transferBlood(player, entity)

        handleTargetDamage(player, entity, targetData)

        return EventResult.interruptFalse()
    }

    /**
     * Handles damage to blood-drained entities
     */
    private fun handleTargetDamage(
        player: ServerPlayer,
        entity: LivingEntity,
        targetData: BloodPoolLivingEntityAttachment.Data
    ) {
        val targetHalfBlood = targetData.maxBlood / 2

        val shouldHurt = when {
            entity is Villager && entity is VillagerTransfix &&
                    !entity.isSleeping && !entity.`witchery$isTransfixed`() -> true

            targetData.bloodPool < targetHalfBlood -> true
            else -> false
        }

        if (shouldHurt) {
            if (entity is Villager && AfflictionPlayerAttachment.getData(player).getVillagersHalfBlood().contains(entity.uuid)) {
                VampireLeveling.removeVillagerHalfBlood(player, entity)
                VampireLeveling.removeTrappedVillager(player, entity)
            }
            entity.hurt(player.damageSources().playerAttack(player), VILLAGER_DAMAGE_AMOUNT)
        }

        if (targetData.bloodPool <= 0) {
            if (entity is Player) {
                entity.hurt(player.damageSources().playerAttack(player), VILLAGER_DAMAGE_AMOUNT)
            } else {
                entity.kill()
            }
        }
    }

    /**
     * Transfers blood between entity and player
     */
    private fun transferBlood(player: ServerPlayer, entity: LivingEntity) {
        val attribute = player.getAttribute(WitcheryAttributes.VAMPIRE_DRINK_SPEED)?.value?.toInt() ?: 0
        val modifiedAmount = BLOOD_TRANSFER_AMOUNT_BASE + attribute

        BloodPoolHandler.decreaseBlood(entity, modifiedAmount)
        BloodPoolHandler.increaseBlood(player, modifiedAmount)
    }

    /**
     * Plays blood drinking visual and sound effects
     */
    private fun playBloodDrinkingEffects(player: ServerPlayer, entity: LivingEntity) {
        player.level().playSound(
            null,
            entity.x,
            entity.y,
            entity.z,
            SoundEvents.HONEY_DRINK,
            SoundSource.PLAYERS
        )

        val particlePosition = entity.position().add(0.5, 0.5, 0.5)
        WitcheryPayloads.sendToPlayers(
            player.level(),
            SpawnBloodParticlesS2CPayload(player, particlePosition)
        )
    }

    /**
     * Handles special vampire leveling logic for half-drained villagers
     */
    private fun handleVillagerHalfBlood(player: ServerPlayer, villager: Villager) {
        VampireLeveling.increaseVillagersHalfBlood(player, villager)

        val cageStates = villager.level().getBlockStates(
            villager.boundingBox.inflate(CAGE_DETECTION_RANGE, CAGE_DETECTION_RANGE, CAGE_DETECTION_RANGE)
        )
        val bars = cageStates.filter {
            it.`is`(Blocks.IRON_BARS) || it.`is`(BlockTags.SLABS)
        }.count()

        if (bars >= MIN_CAGE_BARS) {
            VampireLeveling.increaseTrappedVillagers(player, villager)
        }
    }

}