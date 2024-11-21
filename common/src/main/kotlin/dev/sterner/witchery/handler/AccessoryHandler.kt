package dev.sterner.witchery.handler

import dev.sterner.witchery.item.PoppetItem
import dev.sterner.witchery.platform.PlatformUtils
import io.wispforest.accessories.api.AccessoriesCapability
import io.wispforest.accessories.api.AccessoryItem
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

object AccessoryHandler {

    fun check(livingEntity: LivingEntity, item: Item): Pair<Boolean, ItemStack?> {
        var consume = false
        var itemStack: ItemStack? = null

        if (livingEntity is Player && PlatformUtils.isModLoaded("accessories")) {
            val list: List<ItemStack> = AccessoriesCapability.get(livingEntity)?.allEquipped
                ?.filter { it.stack.item is PoppetItem }
                ?.filter { it.stack.`is`(item) }
                ?.map { it.stack }.orEmpty()

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

    fun checkPoppetNoConsume(livingEntity: LivingEntity, item: Item): ItemStack? {
        var itemStack: ItemStack? = null

        if (livingEntity is Player && PlatformUtils.isModLoaded("accessories")) {
            val list: List<ItemStack> = AccessoriesCapability.get(livingEntity)?.allEquipped
                ?.filter { it.stack.item is PoppetItem }
                ?.filter { it.stack.`is`(item) }
                ?.map { it.stack }.orEmpty()

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

    fun checkNoConsume(livingEntity: LivingEntity, item: Item): ItemStack? {
        var itemStack: ItemStack? = null

        if (livingEntity is Player && PlatformUtils.isModLoaded("accessories")) {
            val list: List<ItemStack> = AccessoriesCapability.get(livingEntity)?.allEquipped
                ?.filter { it.stack.item is AccessoryItem }
                ?.filter { it.stack.`is`(item) }
                ?.map { it.stack }.orEmpty()

            if (list.isNotEmpty()) {
                itemStack = list[0]
            }
        }

        return itemStack
    }
}