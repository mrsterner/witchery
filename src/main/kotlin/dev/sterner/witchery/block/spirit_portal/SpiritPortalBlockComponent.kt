package dev.sterner.witchery.block.spirit_portal

import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlock
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity

import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlock.Companion.EAST
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlock.Companion.NORTH
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlock.Companion.SOUTH
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlock.Companion.WEST
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlock.Companion.litBlockEmission
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class SpiritPortalBlockComponent(properties: Properties) : MultiBlockComponentBlock(
    properties.noOcclusion().noCollission().lightLevel(
        litBlockEmission(8)
    )
) {

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.OPEN, false)
        )
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(
            HORIZONTAL_FACING,
            blockPlaceContext.horizontalDirection.opposite
        )
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

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        if (state.getValue(BlockStateProperties.OPEN)) {
            return Shapes.empty()
        }
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

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        if (entity is Player && state.getValue(BlockStateProperties.OPEN)) {
            val componentBlockEntity = level.getBlockEntity(pos) as? MultiBlockComponentBlockEntity ?: return
            val corePos = componentBlockEntity.corePos ?: return

            val coreBlockState = level.getBlockState(corePos)
            if (coreBlockState.block is SpiritPortalBlock) {
                val corePortalBlock = coreBlockState.block as SpiritPortalBlock

                val dir = state.getValue(HORIZONTAL_FACING)
                val portalShape = when (dir) {
                    Direction.NORTH -> NORTH
                    Direction.EAST -> EAST
                    Direction.WEST -> WEST
                    Direction.SOUTH -> SOUTH
                    else -> return
                }

                val portalBoundingBox = portalShape.bounds().move(pos)

                if (portalBoundingBox.intersects(entity.boundingBox)) {
                    corePortalBlock.handleEntityInside(level, corePos, entity)
                }
            }
        }

        super.entityInside(state, level, pos, entity)
    }

    override fun neighborChanged(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pBlock: Block,
        pFromPos: BlockPos,
        pIsMoving: Boolean
    ) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving)

        val neighborState = pLevel.getBlockState(pFromPos)
        if (neighborState.block is SpiritPortalBlock || neighborState.block is SpiritPortalBlockComponent) {
            val isOpen = neighborState.getValue(BlockStateProperties.OPEN)

            if (pState.getValue(BlockStateProperties.OPEN) != isOpen) {
                pLevel.setBlock(pPos, pState.setValue(BlockStateProperties.OPEN, isOpen), 3)
            }
        }
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(HORIZONTAL_FACING, BlockStateProperties.OPEN)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }
}