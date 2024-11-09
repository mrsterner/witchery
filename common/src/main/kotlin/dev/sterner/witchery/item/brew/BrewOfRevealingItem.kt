package dev.sterner.witchery.item.brew

import dev.sterner.witchery.entity.BansheeEntity
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level

class BrewOfRevealingItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun applyEffectOnEntities(level: Level, livingEntity: LivingEntity) {
        if (livingEntity.hasEffect(MobEffects.INVISIBILITY)) {
            livingEntity.removeEffect(MobEffects.INVISIBILITY)
        }
        if (livingEntity is BansheeEntity) {
            livingEntity.entityData.set(BansheeEntity.REVEALED, true)
        }
    }
}