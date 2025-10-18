package dev.sterner.witchery.block.ancient_tablet

import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlock
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.block.ancient_tablet.AncientTabletBlock.Companion.SHAPE_BOTTOM_LEFT
import dev.sterner.witchery.block.ancient_tablet.AncientTabletBlock.Companion.SHAPE_BOTTOM_RIGHT
import dev.sterner.witchery.block.ancient_tablet.AncientTabletBlock.Companion.SHAPE_FULL
import dev.sterner.witchery.block.ancient_tablet.AncientTabletBlock.Companion.SHAPE_TOP_LEFT
import dev.sterner.witchery.block.ancient_tablet.AncientTabletBlock.Companion.SHAPE_TOP_RIGHT
import dev.sterner.witchery.util.WitcheryUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class AncientTableCompanionBlock(properties: Properties) :
    MultiBlockComponentBlock(properties.noOcclusion()) {

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        val blockEntity = level.getBlockEntity(pos)
        val direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING)

        val offset = when (blockEntity) {
            is MultiBlockComponentBlockEntity -> blockEntity.structureOffset
            else -> Vec3i.ZERO
        }

        val baseShape = when {
            offset.y == 1 && offset.x == -1 -> SHAPE_TOP_LEFT
            offset.y == 1 && offset.x == 0 -> SHAPE_TOP_RIGHT
            offset.y == 0 && offset.x == -1 -> SHAPE_BOTTOM_LEFT
            offset.y == 0 && offset.x == 0 -> SHAPE_BOTTOM_RIGHT
            else -> SHAPE_FULL
        }


        return WitcheryUtil.rotateShape(Direction.NORTH, direction, baseShape)
    }
}