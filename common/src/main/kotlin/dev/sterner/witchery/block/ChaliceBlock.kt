package dev.sterner.witchery.block

import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.phys.BlockHitResult

class ChaliceBlock(properties: Properties): Block(properties) {
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
        if (state.getValue(HAS_SOUP) && stack.`is`(Items.GLASS_BOTTLE)) {
            if (!level.isClientSide)
                level.setBlockAndUpdate(pos, state.setValue(HAS_SOUP, false))
            stack.shrink(1)
            player.addItem(WitcheryItems.REDSTONE_SOUP.get().defaultInstance)
            return ItemInteractionResult.SUCCESS
        } else if (stack.`is`(WitcheryItems.REDSTONE_SOUP.get()) && !state.getValue(HAS_SOUP)) {
            stack.shrink(1)
            player.addItem(Items.GLASS_BOTTLE.defaultInstance)
            if (!level.isClientSide)
                level.setBlockAndUpdate(pos, state.setValue(HAS_SOUP, true))
            return ItemInteractionResult.SUCCESS
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        // TODO: Funni Idea: Drink the Soup?

        return super.useWithoutItem(state, level, pos, player, hitResult)
    }

    companion object {
        val HAS_SOUP = BooleanProperty.create("has_soup")
    }
}