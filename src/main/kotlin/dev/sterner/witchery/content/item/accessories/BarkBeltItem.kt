package dev.sterner.witchery.content.item.accessories

import dev.sterner.witchery.features.bark_belt.BarkBeltPlayerAttachment
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.type.capability.ICurioItem

open class BarkBeltItem(properties: Properties) : Item(properties.stacksTo(1).rarity(Rarity.UNCOMMON)), ICurioItem {

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