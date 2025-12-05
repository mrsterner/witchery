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

    fun getReducedEffectDuration(player: Player, effectInstance: MobEffectInstance): Int? {
        val count = getHunterArmorPieceCount(player)
        if (count == 0) return null

        if (effectInstance.effect.value().category == MobEffectCategory.HARMFUL) {
            val durationModifier = 1.0f - ((count * 0.25f) * POTION_DURATION_REDUCTION)
            val modifiedDuration = (effectInstance.duration * durationModifier).toInt()

            if (modifiedDuration < effectInstance.duration) {
                return modifiedDuration.coerceAtLeast(1)
            }
        }

        return null
    }
}