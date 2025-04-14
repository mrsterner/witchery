package dev.sterner.witchery.item.accessories

import dev.sterner.witchery.api.AccessoryItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity

open class DreamweaverCharmItem(properties: Properties) : Item(properties.stacksTo(1).rarity(Rarity.UNCOMMON)), AccessoryItem {

}