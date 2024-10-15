package dev.sterner.witchery.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

class WaterArtichokeCropBlock(properties: Properties) : WitcheryCropBlock(properties) {

    override fun mayPlaceOn(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean {
        return state.`is`(Blocks.WATER)
    }

    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        if (level.getRawBrightness(pos, 0) >= 9) {
            val i = this.getAge(state)
            if (i < this.maxAge) {
                val f = getWaterGrowthSpeed(this, level, pos)
                if (random.nextInt((25.0f / f).toInt() + 1) == 0) {
                    level.setBlock(pos, this.getStateForAge(i + 1), 2)
                }
            }
        }
    }

    private fun getWaterGrowthSpeed(block: Block, level: BlockGetter, pos: BlockPos): Float {
        val f = 10f
        return f
    }
}