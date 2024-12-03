package dev.sterner.witchery.item.accessories

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.WitcheryAttributes
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.ItemStack

interface BatwingPendantItem {

    val modifier: AttributeModifier
        get() = AttributeModifier(
            Witchery.id("batwing_modifier"), 60.0 * 20,
            AttributeModifier.Operation.ADD_VALUE)
}