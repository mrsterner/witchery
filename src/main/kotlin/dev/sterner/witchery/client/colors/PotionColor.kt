package dev.sterner.witchery.client.colors


import dev.sterner.witchery.core.registry.WitcheryDataComponents
import net.minecraft.client.color.item.ItemColor
import net.minecraft.util.FastColor
import net.minecraft.world.item.ItemStack
import java.awt.Color

object PotionColor : ItemColor {

    override fun getColor(itemStack: ItemStack, i: Int): Int {
        if (itemStack.has(WitcheryDataComponents.WITCHERY_POTION_CONTENT.get())) {
            val potionContentList = itemStack.get(WitcheryDataComponents.WITCHERY_POTION_CONTENT.get())!!
            if (potionContentList.isNotEmpty()) {
                val color = potionContentList.last().color
                return if (i > 0) -1 else FastColor.ARGB32.opaque(color)
            }
        }

        return if (i > 0) -1 else Color(255, 20, 100).rgb
    }
}