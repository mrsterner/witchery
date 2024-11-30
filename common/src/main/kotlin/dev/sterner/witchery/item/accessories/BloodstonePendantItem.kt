package dev.sterner.witchery.item.accessories

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.WitcheryAttributes
import io.wispforest.accessories.api.AccessoryItem
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder
import io.wispforest.accessories.api.slot.SlotReference
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.ItemStack

class BloodstonePendantItem(properties: Properties?) : AccessoryItem(properties) {

    val modifier = AttributeModifier(
        Witchery.id("drink_speed_modifier"), 10.0,
        AttributeModifier.Operation.ADD_VALUE)

    override fun getDynamicModifiers(
        stack: ItemStack?,
        reference: SlotReference?,
        builder: AccessoryAttributeBuilder
    ) {
        builder.addStackable(WitcheryAttributes.VAMPIRE_DRINK_SPEED, modifier)
        super.getDynamicModifiers(stack, reference, builder)
    }
}