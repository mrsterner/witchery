package dev.sterner.witchery.block.spirit_portal

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.api.multiblock.MultiBlockHorizontalDirectionStructure
import dev.sterner.witchery.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.Supplier

class SpiritPortalBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(
            HORIZONTAL_FACING,
            blockPlaceContext.horizontalDirection.opposite
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(HORIZONTAL_FACING)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val dir = state.getValue(HORIZONTAL_FACING)
        return when (dir) {
            Direction.NORTH -> {
                NORTH
            }
            Direction.EAST -> {
                EAST
            }
            Direction.WEST -> {
                WEST
            }
            else -> {
                SOUTH
            }
        }
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.SPIRIT_PORTAL.get().create(pos, state)
    }

    companion object {
        val SOUTH: VoxelShape = Shapes.create(0.0, 0.0, 13.0 / 16, 1.0, 1.0, 1.0)
        val EAST: VoxelShape = Shapes.create(13.0 / 16, 0.0, 0.0, 1.0, 1.0, 1.0)
        val WEST: VoxelShape = Shapes.create(0.0, 0.0, 0.0, 3.0 / 16, 1.0, 1.0)
        val NORTH: VoxelShape = Shapes.create(0.0, 0.0, 0.0, 1.0, 1.0, 3.0 / 16)

        val STRUCTURE: Supplier<MultiBlockHorizontalDirectionStructure> =
            Supplier<MultiBlockHorizontalDirectionStructure> {
                (MultiBlockHorizontalDirectionStructure.of(
                    MultiBlockStructure.StructurePiece(
                        1,
                        0,
                        0,
                        WitcheryBlocks.SPIRIT_PORTAL_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        1,
                        1,
                        0,
                        WitcheryBlocks.SPIRIT_PORTAL_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        0,
                        1,
                        0,
                        WitcheryBlocks.SPIRIT_PORTAL_COMPONENT.get().defaultBlockState()
                    ),
                ))
            }
    }
}