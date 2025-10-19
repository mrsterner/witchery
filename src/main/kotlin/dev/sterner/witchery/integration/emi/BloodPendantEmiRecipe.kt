package dev.sterner.witchery.integration.emi

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.SlotWidget
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Ingredient
import java.util.*

class BloodPendantEmiRecipe(id: ResourceLocation?) :
    EmiPatternCraftingRecipe(mutableListOf<EmiIngredient?>().apply {
        EmiIngredient.of(Ingredient.of(WitcheryItems.WINE_GLASS.get()))
    }, EmiStack.of(WitcheryItems.BLOODSTONE_PENDANT.get()), id) {

    override fun getInputWidget(slot: Int, x: Int, y: Int): SlotWidget {
        when (slot) {
            0 -> {
                return SlotWidget(EmiStack.of(WitcheryItems.WOOL_OF_BAT.get()), x, y)
            }

            1 -> {
                return SlotWidget(EmiStack.of(WitcheryItems.ATTUNED_STONE.get()), x, y)
            }

            2 -> {
                return SlotWidget(EmiStack.of(WitcheryItems.IMPREGNATED_FABRIC.get()), x, y)
            }

            3 -> {
                val blood = WitcheryItems.WINE_GLASS.get().defaultInstance
                blood.set(WitcheryDataComponents.BLOOD.get(), UUID.randomUUID())

                return SlotWidget(EmiStack.of(blood), x, y)
            }
        }
        return SlotWidget(EmiStack.EMPTY, x, y)
    }

    override fun getOutputWidget(x: Int, y: Int): SlotWidget {
        return SlotWidget(EmiStack.of(WitcheryItems.BLOODSTONE_PENDANT.get()), x, y)
    }
}