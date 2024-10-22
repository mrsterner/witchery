package dev.sterner.witchery.item

import dev.sterner.witchery.block.arthana.ArthanaBlockEntity
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tiers
import net.minecraft.world.item.context.UseOnContext

class ArthanaItem(properties: Properties) : SwordItem(Tiers.GOLD, properties) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val face = context.clickedFace
        val state = level.getBlockState(pos)
        if ((state.`is`(WitcheryBlocks.ALTAR.get()) || state.`is`(WitcheryBlocks.ALTAR_COMPONENT.get())) && face == Direction.UP) {
            if (!level.isClientSide) {
                val newState = WitcheryBlocks.ARTHANA.get().defaultBlockState()
                level.setBlockAndUpdate(pos.above(), newState)

                val be = ArthanaBlockEntity(pos.above(), newState)
                be.arthana = context.itemInHand.copy()
                context.itemInHand.shrink(1)
                level.setBlockEntity(be)
            }
            return InteractionResult.SUCCESS
        }

        return super.useOn(context)
    }
}