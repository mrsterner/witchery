package dev.sterner.witchery.client.colors

import dev.sterner.witchery.content.block.ritual.RitualChalkBlock
import dev.sterner.witchery.content.block.sacrificial_circle.SacrificialBlock
import dev.sterner.witchery.registry.WitcheryBlocks
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
        if (block is SacrificialBlock) {
            return WitcheryBlocks.INFERNAL_CHALK_BLOCK.get().color
        }

        return 0xFFFFFF
    }
}