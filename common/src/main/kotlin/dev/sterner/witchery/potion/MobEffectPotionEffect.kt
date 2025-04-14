package dev.sterner.witchery.potion

import dev.sterner.witchery.item.potion.WitcheryPotionEffect
import net.minecraft.core.Holder
import net.minecraft.world.effect.MobEffect

class MobEffectPotionEffect(effectId: String, var mobEffect: Holder<MobEffect>, duration: Int = 20 * 45, amplifier: Int = 0)
    : WitcheryPotionEffect(effectId, duration, amplifier) {
}