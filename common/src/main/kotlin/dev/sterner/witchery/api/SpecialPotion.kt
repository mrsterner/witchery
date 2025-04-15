package dev.sterner.witchery.api

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult

open class SpecialPotion(val id: ResourceLocation) {

    constructor(id: String) : this(Witchery.id(id))

    open fun onActivated(
        level: Level,
        owner: Entity?,
        hitResult: HitResult,
        list: MutableList<Entity>,
        mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier
    ) {

    }

    fun getBox(hitResult: HitResult, mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier): AABB {
        return AABB.ofSize(hitResult.location,
            4 * mergedDispersalModifier.rangeModifier.toDouble(),
            2 * mergedDispersalModifier.rangeModifier.toDouble(),
            4 * mergedDispersalModifier.rangeModifier.toDouble()
        )
    }

}