package dev.sterner.witchery.core.api

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.block.brazier.BrazierBlockEntity
import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level

open class BrazierPassive(val id: ResourceLocation) {

    constructor(id: String) : this(Witchery.id(id))

    open fun onTickBrazier(level: Level, pos: BlockPos, blockEntity: BrazierBlockEntity) {

    }

    open fun onStartBrazier(level: Level, blockPos: BlockPos, blockEntity: BrazierBlockEntity): Boolean {
        return true
    }
}