package dev.sterner.witchery.item

import dev.sterner.witchery.item.TaglockItem.Companion.getLivingEntityName
import dev.sterner.witchery.item.TaglockItem.Companion.getPlayerProfile
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
        val player = getPlayerProfile(stack)
        val living = getLivingEntityName(stack)
        if (player != null) {
            tooltipComponents.add(
                Component.literal(player.gameProfile.name.replaceFirstChar(Char::uppercase))
                    .setStyle(Style.EMPTY.withColor(Color(255, 2, 100).rgb))
            )
        } else if (living != null) {
            tooltipComponents.add(
                Component.translatable(living).setStyle(Style.EMPTY.withColor(Color(255, 100, 100).rgb))
            )
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}