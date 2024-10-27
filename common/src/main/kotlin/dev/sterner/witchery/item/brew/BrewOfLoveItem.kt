package dev.sterner.witchery.item.brew

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult

class BrewOfLoveItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun applyEffect(level: Level, livingEntity: LivingEntity?, result: HitResult) {
        if (livingEntity is Animal) {
            livingEntity.setInLove(null)
        }
    }
}