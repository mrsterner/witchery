package dev.sterner.witchery.item

import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import java.awt.Color
import java.util.*

class WaystoneItem(properties: Properties) : Item(properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val stack = context.itemInHand
        context.player?.let { WitcheryApi.makePlayerWitchy(it) }
        bindGlobalBlockPos(context.level, context.clickedPos.relative(context.clickedFace), stack)
        return super.useOn(context)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val glob = getGlobalPos(stack)
        if (glob != null) {
            val dimension = capitalizeString(glob.dimension.location().path)
            val color = when (dimension) {
                "The Nether" -> {
                    Color(255, 0, 0).rgb
                }

                "The End" -> {
                    Color(255, 0, 255).rgb
                }

                else -> {
                    Color(0, 255, 0).rgb
                }
            }
            tooltipComponents.add(Component.literal(dimension).setStyle(Style.EMPTY).withColor(color))

            tooltipComponents.add(
                Component.literal("Position: ").withColor(0xFFAA00)
                    .append(Component.literal("${glob.pos.x} ${glob.pos.y} ${glob.pos.z}").withColor(0x55FFFF))
            )
        }
        val player = Minecraft.getInstance().level?.let { TaglockItem.getPlayer(it, stack) }
        if (player != null) {
            tooltipComponents.add(
                Component.literal(player.gameProfile.name.replaceFirstChar(Char::uppercase))
                    .setStyle(Style.EMPTY.withColor(Color(255, 2, 100).rgb))
            )
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    companion object {
        fun bindGlobalBlockPos(level: Level, pos: BlockPos, stack: ItemStack) {
            stack.set(WitcheryDataComponents.GLOBAL_POS_COMPONENT.get(), GlobalPos.of(level.dimension(), pos))
        }

        fun getGlobalPos(stack: ItemStack): GlobalPos? {
            return stack.get(WitcheryDataComponents.GLOBAL_POS_COMPONENT.get())
        }

        fun capitalizeString(string: String): String {
            val chars = string.lowercase(Locale.getDefault()).toCharArray()
            var found = false
            for (i in chars.indices) {
                if (!found && Character.isLetter(chars[i])) {
                    chars[i] = chars[i].uppercaseChar()
                    found = true
                } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'' || chars[i] == '_') {
                    chars[i] = ' '
                    found = false
                }
            }
            return String(chars)
        }
    }
}