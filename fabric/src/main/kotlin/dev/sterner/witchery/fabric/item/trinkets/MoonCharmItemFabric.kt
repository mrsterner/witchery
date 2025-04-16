package dev.sterner.witchery.fabric.item.trinkets

import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.Trinket
import dev.emi.trinkets.api.TrinketsApi
import dev.sterner.witchery.handler.transformation.TransformationHandler
import dev.sterner.witchery.handler.werewolf.WerewolfAbilityHandler
import dev.sterner.witchery.item.accessories.MoonCharmItem
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class MoonCharmItemFabric(settings: Properties) : MoonCharmItem(settings), Trinket {

    init {
        TrinketsApi.registerTrinket(this, this)
    }

    override fun onUnequip(stack: ItemStack?, slot: SlotReference?, entity: LivingEntity?) {
        if (entity is Player) {
            TransformationHandler.removeForm(player = entity)
            WerewolfAbilityHandler.setAbilityIndex(player = entity, -1)
        }

        super.onUnequip(stack, slot, entity)
    }
}