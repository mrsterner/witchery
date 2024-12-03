package dev.sterner.witchery.neoforge.curios

import dev.sterner.witchery.item.accessories.BarkBeltItem
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.type.capability.ICurioItem

class BarkBeltItemNeoforge(properties: Properties): Item(properties), BarkBeltItem, ICurioItem {

    override fun onEquip(slotContext: SlotContext?, prevStack: ItemStack?, stack: ItemStack?) {
        if (slotContext?.entity is Player) {
            onEquip(stack, slotContext.entity as Player)
        }
    }

    override fun onUnequip(slotContext: SlotContext?, newStack: ItemStack?, stack: ItemStack?) {
        if (slotContext?.entity is Player) {
            onUnequip(stack,  slotContext.entity as Player)
        }
    }
}