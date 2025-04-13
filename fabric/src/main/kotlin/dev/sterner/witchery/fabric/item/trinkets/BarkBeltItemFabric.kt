package dev.sterner.witchery.fabric.item.trinkets

import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.Trinket
import dev.sterner.witchery.item.accessories.BarkBeltItem
import dev.sterner.witchery.platform.BarkBeltPlayerAttachment
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class BarkBeltItemFabric(settings: Properties) : BarkBeltItem(settings), Trinket {

    override fun onEquip(stack: ItemStack?, slot: SlotReference?, entity: LivingEntity?) {
        if (entity is Player) {
            BarkBeltPlayerAttachment.setData(entity, BarkBeltPlayerAttachment.Data(0, 10, 1, 0))
        }
    }

    override fun onUnequip(stack: ItemStack?, slot: SlotReference?, entity: LivingEntity?) {
        if (entity is Player) {
            BarkBeltPlayerAttachment.setData(entity, BarkBeltPlayerAttachment.Data(0, 0, 1, 0))
        }
    }
}