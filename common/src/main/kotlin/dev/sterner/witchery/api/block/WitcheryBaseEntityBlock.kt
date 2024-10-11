package dev.sterner.witchery.api.block

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult


abstract class WitcheryBaseEntityBlock(properties: Properties) : Block(properties), EntityBlock {

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker { _, pos, _, blockEntity ->
            if (blockEntity is WitcheryBaseBlockEntity) {
                blockEntity.tick(level, pos, state)
            }
        }
    }

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

    override fun useWithoutItem(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHitResult: BlockHitResult
    ): InteractionResult {
        val be = pLevel.getBlockEntity(pPos)
        if (be is WitcheryBaseBlockEntity) {
            val earlyResult = be.onUseWithoutItem(pPlayer)
            return if (earlyResult.consumesAction()) earlyResult else be.onUse(
                pPlayer,
                InteractionHand.MAIN_HAND
            ).result()
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult)
    }

    override fun useItemOn(
        pStack: ItemStack,
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHitResult: BlockHitResult
    ): ItemInteractionResult {
        val be = pLevel.getBlockEntity(pPos)
        if (be is WitcheryBaseBlockEntity) {
            val earlyResult = be.onUseWithItem(pPlayer, pStack, pHand)
            return if (earlyResult.consumesAction()) earlyResult else be.onUse(pPlayer, pHand)
        }
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult)
    }

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        onBlockBroken(state, level, pos, player)
        return super.playerWillDestroy(level, pos, state, player)
    }

    private fun onBlockBroken(state: BlockState, level: BlockGetter, pos: BlockPos, player: Player) {
        val be = level.getBlockEntity(pos)
        if (be is WitcheryBaseBlockEntity) {
            be.onBreak(player)
        }
    }

    public override fun neighborChanged(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pBlock: Block,
        pFromPos: BlockPos,
        pIsMoving: Boolean
    ) {
        val be = pLevel.getBlockEntity(pPos)
        if (be is WitcheryBaseBlockEntity) {
            be.onNeighborUpdate(pState, pPos, pFromPos)
        }
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving)
    }
}