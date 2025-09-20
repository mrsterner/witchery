package dev.sterner.witchery.api

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.ritual.GoldenChalkBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level

open class Ritual(val id: ResourceLocation) {

    constructor(id: String) : this(Witchery.id(id))

    /**
     * Runs once every tick if the ritual has a tick time of more than 0
     */
    open fun onTickRitual(level: Level, pos: BlockPos, goldenChalkBlockEntity: GoldenChalkBlockEntity) {

    }

    /**
     * Runs once when the ritual is started
     */
    open fun onStartRitual(level: Level, blockPos: BlockPos, goldenChalkBlockEntity: GoldenChalkBlockEntity) {

    }

    /**
     * Runs once when the ritual has ended
     */
    open fun onEndRitual(level: Level, blockPos: BlockPos, goldenChalkBlockEntity: GoldenChalkBlockEntity) {

    }
}