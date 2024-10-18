package dev.sterner.witchery.block

import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class BelladonnaCropBlock(properties: Properties) : WitcheryCropBlock(properties) {
    override fun getBaseSeedId() = WitcheryItems.BELLADONNA_SEEDS.get()

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return CUSTOM_SHAPE_BY_AGE[state.getValue(AGE)]
    }

    companion object {
        val CUSTOM_SHAPE_BY_AGE = arrayOf(
            box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 11.0, 16.0)
        )
    }
}