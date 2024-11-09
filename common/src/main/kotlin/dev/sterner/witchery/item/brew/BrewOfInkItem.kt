package dev.sterner.witchery.item.brew

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class BrewOfInkItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun applyEffectOnEntities(level: Level, livingEntity: LivingEntity) {
        livingEntity.addEffect(MobEffectInstance(MobEffects.BLINDNESS, 20 * 8, 1))
        if (livingEntity !is Player) {
            livingEntity.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 8, 2))
        }
    }
}