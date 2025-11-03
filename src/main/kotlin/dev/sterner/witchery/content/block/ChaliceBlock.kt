package dev.sterner.witchery.content.block

import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class ChaliceBlock(properties: Properties) : Block(properties.noOcclusion()) {
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder.add(HAS_SOUP))
    }

    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack> {
        return super.getDrops(state, params).map { stack ->
            if (stack.`is`(WitcheryItems.CHALICE.get()) && state.getValue(HAS_SOUP))
                stack.set(WitcheryDataComponents.HAS_SOUP.get(), true)
            stack
        }.toMutableList()
    }

    override fun getCloneItemStack(level: LevelReader, pos: BlockPos, state: BlockState): ItemStack {
        val stack = super.getCloneItemStack(level, pos, state)
        stack.set(WitcheryDataComponents.HAS_SOUP.get(), state.getValue(HAS_SOUP))
        return stack
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        if (level.isClientSide)
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult)

        if (state.getValue(HAS_SOUP) && stack.`is`(Items.GLASS_BOTTLE)) {
            level.setBlockAndUpdate(pos, state.setValue(HAS_SOUP, false))
            stack.shrink(1)
            player.addItem(WitcheryItems.REDSTONE_SOUP.get().defaultInstance)
            return ItemInteractionResult.SUCCESS
        } else if (stack.`is`(WitcheryItems.REDSTONE_SOUP.get()) && !state.getValue(HAS_SOUP)) {
            stack.shrink(1)
            player.addItem(Items.GLASS_BOTTLE.defaultInstance)
            level.setBlockAndUpdate(pos, state.setValue(HAS_SOUP, true))
            return ItemInteractionResult.SUCCESS
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.box(5.0 / 16, 0.0, 5.0 / 16, 11.0 / 16, 10.0 / 16, 11.0 / 16)
    }

    companion object {
        val HAS_SOUP = BooleanProperty.create("has_soup")
    }
}