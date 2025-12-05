package dev.sterner.witchery.content.mob_effect

import dev.sterner.witchery.features.affliction.werewolf.WerewolfSpecificEventHandler
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity

class LycanthropyMobEffect(category: MobEffectCategory, color: Int) :
    MobEffect(category, color) {

    override fun shouldApplyEffectTickThisTick(duration: Int, amplifier: Int): Boolean {
        return duration == 1
    }

    override fun applyEffectTick(
        livingEntity: LivingEntity,
        amplifier: Int
    ): Boolean {
        if (livingEntity is ServerPlayer) {
            WerewolfSpecificEventHandler.infectPlayer(livingEntity)
        }
        return super.applyEffectTick(livingEntity, amplifier)
    }
}