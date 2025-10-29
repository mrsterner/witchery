package dev.sterner.witchery.content.block

import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.ItemAbilities
import net.neoforged.neoforge.common.ItemAbility
import org.jetbrains.annotations.Nullable
import java.util.function.Supplier


class LogBlock(val stripped: Supplier<out RotatedPillarBlock>, properties: Properties) : RotatedPillarBlock(properties) {

    @Nullable
    override fun getToolModifiedState(
        state: BlockState,
        context: UseOnContext,
        itemAbility: ItemAbility,
        simulate: Boolean
    ): BlockState? {
        if (itemAbility == ItemAbilities.AXE_STRIP) {
            return stripped.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS))
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate)
    }
}