package dev.sterner.witchery.api.multiblock

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Supplier


class MultiBlockItem(block: Block, properties: Properties, private val structure: Supplier<out MultiBlockStructure?>) :
    BlockItem(block, properties) {

    override fun canPlace(context: BlockPlaceContext, state: BlockState): Boolean {
        if (structure.get()?.canPlace(context) != true) {
            return false
        }
        return super.canPlace(context, state)
    }

    override fun placeBlock(context: BlockPlaceContext, state: BlockState): Boolean {
        structure.get()?.place(context)
        return super.placeBlock(context, state)
    }
}