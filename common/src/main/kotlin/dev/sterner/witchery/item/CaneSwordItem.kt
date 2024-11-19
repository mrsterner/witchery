package dev.sterner.witchery.item

import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Items
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tier
import net.minecraft.world.item.context.UseOnContext

class CaneSwordItem(tier: Tier, properties: Properties, val unsheeded: Boolean) : SwordItem(tier, properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        if (context.player?.isShiftKeyDown == true) {
            val blood = context.itemInHand.get(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get())
            if (unsheeded) {
                val item = WitcheryItems.CANE_SWORD.get().defaultInstance
                item.set(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get(), blood)
                context.player!!.setItemInHand(InteractionHand.MAIN_HAND, item)
            } else {
                val item = WitcheryItems.CANE_SWORD_UNSHEETED.get().defaultInstance
                item.set(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get(), blood)
                context.player!!.setItemInHand(InteractionHand.MAIN_HAND, item)
            }
        }

        return super.useOn(context)
    }


}