package dev.sterner.witchery.item.accessories

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.AccessoryItem
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity

open class BatwingPendantItem(properties: Properties) : Item(properties.stacksTo(1).rarity(Rarity.UNCOMMON)),
    AccessoryItem {

    val modifier = AttributeModifier(
        Witchery.id("batwing_modifier"), 60.0 * 20,
        AttributeModifier.Operation.ADD_VALUE
    )

}