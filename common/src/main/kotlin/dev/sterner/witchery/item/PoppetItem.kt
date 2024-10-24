package dev.sterner.witchery.item

import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.client.Minecraft
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
        val profile = stack.get(WitcheryDataComponents.PLAYER_UUID.get())
        val name = stack.get(WitcheryDataComponents.ENTITY_NAME_COMPONENT.get())
        println(profile)
        if (profile != null) {
            val player = Minecraft.getInstance().level?.getPlayerByUUID(profile)

            if (player != null) {
                tooltipComponents.add(
                    Component.literal(player.gameProfile.name.replaceFirstChar(Char::uppercase))
                        .setStyle(Style.EMPTY.withColor(Color(255, 2, 100).rgb))
                )
            } else if (name != null) {
                tooltipComponents.add(
                    Component.literal(name.replaceFirstChar(Char::uppercase))
                        .setStyle(Style.EMPTY.withColor(Color(255, 2, 100).rgb))
                )
            }
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}