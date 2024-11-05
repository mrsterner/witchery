package dev.sterner.witchery.client.colors

import dev.sterner.witchery.block.ritual.RitualChalkBlock
import net.minecraft.client.color.block.BlockColor
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState

object RitualChalkColors : BlockColor {

    override fun getColor(
        blockState: BlockState,
        blockAndTintGetter: BlockAndTintGetter?,
        blockPos: BlockPos?,
        i: Int
    ): Int {
        val block = blockState.block
        if (block is RitualChalkBlock) {
            return block.color
        }

        return 0xFFFFFF
    }
}