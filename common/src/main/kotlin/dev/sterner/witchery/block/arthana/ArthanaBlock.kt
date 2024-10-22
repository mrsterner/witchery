package dev.sterner.witchery.block.arthana

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class ArthanaBlock(properties: Properties) : WitcheryBaseEntityBlock(properties.noCollission().noOcclusion().noLootTable().instabreak()) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = ArthanaBlockEntity(pos, state)

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val be = level.getBlockEntity(pos)
        if (!level.isClientSide && be is ArthanaBlockEntity) {
            val arthana = be.arthana.copy()
            player.setItemInHand(InteractionHand.MAIN_HAND, arthana)
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
        }

        return InteractionResult.SUCCESS
    }

    override fun playerDestroy(
        level: Level,
        player: Player,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        tool: ItemStack
    ) {
        if (!level.isClientSide && blockEntity is ArthanaBlockEntity) {
            val item = ItemEntity(level, pos.center.x, pos.center.y, pos.center.z, blockEntity.arthana.copy())
            level.addFreshEntity(item)
        }

        super.playerDestroy(level, player, pos, state, blockEntity, tool)
    }

    override fun canBeReplaced(state: BlockState, fluid: Fluid): Boolean {
        return false
    }

    override fun canBeReplaced(state: BlockState, useContext: BlockPlaceContext): Boolean {
        return false
    }

    override fun getCloneItemStack(level: LevelReader, pos: BlockPos, state: BlockState): ItemStack {
        val be = level.getBlockEntity(pos)
        if (be is ArthanaBlockEntity)
            return be.arthana.copy()
        return WitcheryItems.ARTHANA.get().defaultInstance
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.box(2.0 / 16, 0.0, 2.0 / 16, 14.0 / 16, 1.0 / 16, 14.0 / 16)
    }
}