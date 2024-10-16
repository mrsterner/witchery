package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.item.BoneNeedleItem
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class BoneNeedleItemForge(properties: Item.Properties) : BoneNeedleItem(properties) {

    override fun onCraftedBy(stack: ItemStack, level: Level, player: Player) {
        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND)
        super.onCraftedBy(stack, level, player)
    }


}