package dev.sterner.witchery.neoforge.curios

import com.google.common.collect.Multimap
import dev.sterner.witchery.item.accessories.BatwingPendantItem
import dev.sterner.witchery.platform.WitcheryAttributes
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.type.capability.ICurioItem

class BatwingPendantItemNeoforge(settings: Properties) : Item(settings), BatwingPendantItem, ICurioItem {

    override fun getAttributeModifiers(
        slotContext: SlotContext?,
        id: ResourceLocation?,
        stack: ItemStack?
    ): Multimap<Holder<Attribute>, AttributeModifier> {
        val v =  super.getAttributeModifiers(slotContext, id, stack)
        v.put(WitcheryAttributes.VAMPIRE_BAT_FORM_DURATION, modifier)
        return v
    }
}