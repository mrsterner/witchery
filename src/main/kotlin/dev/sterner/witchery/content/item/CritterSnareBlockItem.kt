package dev.sterner.witchery.content.item

import dev.sterner.witchery.core.registry.WitcheryDataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block

class CritterSnareBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        if (stack.has(WitcheryDataComponents.CAPTURED_ENTITY.get())) {
            tooltipComponents.add(
                Component.translatable("witchery.captured.${stack.get(WitcheryDataComponents.CAPTURED_ENTITY.get())!!.serializedName.lowercase()}")
                    .setStyle(Style.EMPTY)
            )
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}