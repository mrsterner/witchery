package dev.sterner.witchery.api

import dev.sterner.witchery.Witchery
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult

open class SpecialPotion(val id: ResourceLocation) {

    constructor(id: String) : this(Witchery.id(id))

    open fun onActivated(level: Level, owner: Entity?, hitResult: HitResult?, list: MutableList<Entity>) {

    }


}