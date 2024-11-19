package dev.sterner.witchery.item

import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tier
import net.minecraft.world.item.context.UseOnContext

class CaneSwordItem(tier: Tier, properties: Properties) : SwordItem(tier, properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        if (!context.level.isClientSide && context.player?.isShiftKeyDown == true && context.hand == InteractionHand.MAIN_HAND) {
            val unsheeted = context.itemInHand.get(WitcheryDataComponents.UNSHEETED.get())
            println(unsheeted)
            val item = context.itemInHand.copy()
            if (unsheeted == true) {
                item.set(WitcheryDataComponents.UNSHEETED.get(), false)
            } else {
                item.set(WitcheryDataComponents.UNSHEETED.get(), true)
            }
            context.player!!.setItemInHand(InteractionHand.MAIN_HAND, item)
        }

        return super.useOn(context)
    }


}