package dev.sterner.witchery.core.api

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack

data class SavedInventory(
    val items: List<ItemStack>,
    val armor: List<ItemStack>,
    val offhand: List<ItemStack>
) {
    fun restore(inventory: Inventory) {
        items.forEachIndexed { index, stack ->
            if (index < inventory.items.size) {
                inventory.items[index] = stack.copy()
            }
        }

        armor.forEachIndexed { index, stack ->
            if (index < inventory.armor.size) {
                inventory.armor[index] = stack.copy()
            }
        }

        offhand.forEachIndexed { index, stack ->
            if (index < inventory.offhand.size) {
                inventory.offhand[index] = stack.copy()
            }
        }
    }

    companion object {
        fun from(inventory: Inventory): SavedInventory {
            return SavedInventory(
                items = inventory.items.map { it.copy() },
                armor = inventory.armor.map { it.copy() },
                offhand = inventory.offhand.map { it.copy() }
            )
        }
    }
}