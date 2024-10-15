package dev.sterner.witchery.api

import dev.sterner.witchery.block.ritual.GoldenChalkBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level

open class Ritual(val id: ResourceLocation) {

    fun tick(level: Level, pos: BlockPos, goldenChalkBlockEntity: GoldenChalkBlockEntity) {

    }
}