package dev.sterner.witchery.neoforge.item.curios

import dev.sterner.witchery.handler.affliction.TransformationHandler
import dev.sterner.witchery.item.accessories.MoonCharmItem
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.type.capability.ICurioItem

class MoonCharmItemNeoForge(settings: Properties) : MoonCharmItem(settings), ICurioItem {

    override fun onUnequip(slotContext: SlotContext?, newStack: ItemStack?, stack: ItemStack?) {
        if (slotContext?.entity is Player) {
            TransformationHandler.removeForm(player = slotContext.entity as Player)
        }
        super.onUnequip(slotContext, newStack, stack)
    }
}