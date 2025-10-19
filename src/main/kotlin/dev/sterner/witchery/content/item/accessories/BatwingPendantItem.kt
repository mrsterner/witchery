package dev.sterner.witchery.content.item.accessories

import com.google.common.collect.Multimap
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.data_attachment.WitcheryAttributes
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.type.capability.ICurioItem

open class BatwingPendantItem(properties: Properties) : Item(properties.stacksTo(1).rarity(Rarity.UNCOMMON)),
    ICurioItem {

    val modifier = AttributeModifier(
        Witchery.id("batwing_modifier"), 60.0 * 20,
        AttributeModifier.Operation.ADD_VALUE
    )

    override fun getAttributeModifiers(
        slotContext: SlotContext?,
        id: ResourceLocation?,
        stack: ItemStack?
    ): Multimap<Holder<Attribute>, AttributeModifier> {
        val map = super.getAttributeModifiers(slotContext, id, stack)
        map.put(WitcheryAttributes.VAMPIRE_BAT_FORM_DURATION, modifier)
        return map
    }
}