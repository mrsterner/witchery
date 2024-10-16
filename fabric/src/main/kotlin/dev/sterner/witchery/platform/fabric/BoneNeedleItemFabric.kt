package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.item.BoneNeedleItem
import net.fabricmc.fabric.api.item.v1.FabricItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class BoneNeedleItemFabric(properties: Item.Properties) : BoneNeedleItem(properties), FabricItem {

    override fun getRecipeRemainder(stack: ItemStack): ItemStack {
        val damage = stack.damageValue + 1
        val copy = stack.copy()
        copy.damageValue = damage
        return copy
    }
}