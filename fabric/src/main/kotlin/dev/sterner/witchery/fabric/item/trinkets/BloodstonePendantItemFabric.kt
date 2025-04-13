package dev.sterner.witchery.fabric.item.trinkets

import com.google.common.collect.Multimap
import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.Trinket
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.accessories.BatwingPendantItem
import dev.sterner.witchery.item.accessories.BloodstonePendantItem
import dev.sterner.witchery.item.accessories.SunstonePendantItem
import dev.sterner.witchery.platform.WitcheryAttributes
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class BloodstonePendantItemFabric(settings: Properties) : BloodstonePendantItem(settings), Trinket {

    override fun getModifiers(
        stack: ItemStack?,
        slot: SlotReference?,
        entity: LivingEntity?,
        slotIdentifier: ResourceLocation?
    ): Multimap<Holder<Attribute>, AttributeModifier> {
        val multimap = super.getModifiers(stack, slot, entity, slotIdentifier)
        multimap.put(WitcheryAttributes.VAMPIRE_DRINK_SPEED, modifier)
        return multimap
    }
}