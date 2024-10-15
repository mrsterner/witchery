package dev.sterner.witchery.menu.slot

import dev.sterner.witchery.menu.OvenMenu
import net.minecraft.world.Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class OvenFuelSlot(
    private val menu: OvenMenu,
    furnaceContainer: Container?,
    slot: Int,
    xPosition: Int,
    yPosition: Int
) :
    Slot(furnaceContainer, slot, xPosition, yPosition) {

    override fun mayPlace(stack: ItemStack): Boolean {
        return menu.isFuel(stack) || isBucket(stack)
    }

    override fun getMaxStackSize(stack: ItemStack): Int {
        return if (isBucket(stack)) 1 else super.getMaxStackSize(stack)
    }

    companion object {
        fun isBucket(stack: ItemStack): Boolean {
            return stack.`is`(Items.BUCKET)
        }
    }
}