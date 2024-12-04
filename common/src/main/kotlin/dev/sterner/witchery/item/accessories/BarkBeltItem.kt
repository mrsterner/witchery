package dev.sterner.witchery.item.accessories

import dev.sterner.witchery.platform.BarkBeltPlayerAttachment
import io.wispforest.accessories.api.AccessoryItem
import io.wispforest.accessories.api.slot.SlotReference
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class BarkBeltItem(properties: Properties?) : AccessoryItem(properties) {



    override fun onEquip(stack: ItemStack?, reference: SlotReference?) {
        val entity = reference?.entity()

        if (entity is Player) {
            BarkBeltPlayerAttachment.setData(entity, BarkBeltPlayerAttachment.Data(0, 10, 1, 0))
        }

        super.onEquip(stack, reference)
    }

    override fun onUnequip(stack: ItemStack?, reference: SlotReference?) {
        val entity = reference?.entity()

        if (entity is Player) {
            BarkBeltPlayerAttachment.setData(entity, BarkBeltPlayerAttachment.Data(0, 0, 1, 0))
        }

        super.onUnequip(stack, reference)
    }
}