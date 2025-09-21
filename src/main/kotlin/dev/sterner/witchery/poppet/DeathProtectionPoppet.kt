package dev.sterner.witchery.poppet

import dev.sterner.witchery.api.PoppetType
import dev.sterner.witchery.api.PoppetUsage
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class DeathProtectionPoppet : PoppetType {
    override val item = WitcheryItems.DEATH_PROTECTION_POPPET.get()

    override fun isValidFor(owner: LivingEntity, source: DamageSource?): Boolean {
        return source == null || !source.`is`(DamageTypeTags.BYPASSES_INVULNERABILITY)
    }

    override fun getDurabilityDamage(usage: PoppetUsage): Int = when (usage) {
        PoppetUsage.PROTECTION -> 1
        else -> 0
    }

    override fun onActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        if (owner is Player) {
            owner.health = 4.0f
            owner.removeAllEffects()
            owner.addEffect(MobEffectInstance(MobEffects.REGENERATION, 900, 1))
            owner.addEffect(MobEffectInstance(MobEffects.ABSORPTION, 100, 1))
            owner.addEffect(MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0))
            owner.playSound(SoundEvents.TOTEM_USE)
            return true
        }
        return false
    }

    override fun onCorruptedActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        if (owner is Player) {
            owner.health = 1.0f
            owner.removeAllEffects()

            owner.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 1200, 1))
            owner.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1000, 1))
            owner.addEffect(MobEffectInstance(MobEffects.UNLUCK, 2400, 0))

            owner.addEffect(MobEffectInstance(MobEffects.REGENERATION, 200, 0))

            owner.playSound(SoundEvents.TOTEM_USE, 0.8f, 0.5f)

            owner.displayClientMessage(
                Component.translatable("curse.witchery.corrupt_poppet.death_effect")
                    .withStyle(ChatFormatting.DARK_PURPLE),
                true
            )

            return true
        }
        return false
    }
}