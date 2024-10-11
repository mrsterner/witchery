package dev.sterner.witchery.menu

import dev.sterner.witchery.registry.WitcheryMenuTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

class OvenMenu(id: Int, val inventory: Inventory, val buf: FriendlyByteBuf) : AbstractContainerMenu(WitcheryMenuTypes.OVEN_MENU_TYPE.get(), id) {

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return ItemStack.EMPTY //TODO impl
    }

    override fun stillValid(player: Player): Boolean {
        return true //TODO impl
    }


}