package dev.sterner.witchery.content.block

import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class WolfsbaneCropBlock(properties: Properties) : WitcheryCropBlock(properties) {

    override fun getBaseSeedId(): ItemNameBlockItem = WitcheryItems.WOLFSBANE_SEEDS.get()

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return CUSTOM_SHAPE_BY_AGE[state.getValue(AGE)]
    }

    companion object {
        val CUSTOM_SHAPE_BY_AGE = arrayOf(
            box(4.0, 0.0, 4.0, 12.0, 2.0, 12.0),
            box(4.0, 0.0, 4.0, 12.0, 7.0, 12.0),
            box(4.0, 0.0, 4.0, 12.0, 9.0, 12.0),
            box(4.0, 0.0, 4.0, 12.0, 13.0, 12.0),
            box(4.0, 0.0, 4.0, 12.0, 15.0, 12.0)
        )
    }
}