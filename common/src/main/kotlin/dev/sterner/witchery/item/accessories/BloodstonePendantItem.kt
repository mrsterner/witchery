package dev.sterner.witchery.item.accessories

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.WitcheryAttributes
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity

open class BloodstonePendantItem(properties: Properties) : Item(properties.stacksTo(1).rarity(Rarity.UNCOMMON)) {

    val modifier = AttributeModifier(
        Witchery.id("drink_speed_modifier"), 10.0,
        AttributeModifier.Operation.ADD_VALUE)
}