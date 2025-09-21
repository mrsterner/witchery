package dev.sterner.witchery.block

import dev.sterner.witchery.block.trees.StrippableLogBlock
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.common.ItemAbilities
import net.neoforged.neoforge.common.ItemAbility
import net.neoforged.neoforge.common.extensions.IBlockExtension
import java.util.function.Supplier

class ForgeStrippableLogBlock(strippedLog: Supplier<out Block>, properties: Properties) :
    StrippableLogBlock(strippedLog, properties), IBlockExtension {

    override fun getToolModifiedState(
        state: BlockState,
        context: UseOnContext,
        itemAbility: ItemAbility,
        simulate: Boolean
    ): BlockState? {
        if (ItemAbilities.AXE_STRIP == itemAbility)
            return strippedLog.get().defaultBlockState()
                .setValue(BlockStateProperties.FACING, state.getValue(BlockStateProperties.FACING))
        return super<IBlockExtension>.getToolModifiedState(state, context, itemAbility, simulate)
    }
}