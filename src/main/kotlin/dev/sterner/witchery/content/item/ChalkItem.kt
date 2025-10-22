package dev.sterner.witchery.content.item

import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.content.block.ritual.GoldenChalkBlock
import dev.sterner.witchery.content.block.ritual.RitualChalkBlock
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block

class ChalkItem(block: Block, properties: Properties) : ItemNameBlockItem(block, properties.durability(96)) {

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

            context.itemInHand.hurtAndBreak(1, context.player, EquipmentSlot.MAINHAND)
            return InteractionResult.CONSUME
        } else if (state.block is GoldenChalkBlock && state.`is`(item.block)) {
            return InteractionResult.FAIL
        }

        return super.useOn(context)
    }
}