package dev.sterner.witchery.item

import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.core.component.DataComponents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tier
import net.minecraft.world.item.Tiers
import net.minecraft.world.item.context.UseOnContext

class CaneSwordItem(tier: Tier, properties: Properties) : SwordItem(tier, properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        if (!context.level.isClientSide && context.player?.isShiftKeyDown == true && context.hand == InteractionHand.MAIN_HAND) {
            val item = context.itemInHand.copy()
            item.remove(DataComponents.ATTRIBUTE_MODIFIERS)
            if (context.itemInHand.has(WitcheryDataComponents.UNSHEETED.get())) {
                val unsheeted = context.itemInHand.get(WitcheryDataComponents.UNSHEETED.get())!!
                item.set(WitcheryDataComponents.UNSHEETED.get(), !unsheeted)
                if (!unsheeted) {
                    item.set(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(Tiers.IRON, 4, -2.4F))
                }
            } else {
                item.set(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(Tiers.IRON, 4, -2.4F))
                item.set(WitcheryDataComponents.UNSHEETED.get(), true)
            }
            context.player!!.setItemInHand(InteractionHand.MAIN_HAND, item)
        }

        return super.useOn(context)
    }
}