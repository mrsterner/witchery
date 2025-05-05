package dev.sterner.witchery.handler

import dev.sterner.witchery.api.interfaces.AccessoryItem
import dev.sterner.witchery.item.PoppetItem
import dev.sterner.witchery.platform.PlatformUtils
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

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
        var consume = false
        var itemStack: ItemStack? = null

        if (livingEntity is Player && PlatformUtils.isModLoaded("accessories")) {
            val list: List<ItemStack> = PlatformUtils.allEquippedAccessories(livingEntity)
                .filter { it.item is PoppetItem }
                .filter { it.`is`(item) }

            for (accessory in list) {
                val profile = accessory.get(DataComponents.PROFILE)
                if (profile?.gameProfile == livingEntity.gameProfile) {
                    consume = true
                }

                if (consume) {
                    itemStack = accessory.copy()
                    accessory.shrink(1)
                    break
                }
            }
        }

        return Pair(consume, itemStack)
    }

    /**
     * Checks if a specified item exists in the accessory slots of a living entity without consuming it.
     * The item must also match the entity's profile.
     *
     * @param livingEntity the living entity to check, expected to be a player.
     * @param item the item to check for in the accessory slots.
     * @return a copy of the matched {@link ItemStack}, or null if no matching item is found.
     */
    fun checkPoppetNoConsume(livingEntity: LivingEntity, item: Item): ItemStack? {
        var itemStack: ItemStack? = null

        if (livingEntity is Player && PlatformUtils.isModLoaded("accessories")) {
            val list: List<ItemStack> = PlatformUtils.allEquippedAccessories(livingEntity)
                .filter { it.item is PoppetItem }
                .filter { it.`is`(item) }

            for (accessory in list) {
                val profile = accessory.get(DataComponents.PROFILE)
                if (profile?.gameProfile == livingEntity.gameProfile) {
                    itemStack = accessory.copy()
                    break
                }
            }
        }

        return itemStack
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

        if (livingEntity is Player && PlatformUtils.isModLoaded("accessories")) {
            val list: List<ItemStack> = PlatformUtils.allEquippedAccessories(livingEntity)
                .filter { it.item is AccessoryItem }
                .filter { it.`is`(item) }


            if (list.isNotEmpty()) {
                itemStack = list[0]
            }
        }

        return itemStack
    }
}