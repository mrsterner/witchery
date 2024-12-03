package dev.sterner.witchery.item.accessories

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.WitcheryAttributes
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.ItemStack

interface SunstonePendantItem {

    val modifier: AttributeModifier
        get() = AttributeModifier(
            Witchery.id("sunresist_modifier"), 100.0,
            AttributeModifier.Operation.ADD_VALUE)

}