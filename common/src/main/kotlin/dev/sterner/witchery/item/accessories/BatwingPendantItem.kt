package dev.sterner.witchery.item.accessories

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryAttributes
import io.wispforest.accessories.api.AccessoryItem
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder
import io.wispforest.accessories.api.slot.SlotReference
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.ItemStack

class BatwingPendantItem(properties: Properties?) : AccessoryItem(properties) {

    override fun getDynamicModifiers(
        stack: ItemStack?,
        reference: SlotReference?,
        builder: AccessoryAttributeBuilder
    ) {
        builder.addStackable(
            WitcheryAttributes.VAMPIRE_BAT_FORM_DURATION,
            AttributeModifier(
                Witchery.id("batwing_modifier"), 120.0,
                AttributeModifier.Operation.ADD_VALUE)
        )
        super.getDynamicModifiers(stack, reference, builder)
    }
}