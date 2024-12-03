package dev.sterner.witchery.item.accessories

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.WitcheryAttributes
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.ItemStack

interface BloodstonePendantItem {

    val modifier: AttributeModifier
        get() = AttributeModifier(
            Witchery.id("drink_speed_modifier"), 10.0,
            AttributeModifier.Operation.ADD_VALUE)

}