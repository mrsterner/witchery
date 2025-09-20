package dev.sterner.witchery.block.oven


import dev.sterner.witchery.block.MultiBlockHorizontalDirectionStructure
import dev.sterner.witchery.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.block.cauldron.CauldronBlock.Companion.litBlockEmission
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
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
import team.lodestar.lodestone.systems.multiblock.MultiBlockStructure
import java.util.function.Supplier

open class OvenFumeExtensionBlock(properties: Properties) : WitcheryBaseEntityBlock(
    properties.noOcclusion().lightLevel(
        litBlockEmission(8)
    )
) {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.OVEN_FUME_EXTENSION.get().create(pos, state)
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

        // Check if this block is placed next to an oven
        val (canSurvive, isOvenRight) = checkOvenPlacement(pState, pLevel, pPos)

        if (canSurvive) {
            updateLit(pLevel, isOvenRight, pPos, pState)
        }
    }

    fun updateLit(pLevel: Level, isOvenRight: Boolean?, pPos: BlockPos, pState: BlockState) {
        val ovenPos =
            if (isOvenRight == true) pPos.relative(pState.getValue(BlockStateProperties.HORIZONTAL_FACING).clockWise)
            else pPos.relative(pState.getValue(BlockStateProperties.HORIZONTAL_FACING).counterClockWise)

        val ovenState = pLevel.getBlockState(ovenPos)

        // Check if the oven is lit and set this block's LIT state accordingly
        val isOvenLit = ovenState.getValue(BlockStateProperties.LIT)
        pLevel.setBlock(pPos, pState.setValue(BlockStateProperties.LIT, isOvenLit), 3)
    }

    private fun checkOvenPlacement(state: BlockState, level: LevelReader, pos: BlockPos): Pair<Boolean, Boolean?> {
        // Get the facing direction of this block
        val blockFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING)

        // Check positions on the left and right of the current block
        val leftPos = pos.relative(blockFacing.counterClockWise)
        val rightPos = pos.relative(blockFacing.clockWise)

        // Check if there's an oven at either of those positions
        val leftState = level.getBlockState(leftPos)
        val rightState = level.getBlockState(rightPos)

        val isOvenLeft = leftState.block is OvenBlock
        val isOvenRight = rightState.block is OvenBlock

        // Return false if the block isn't next to an oven
        if (!isOvenLeft && !isOvenRight) {
            return Pair(false, null)
        }

        // Get the oven's facing direction and compare it with this block's facing
        val ovenFacing =
            if (isOvenLeft) leftState.getValue(BlockStateProperties.HORIZONTAL_FACING) else rightState.getValue(
                BlockStateProperties.HORIZONTAL_FACING
            )

        // Return true if the facings match, and also return whether the oven is on the right (ALT)
        return Pair(blockFacing == ovenFacing, isOvenRight)
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        val (canSurvive, _) = checkOvenPlacement(state, level, pos)
        return canSurvive
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, movedByPiston: Boolean) {
        super.onPlace(state, level, pos, oldState, movedByPiston)

        val (canSurvive, isOvenRight) = checkOvenPlacement(state, level, pos)

        if (canSurvive) {
            // Set ALT based on whether the oven is on the right
            updateLit(
                level,
                isOvenRight,
                pos,
                state.setValue(OvenFumeExtensionBlockComponent.ALT, isOvenRight == false)
            )
        }
    }

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.LIT, false)
                .setValue(OvenFumeExtensionBlockComponent.ALT, false)
        )
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            blockPlaceContext.horizontalDirection.opposite
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(
            BlockStateProperties.HORIZONTAL_FACING,
            BlockStateProperties.LIT,
            OvenFumeExtensionBlockComponent.ALT
        )
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    companion object {
        val STRUCTURE: Supplier<MultiBlockHorizontalDirectionStructure> =
            Supplier<MultiBlockHorizontalDirectionStructure> {
                (MultiBlockHorizontalDirectionStructure.of(
                    MultiBlockStructure.StructurePiece(
                        0,
                        1,
                        0,
                        WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION_COMPONENT.get().defaultBlockState()
                    )
                ))
            }

        val shape = box(3.0, 0.0, 3.0, 13.0, 3.0, 13.0)
        val shape2 = box(3.0, 12.0, 3.0, 13.0, 15.0, 13.0)
        val smallShape = box(5.0, 14.0, 5.0, 11.0, 15.0, 11.0)
        val bigShape = box(5.0, 3.0, 5.0, 11.0, 15.0, 11.0)
        val SHAPE: VoxelShape = Shapes.join(
            Shapes.join(shape, bigShape, BooleanOp.OR),
            Shapes.join(shape2, smallShape, BooleanOp.OR),
            BooleanOp.OR
        )
    }
}