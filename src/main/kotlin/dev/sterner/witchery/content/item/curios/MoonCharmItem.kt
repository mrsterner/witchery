package dev.sterner.witchery.content.item.curios

import dev.sterner.witchery.features.affliction.event.TransformationHandler
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.type.capability.ICurioItem

open class MoonCharmItem(properties: Properties) : Item(properties.stacksTo(1).rarity(Rarity.UNCOMMON)), ICurioItem {
    override fun onUnequip(slotContext: SlotContext?, newStack: ItemStack?, stack: ItemStack?) {
        if (slotContext?.entity is Player) {
            TransformationHandler.removeForm(player = slotContext.entity as Player)
        }
        super.onUnequip(slotContext, newStack, stack)
    }
}