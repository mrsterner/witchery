package dev.sterner.witchery.content.item.brew

import dev.sterner.witchery.content.entity.AbstractSpectralEntity
import dev.sterner.witchery.content.entity.SpectralPigEntity
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level

class BrewOfRevealingItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun applyEffectOnEntities(level: Level, livingEntity: LivingEntity, hasFrog: Boolean) {
        if (livingEntity.hasEffect(MobEffects.INVISIBILITY)) {
            livingEntity.removeEffect(MobEffects.INVISIBILITY)
        }
        if (livingEntity is AbstractSpectralEntity) {
            livingEntity.entityData.set(AbstractSpectralEntity.REVEALED, true)
        }
        if (livingEntity is SpectralPigEntity) {
            livingEntity.entityData.set(SpectralPigEntity.REVEALED, true)
        }
    }
}