package dev.sterner.witchery.block.coffin

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.BedBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BedPart
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape


class CoffinBlock(properties: Properties, color: DyeColor) : BedBlock(color, properties) {

    companion object {
        val OPEN: BooleanProperty = BlockStateProperties.OPEN
    }

    val SHAPE: VoxelShape = Shapes.box(0.0, 0.0, 0.0, 16.0 / 16, 10.0 / 16, 16.0 / 16)

    init {
        this.registerDefaultState(
            this.stateDefinition.any().setValue(PART, BedPart.FOOT).setValue(OCCUPIED, false).setValue(OPEN, false)
        )
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return CoffinBlockEntity(pos, state)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(OPEN)
        super.createBlockStateDefinition(builder)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (direction == getDirectionTowardsOtherPart(state.getValue(PART), state.getValue(FACING))) {
            return if (neighborState.`is`(this) && neighborState.getValue(PART) !== state.getValue(PART)) state.setValue(
                OCCUPIED, neighborState.getValue(
                    OCCUPIED
                )
            ).setValue(OPEN, neighborState.getValue(OPEN)) else Blocks.AIR.defaultBlockState()
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }


    private fun getDirectionTowardsOtherPart(part: BedPart, direction: Direction): Direction {
        return if (part == BedPart.FOOT) direction else direction.opposite
    }


    //BASE
    override fun setPlacedBy(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pPlacer: LivingEntity?,
        pStack: ItemStack
    ) {
        val be = pLevel.getBlockEntity(pPos)
        if (be is WitcheryBaseBlockEntity) {
            be.onPlace(pPlacer, pStack)
        }
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack)
    }

    override fun useItemOn(
        stack: ItemStack,
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        if (pLevel.isClientSide) {
            return ItemInteractionResult.CONSUME
        } else {
            if (pPlayer.isShiftKeyDown) {
                val state = pState.cycle(OPEN)
                pLevel.setBlockAndUpdate(pPos, state)
                return ItemInteractionResult.SUCCESS
            } else {
                if (pState.getValue(OPEN)) {
                    val state = pState.cycle(OPEN)
                    pLevel.setBlockAndUpdate(pPos, state)
                    return super.useItemOn(stack, pState, pLevel, pPos, pPlayer, hand, hitResult)
                } else {
                    return ItemInteractionResult.CONSUME
                }
            }
        }
    }
}