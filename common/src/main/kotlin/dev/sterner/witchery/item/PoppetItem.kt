package dev.sterner.witchery.item

import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import java.awt.Color

class PoppetItem(properties: Properties) : Item(properties.stacksTo(1)) {

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val profile = stack.get(DataComponents.PROFILE)
        val name = stack.get(WitcheryDataComponents.ENTITY_NAME_COMPONENT.get())
        //println(profile)
        if (profile != null) {
            tooltipComponents.add(
                Component.literal(profile.gameProfile.name.replaceFirstChar(Char::uppercase))
                    .setStyle(Style.EMPTY.withColor(Color(255, 2, 100).rgb))
            )
        } else if (name != null) {
            tooltipComponents.add(
                Component.literal(name.replaceFirstChar(Char::uppercase))
                    .setStyle(Style.EMPTY.withColor(Color(255, 2, 100).rgb))
            )
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}