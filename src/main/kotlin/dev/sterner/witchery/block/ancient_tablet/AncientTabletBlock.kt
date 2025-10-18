package dev.sterner.witchery.block.ancient_tablet


import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlock
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.api.multiblock.MultiBlockHorizontalDirectionStructure
import dev.sterner.witchery.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.util.WitcheryUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.Supplier

class AncientTabletBlock(properties: Properties) : MultiBlockComponentBlock(properties.noOcclusion().noCollission()) {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return AncientTabletBlockEntity(pos, state)
    }

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            blockPlaceContext.horizontalDirection.opposite
        )
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
            is MultiBlockCoreEntity -> blockEntity.structureOffset
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

    companion object {
        val SHAPE_FULL = Shapes.create(0.0 / 16, 0.0, 4.0 / 16, 16.0 / 16, 16.0 / 16, 12.0 / 16)

        val SHAPE_TOP_LEFT = Shapes.create(4.0 / 16, 0.0, 4.0 / 16, 16.0 / 16, 8.0 / 16, 12.0 / 16)
        val SHAPE_TOP_RIGHT = Shapes.create(0.0 / 16, 0.0, 4.0 / 16, 12.0 / 16, 8.0 / 16, 12.0 / 16)

        val SHAPE_BOTTOM_LEFT = Shapes.create(4.0 / 16, 0.0, 4.0 / 16, 16.0 / 16, 16.0 / 16, 12.0 / 16)
        val SHAPE_BOTTOM_RIGHT = Shapes.create(0.0 / 16, 0.0, 4.0 / 16, 12.0 / 16, 16.0 / 16, 12.0 / 16)

        val STRUCTURE: Supplier<MultiBlockHorizontalDirectionStructure> =
            Supplier<MultiBlockHorizontalDirectionStructure> {
                (MultiBlockHorizontalDirectionStructure.of(
                    MultiBlockStructure.StructurePiece(
                        -1,
                        0,
                        0,
                        WitcheryBlocks.ANCIENT_TABLET_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        0,
                        1,
                        0,
                        WitcheryBlocks.ANCIENT_TABLET_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        -1,
                        1,
                        0,
                        WitcheryBlocks.ANCIENT_TABLET_COMPONENT.get().defaultBlockState()
                    )
                ))
            }
    }
}