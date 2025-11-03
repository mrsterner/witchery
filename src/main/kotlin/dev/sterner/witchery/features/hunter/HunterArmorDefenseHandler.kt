package dev.sterner.witchery.features.hunter

import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.event.entity.living.MobEffectEvent


object HunterArmorDefenseHandler {

    private const val POTION_DURATION_REDUCTION = 0.5f
    private const val CURSE_DURATION_REDUCTION = 0.4f
    const val POPPET_DAMAGE_REDUCTION = 0.35f
    private const val MAGIC_DAMAGE_REDUCTION = 0.25f
    private const val CURSE_REFLECT_CHANCE = 0.15f

    private const val PARTIAL_BONUS_MULTIPLIER = 0.25f

    fun hasFullHunterSet(player: Player): Boolean {
        return player.inventory.armor[3].item == WitcheryItems.HUNTER_HELMET.get() &&
                player.inventory.armor[2].item == WitcheryItems.HUNTER_CHESTPLATE.get() &&
                player.inventory.armor[1].item == WitcheryItems.HUNTER_LEGGINGS.get() &&
                player.inventory.armor[0].item == WitcheryItems.HUNTER_BOOTS.get()
    }

    fun getHunterArmorPieceCount(player: Player): Int {
        var count = 0
        if (player.getItemBySlot(EquipmentSlot.HEAD) == WitcheryItems.HUNTER_HELMET.get()) count++
        if (player.getItemBySlot(EquipmentSlot.CHEST) == WitcheryItems.HUNTER_CHESTPLATE.get()) count++
        if (player.getItemBySlot(EquipmentSlot.LEGS) == WitcheryItems.HUNTER_LEGGINGS.get()) count++
        if (player.getItemBySlot(EquipmentSlot.FEET) == WitcheryItems.HUNTER_BOOTS.get()) count++
        return count
    }

    fun getProtectionMultiplier(player: Player): Float {
        val pieceCount = getHunterArmorPieceCount(player)
        return if (pieceCount == 4) {
            1.0f
        } else {
            pieceCount * PARTIAL_BONUS_MULTIPLIER
        }
    }

    fun onPotionEffectApplied(event: MobEffectEvent.Added) {
        val entity = event.entity
        if (entity !is Player) return

        val pieceCount = getHunterArmorPieceCount(entity)
        if (pieceCount == 0) return

        val effectInstance = event.effectInstance ?: return

        if (isHarmfulEffect(effectInstance.effect)) {
            val multiplier = getProtectionMultiplier(entity)
            val reduction = POTION_DURATION_REDUCTION * multiplier

            val originalDuration = effectInstance.duration
            val newDuration = (originalDuration * (1.0f - reduction)).toInt()

            entity.removeEffect(effectInstance.effect)

            entity.addEffect(
                MobEffectInstance(
                    effectInstance.effect,
                    newDuration,
                    effectInstance.amplifier,
                    effectInstance.isAmbient,
                    effectInstance.isVisible,
                    effectInstance.showIcon()
                )
            )

            if (entity is ServerPlayer && hasFullHunterSet(entity)) {
                HunterArmorParticleEffects.spawnProtectionParticles(
                    entity,
                    HunterArmorParticleEffects.ProtectionType.POTION_REDUCTION
                )
            }
        }
    }

    private fun isHarmfulEffect(effect: Holder<MobEffect>): Boolean {
        return effect.value().category == MobEffectCategory.HARMFUL
    }

    fun onLivingHurt(event: LivingIncomingDamageEvent) {
        val entity = event.entity
        if (entity !is Player) return

        val pieceCount = getHunterArmorPieceCount(entity)
        if (pieceCount == 0) return

        val source = event.source
        val amount = event.amount

        if (isPoppetDamage(source)) {
            val multiplier = getProtectionMultiplier(entity)
            val reduction = POPPET_DAMAGE_REDUCTION * multiplier
            val newDamage = amount * (1.0f - reduction)

            event.amount = newDamage

            if (entity is ServerPlayer && hasFullHunterSet(entity)) {
                HunterArmorParticleEffects.spawnProtectionParticles(entity, HunterArmorParticleEffects.ProtectionType.POPPET_DAMAGE)
            }
        }

        if (isWitcheryMagicDamage(source)) {
            val multiplier = getProtectionMultiplier(entity)
            val reduction = MAGIC_DAMAGE_REDUCTION * multiplier
            val newDamage = amount * (1.0f - reduction)

            event.amount = newDamage

            if (entity is ServerPlayer && hasFullHunterSet(entity)) {
                HunterArmorParticleEffects.spawnProtectionParticles(entity, HunterArmorParticleEffects.ProtectionType.MAGIC_RESISTANCE)
            }
        }
    }

    private fun isPoppetDamage(source: DamageSource): Boolean {
        val sourceType = source.type()
        return sourceType.msgId.contains("poppet") || sourceType.msgId.contains("voodoo")
    }

    private fun isWitcheryMagicDamage(source: DamageSource): Boolean {
        val attacker = source.entity
        if (attacker == null) {
            val sourceType = source.type()
            val msgId = sourceType.msgId
            return msgId.contains("witchery") || msgId.contains("curse") || msgId.contains("magic")
        }

        val entityType = attacker.type
        val entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType)
        return entityId.namespace == "witchery"
    }

    fun reduceCurseDuration(player: Player, originalDuration: Int): Int {
        val pieceCount = getHunterArmorPieceCount(player)
        if (pieceCount == 0) return originalDuration

        val multiplier = getProtectionMultiplier(player)
        val reduction = CURSE_DURATION_REDUCTION * multiplier
        val newDuration = (originalDuration * (1.0f - reduction)).toInt()

        if (player is ServerPlayer && hasFullHunterSet(player)) {
            HunterArmorParticleEffects.spawnProtectionParticles(player, HunterArmorParticleEffects.ProtectionType.CURSE_REDUCTION)
        }

        return newDuration.coerceAtLeast(20)
    }

    fun tryReflectCurse(player: Player, sourcePlayer: ServerPlayer?): Boolean {
        if (!hasFullHunterSet(player)) return false
        if (sourcePlayer == null) return false

        val random = player.level().random
        if (random.nextFloat() < CURSE_REFLECT_CHANCE) {
            if (player is ServerPlayer) {
                HunterArmorParticleEffects.spawnCurseReflectionEffect(player, sourcePlayer)
                HunterArmorParticleEffects.spawnProtectionParticles(player, HunterArmorParticleEffects.ProtectionType.CURSE_REFLECTION)
            }
            return true
        }

        return false
    }

    fun calculatePoppetDamage(victim: Player, originalDamage: Float): Float {
        val pieceCount = getHunterArmorPieceCount(victim)
        if (pieceCount == 0) return originalDamage

        val multiplier = getProtectionMultiplier(victim)
        val reduction = POPPET_DAMAGE_REDUCTION * multiplier

        return originalDamage * (1.0f - reduction)
    }

    fun modifyDamage(player: Player, source: DamageSource, damage: Float): Float {
        var modifiedDamage = damage

        if (isPoppetDamage(source)) {
            modifiedDamage = calculatePoppetDamage(player, modifiedDamage)
        }

        if (isWitcheryMagicDamage(source)) {
            val multiplier = getProtectionMultiplier(player)
            val reduction = MAGIC_DAMAGE_REDUCTION * multiplier
            modifiedDamage *= (1.0f - reduction)
        }

        return modifiedDamage
    }
}