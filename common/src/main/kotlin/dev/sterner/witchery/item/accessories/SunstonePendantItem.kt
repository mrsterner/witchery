package dev.sterner.witchery.item.accessories

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.WitcheryAttributes
import io.wispforest.accessories.api.AccessoryItem
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder
import io.wispforest.accessories.api.slot.SlotReference
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.ItemStack

class SunstonePendantItem(properties: Properties?) : AccessoryItem(properties) {

    val modifier = AttributeModifier(
        Witchery.id("sunresist_modifier"), 100.0,
        AttributeModifier.Operation.ADD_VALUE)

    override fun getDynamicModifiers(
        stack: ItemStack?,
        reference: SlotReference?,
        builder: AccessoryAttributeBuilder
    ) {
        builder.addStackable(WitcheryAttributes.VAMPIRE_SUN_RESISTANCE, modifier)
        super.getDynamicModifiers(stack, reference, builder)
    }
}