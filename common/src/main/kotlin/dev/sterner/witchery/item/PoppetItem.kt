package dev.sterner.witchery.item

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
        val profile = TaglockItem.getPlayerProfile(stack)
        if (profile != null) {
            tooltipComponents.add(
                Component.literal(profile.gameProfile.name.replaceFirstChar(Char::uppercase))
                    .setStyle(Style.EMPTY.withColor(Color(255, 2, 100).rgb))
            )
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}