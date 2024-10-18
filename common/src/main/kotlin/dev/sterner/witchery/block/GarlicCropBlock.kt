package dev.sterner.witchery.block

import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class GarlicCropBlock(properties: Properties) : WitcheryCropBlock(properties) {
    override fun getBaseSeedId() = WitcheryItems.GARLIC.get()

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return CUSTOM_SHAPE_BY_AGE[state.getValue(AGE)]
    }

    companion object {
        val CUSTOM_SHAPE_BY_AGE = arrayOf(
            box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0)
        )
    }
}