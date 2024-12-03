package dev.sterner.witchery.item.accessories

import dev.sterner.witchery.platform.BarkBeltPlayerAttachment
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

open interface BarkBeltItem {

    fun onEquip(stack: ItemStack?, player: Player) {
        BarkBeltPlayerAttachment.setData(player, BarkBeltPlayerAttachment.Data(0, 10, 1, 0))
    }

    fun onUnequip(stack: ItemStack?, player: Player) {
        BarkBeltPlayerAttachment.setData(player, BarkBeltPlayerAttachment.Data(0, 0, 1, 0))
    }
}