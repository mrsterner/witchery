package dev.sterner.witchery.fabric.trinkets

import com.google.common.collect.Multimap
import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.TrinketItem
import dev.sterner.witchery.item.accessories.BatwingPendantItem
import dev.sterner.witchery.platform.WitcheryAttributes
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.ItemStack

class BatwingPendantItemFabric(settings: Properties?) : TrinketItem(settings), BatwingPendantItem {

    override fun getModifiers(
        stack: ItemStack?,
        slot: SlotReference?,
        entity: LivingEntity?,
        slotIdentifier: ResourceLocation?
    ): Multimap<Holder<Attribute>, AttributeModifier> {
        val s = super.getModifiers(stack, slot, entity, slotIdentifier)
        s.put(WitcheryAttributes.VAMPIRE_BAT_FORM_DURATION, modifier)
        return s
    }
}