package dev.sterner.witchery.item

import dev.sterner.witchery.block.phylactery.PhylacteryBlock
import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class PhylacteryBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {

    override fun placeBlock(context: BlockPlaceContext, state: BlockState): Boolean {
        val variant = context.itemInHand.get(WitcheryDataComponents.PHYLACTERY_VARIANT.get())
            ?: PhylacteryBlock.Variant.GOLD

        val newState = state.setValue(PhylacteryBlock.VARIANT, variant)

        return super.placeBlock(context, newState)
    }
}