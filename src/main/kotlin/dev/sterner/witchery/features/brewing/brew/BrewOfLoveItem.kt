package dev.sterner.witchery.features.brewing.brew

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.level.Level

class BrewOfLoveItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun applyEffectOnEntities(level: Level, livingEntity: LivingEntity, hasFrog: Boolean) {
        if (livingEntity is Animal) {
            livingEntity.setInLove(null)
        }
    }
}