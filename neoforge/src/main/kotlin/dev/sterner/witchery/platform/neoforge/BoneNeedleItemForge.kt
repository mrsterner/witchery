package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.item.BoneNeedleItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.common.extensions.IItemExtension

class BoneNeedleItemForge(properties: Item.Properties) : BoneNeedleItem(properties), IItemExtension {

    override fun isRepairable(arg: ItemStack): Boolean {
        return false
    }

    override fun getCraftingRemainingItem(itemStack: ItemStack): ItemStack {
        val damage = itemStack.damageValue + 1
        val copy = itemStack.copy()
        copy.damageValue = damage
        return copy
    }
}