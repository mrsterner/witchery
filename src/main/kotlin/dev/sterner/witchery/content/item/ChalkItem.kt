package dev.sterner.witchery.content.item

import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.content.block.ritual.GoldenChalkBlock
import dev.sterner.witchery.content.block.ritual.RitualChalkBlock
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block

class ChalkItem(block: Block, properties: Properties) : ItemNameBlockItem(block, properties.component(WitcheryDataComponents.CHALK_USES.get(), ChalkItem.MAX_USES)) {

    companion object {
        const val MAX_USES = 96
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val item = (context.itemInHand.item as ChalkItem)
        val state = level.getBlockState(pos)

        context.player?.let { WitcheryApi.makePlayerWitchy(it) }

        if (level.isClientSide) return InteractionResult.sidedSuccess(true)

        if (state.block is RitualChalkBlock && state.`is`(item.block)) {
            level.setBlockAndUpdate(
                pos, state.setValue(
                    RitualChalkBlock.VARIANT,
                    context.level.random.nextIntBetweenInclusive(0, RitualChalkBlock.VARIANTS)
                )
            )

            decrementUses(context.itemInHand, context.player)
            return InteractionResult.CONSUME
        } else if (state.block is GoldenChalkBlock && state.`is`(item.block)) {
            return InteractionResult.FAIL
        }

        val result = super.useOn(context)

        if (result.consumesAction()) {
            decrementUses(context.itemInHand, context.player)
        }

        return result
    }

    private fun decrementUses(stack: ItemStack, player: Player?) {
        val currentUses = stack.getOrDefault(WitcheryDataComponents.CHALK_USES.get(), MAX_USES)
        val newUses = currentUses - 1

        if (newUses <= 0) {
            stack.shrink(1)
            player?.level()?.playSound(
                null,
                player.blockPosition(),
                SoundEvents.ITEM_BREAK,
                SoundSource.PLAYERS,
                0.8f,
                0.8f + player.level().random.nextFloat() * 0.4f
            )
        } else {
            stack.set(WitcheryDataComponents.CHALK_USES.get(), newUses)
        }
    }

    override fun isBarVisible(stack: ItemStack): Boolean {
        val uses = stack.getOrDefault(WitcheryDataComponents.CHALK_USES.get(), MAX_USES)
        return uses < MAX_USES
    }

    override fun getBarWidth(stack: ItemStack): Int {
        val uses = stack.getOrDefault(WitcheryDataComponents.CHALK_USES.get(), MAX_USES)
        return Math.round(13.0f * uses / MAX_USES)
    }

    override fun getBarColor(stack: ItemStack): Int {
        val uses = stack.getOrDefault(WitcheryDataComponents.CHALK_USES.get(), MAX_USES)
        val fraction = uses.toFloat() / MAX_USES
        return Mth.hsvToRgb(fraction / 3.0f, 1.0f, 1.0f)
    }
}