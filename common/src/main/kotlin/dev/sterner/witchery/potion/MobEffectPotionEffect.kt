package dev.sterner.witchery.potion

import dev.sterner.witchery.item.potion.WitcheryPotionEffect
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import net.minecraft.core.Holder
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity

class MobEffectPotionEffect(effectId: String, var mobEffect: Holder<MobEffect>, duration: Int = 20 * 45, amplifier: Int = 0)
    : WitcheryPotionEffect(effectId, duration, amplifier) {

    override fun affectEntity(livingEntity: LivingEntity, activeIngredient: WitcheryPotionIngredient) {
        livingEntity.addEffect(MobEffectInstance(mobEffect, duration, amplifier))
    }
}