package dev.sterner.witchery.item

import dev.sterner.witchery.block.censer.CenserBlock
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class CenserBlockItem(block: Block, properties: Properties, private val longType: Boolean) :
    ItemNameBlockItem(block, properties) {

    override fun getPlacementState(context: BlockPlaceContext): BlockState? {
        val baseState = super.getPlacementState(context)
        return baseState?.setValue(CenserBlock.TYPE, longType)
    }
}