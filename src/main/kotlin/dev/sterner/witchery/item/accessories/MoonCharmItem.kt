package dev.sterner.witchery.item.accessories

import dev.sterner.witchery.api.interfaces.AccessoryItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity

open class MoonCharmItem(properties: Properties) : Item(properties.stacksTo(1).rarity(Rarity.UNCOMMON)),
    AccessoryItem