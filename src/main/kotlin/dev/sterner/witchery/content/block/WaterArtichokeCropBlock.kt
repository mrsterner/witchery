package dev.sterner.witchery.content.block

import dev.sterner.witchery.item.WaterCropBlockItem
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class WaterArtichokeCropBlock(properties: Properties) : WitcheryCropBlock(properties.noCollission()) {

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

    override fun getBaseSeedId(): WaterCropBlockItem = WitcheryItems.WATER_ARTICHOKE_SEEDS.get()

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return CUSTOM_SHAPE_BY_AGE[state.getValue(AGE)]
    }

    companion object {
        val CUSTOM_SHAPE_BY_AGE = arrayOf(
            box(4.0, 0.0, 4.0, 12.0, 2.0, 12.0),
            box(4.0, 0.0, 4.0, 12.0, 5.0, 12.0),
            box(4.0, 0.0, 4.0, 12.0, 9.0, 12.0),
            box(4.0, 0.0, 4.0, 12.0, 13.0, 12.0),
            box(4.0, 0.0, 4.0, 12.0, 15.0, 12.0)
        )
    }
}