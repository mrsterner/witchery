package dev.sterner.witchery.features.brewing.brew

import dev.sterner.witchery.entity.BansheeEntity
import dev.sterner.witchery.entity.SpectralPigEntity
import dev.sterner.witchery.entity.SpectreEntity
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level

class BrewOfRevealingItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun applyEffectOnEntities(level: Level, livingEntity: LivingEntity, hasFrog: Boolean) {
        if (livingEntity.hasEffect(MobEffects.INVISIBILITY)) {
            livingEntity.removeEffect(MobEffects.INVISIBILITY)
        }
        if (livingEntity is BansheeEntity) {
            livingEntity.entityData.set(BansheeEntity.REVEALED, true)
        }
        if (livingEntity is SpectralPigEntity) {
            livingEntity.entityData.set(SpectralPigEntity.REVEALED, true)
        }
        if (livingEntity is SpectreEntity) {
            livingEntity.entityData.set(SpectreEntity.REVEALED, true)
        }
    }
}