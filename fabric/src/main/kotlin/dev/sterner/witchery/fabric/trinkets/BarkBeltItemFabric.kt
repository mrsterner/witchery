package dev.sterner.witchery.fabric.trinkets

import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.TrinketItem
import dev.sterner.witchery.item.accessories.BarkBeltItem
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class BarkBeltItemFabric(properties: Properties): TrinketItem(properties), BarkBeltItem {

    override fun onEquip(stack: ItemStack?, slot: SlotReference?, entity: LivingEntity?) {
        if (entity is Player) {
            onEquip(stack, entity)
        }
    }

    override fun onUnequip(stack: ItemStack?, slot: SlotReference?, entity: LivingEntity?) {
        if (entity is Player) {
            onUnequip(stack, entity)
        }

    }
}