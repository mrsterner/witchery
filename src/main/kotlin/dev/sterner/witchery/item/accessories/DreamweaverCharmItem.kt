package dev.sterner.witchery.item.accessories

import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import top.theillusivec4.curios.api.type.capability.ICurio
import top.theillusivec4.curios.api.type.capability.ICurioItem

open class DreamweaverCharmItem(properties: Properties) : Item(properties.stacksTo(1).rarity(Rarity.UNCOMMON)), ICurioItem {

}