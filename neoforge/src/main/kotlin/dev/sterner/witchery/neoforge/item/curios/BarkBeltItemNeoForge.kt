package dev.sterner.witchery.neoforge.item.curios

import dev.sterner.witchery.item.accessories.BarkBeltItem
import dev.sterner.witchery.platform.BarkBeltPlayerAttachment
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.type.capability.ICurioItem

class BarkBeltItemNeoForge(settings: Properties) : BarkBeltItem(settings), ICurioItem {

    override fun onEquip(slotContext: SlotContext?, prevStack: ItemStack?, stack: ItemStack?) {
        val entity = slotContext?.entity
        if (entity is Player) {
            BarkBeltPlayerAttachment.setData(entity, BarkBeltPlayerAttachment.Data(0, 10, 1, 0))
        }
        super.onEquip(slotContext, prevStack, stack)
    }

    override fun onUnequip(slotContext: SlotContext?, newStack: ItemStack?, stack: ItemStack?) {
        val entity = slotContext?.entity
        if (entity is Player) {
            BarkBeltPlayerAttachment.setData(entity, BarkBeltPlayerAttachment.Data(0, 0, 1, 0))
        }
        super.onUnequip(slotContext, newStack, stack)
    }
}