package dev.sterner.witchery.features.misc

import dev.sterner.witchery.content.item.PoppetItem
import dev.sterner.witchery.core.util.WitcheryUtil
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.type.capability.ICurioItem

/**
 * Utility class for handling accessory-related operations for living entities, particularly players.
 * Provides methods to check for specific items in accessory slots, with or without consumption.
 */
object AccessoryHandler {

    /**
     * Checks if a specified item exists in the accessory slots of a living entity.
     * If the item exists and is linked to the entity's profile, it optionally consumes one instance of the item.
     *
     * @param livingEntity the living entity to check, expected to be a player.
     * @param item the item to check for in the accessory slots.
     * @return a pair containing a boolean indicating if the item was found and matches the profile,
     *         and an optional {@link ItemStack} copy of the matched item (null if not found).
     */
    fun checkPoppet(livingEntity: LivingEntity, item: Item): Pair<Boolean, ItemStack?> {
        var found = false
        var itemStack: ItemStack? = null

        if (livingEntity is Player) {

            val list: List<ItemStack> = WitcheryUtil.allEquippedAccessories(livingEntity)
                .filter { it.item is PoppetItem }
                .filter { it.`is`(item) }

            for (accessory in list) {
                val profile = accessory.get(DataComponents.PROFILE)
                val profileMatches = profile?.gameProfile == livingEntity.gameProfile

                if (profileMatches) {
                    found = true
                    itemStack = accessory.copy()
                    break
                }
            }
        }

        return Pair(found, itemStack)
    }

    /**
     * Removes a specified poppet from the accessory (Curio) slots of a living entity.
     */
    fun removeAccessory(livingEntity: LivingEntity, item: Item): Boolean {
        if (livingEntity !is Player) return false

        val curioInventory = CuriosApi.getCuriosInventory(livingEntity).orElse(null) ?: return false
        val equippedCurios = curioInventory.equippedCurios

        for (slotIndex in 0 until equippedCurios.slots) {
            val itemStack = equippedCurios.getStackInSlot(slotIndex)
            if (!itemStack.isEmpty && itemStack.`is`(item)) {
                val profile = itemStack.get(DataComponents.PROFILE)
                if (profile?.gameProfile == livingEntity.gameProfile) {
                    equippedCurios.setStackInSlot(slotIndex, ItemStack.EMPTY)
                    return true
                }
            }
        }

        return false
    }


    /**
     * Checks if a specified item exists in the accessory slots of a living entity without considering profile matching or consuming it.
     *
     * @param livingEntity the living entity to check, expected to be a player.
     * @param item the item to check for in the accessory slots.
     * @return a copy of the first matching {@link ItemStack}, or null if no matching item is found.
     */
    fun checkNoConsume(livingEntity: LivingEntity, item: Item): ItemStack? {
        var itemStack: ItemStack? = null

        if (livingEntity is Player) {
            val list: List<ItemStack> = WitcheryUtil.allEquippedAccessories(livingEntity)
                .filter { it.item is ICurioItem }
                .filter { it.`is`(item) }


            if (list.isNotEmpty()) {
                itemStack = list[0]
            }
        }

        return itemStack
    }
}