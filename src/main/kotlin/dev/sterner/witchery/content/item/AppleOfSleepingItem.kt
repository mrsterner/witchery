package dev.sterner.witchery.content.item

import dev.sterner.witchery.content.item.brew.BrewOfSleepingItem
import dev.sterner.witchery.content.worldgen.WitcheryWorldgenKeys
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level

class AppleOfSleepingItem(properties: Properties) : Item(properties) {

    override fun finishUsingItem(
        stack: ItemStack,
        level: Level,
        livingEntity: LivingEntity
    ): ItemStack {

        if (livingEntity is Player) {
            val player = livingEntity
            if (player.level().dimension() != Level.OVERWORLD) {
                player.sendSystemMessage(Component.translatable("witchery.message.cant_sleep_here"))
                return super.finishUsingItem(stack, level, livingEntity)
            }

            val (itemsToKeep, armorToKeep) = BrewOfSleepingItem.savePlayerItems(player, false)

            val sleepingPlayer = BrewOfSleepingItem.createSleepingEntity(player)
            player.inventory.clearContent()
            BrewOfSleepingItem.restoreKeptItems(player, itemsToKeep, armorToKeep)

            player.level().addFreshEntity(sleepingPlayer)

            BrewOfSleepingItem.forceLoadPlayerChunk(player)

            val destinationKey = WitcheryWorldgenKeys.NIGHTMARE

            BrewOfSleepingItem.teleportToDreamDimension(player, destinationKey)
        }

        return super.finishUsingItem(stack, level, livingEntity)
    }
}