package dev.sterner.witchery.features.hunter

import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.event.entity.living.MobEffectEvent

object HunterArmorDefenseHandler {

    private const val POTION_DURATION_REDUCTION = 0.5f
    private const val CURSE_DURATION_REDUCTION = 0.4f
    const val MAGIC_DAMAGE_REDUCTION = 0.25f
    private const val CURSE_REFLECT_CHANCE = 0.15f


    fun getHunterArmorPieceCount(player: Player): Int {
        var count = 0
        if (player.getItemBySlot(EquipmentSlot.HEAD).item == WitcheryItems.HUNTER_HELMET.get()) count++
        if (player.getItemBySlot(EquipmentSlot.CHEST).item == WitcheryItems.HUNTER_CHESTPLATE.get()) count++
        if (player.getItemBySlot(EquipmentSlot.LEGS).item == WitcheryItems.HUNTER_LEGGINGS.get()) count++
        if (player.getItemBySlot(EquipmentSlot.FEET).item == WitcheryItems.HUNTER_BOOTS.get()) count++
        return count
    }

    fun getProtectionMultiplier(player: Player): Float {
        val pieceCount = getHunterArmorPieceCount(player)
        return (pieceCount * 0.25f) * 0.5f
    }

    fun reduceCurseDuration(player: Player, originalDuration: Int): Int {
        val pieceCount = getHunterArmorPieceCount(player)
        if (pieceCount == 0) return originalDuration

        val multiplier = getProtectionMultiplier(player)
        val reduction = CURSE_DURATION_REDUCTION * multiplier
        val newDuration = (originalDuration * (1.0f - reduction)).toInt()

        HunterArmorParticleEffects.spawnProtectionParticles(
            player,
            HunterArmorParticleEffects.ProtectionType.CURSE_REDUCTION
        )

        return newDuration.coerceAtLeast(20)
    }

    fun tryReflectCurse(player: Player, sourcePlayer: ServerPlayer?): Boolean {
        if (getHunterArmorPieceCount(player) != 4) return false
        if (sourcePlayer == null) return false

        val random = player.level().random
        if (random.nextFloat() < CURSE_REFLECT_CHANCE) {
            if (player is ServerPlayer) {
                HunterArmorParticleEffects.spawnCurseReflectionEffect(player, sourcePlayer)
                HunterArmorParticleEffects.spawnProtectionParticles(
                    player,
                    HunterArmorParticleEffects.ProtectionType.CURSE_REFLECTION
                )
            }
            return true
        }

        return false
    }

    fun onPotionEffectApplied(event: MobEffectEvent.Added) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val count = getHunterArmorPieceCount(player)

            if (count > 0) {

                val effectInstance = event.effectInstance ?: return

                if (effectInstance.effect.value().category == MobEffectCategory.HARMFUL) {
                    val durationModifier = (count * 0.25) * POTION_DURATION_REDUCTION

                    val originalDuration = effectInstance.duration

                    player.removeEffect(effectInstance.effect)
                    player.addEffect(
                        MobEffectInstance(
                            effectInstance.effect,
                            (originalDuration * durationModifier).toInt(),
                            effectInstance.amplifier,
                            effectInstance.isAmbient,
                            effectInstance.isVisible,
                            effectInstance.showIcon()
                        )
                    )

                    HunterArmorParticleEffects.spawnProtectionParticles(
                        player,
                        HunterArmorParticleEffects.ProtectionType.POTION_REDUCTION
                    )
                }
            }
        }
    }
}