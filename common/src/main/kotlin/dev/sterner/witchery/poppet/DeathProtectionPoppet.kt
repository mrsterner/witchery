package dev.sterner.witchery.poppet

import dev.sterner.witchery.handler.poppet.PoppetType
import dev.sterner.witchery.handler.poppet.PoppetUsage
import dev.sterner.witchery.registry.WitcheryItems
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

    override fun getDurabilityDamage(usage: PoppetUsage): Int = 1
}