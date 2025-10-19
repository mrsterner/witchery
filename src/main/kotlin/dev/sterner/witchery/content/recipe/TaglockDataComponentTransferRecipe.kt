package dev.sterner.witchery.content.recipe

import dev.sterner.witchery.content.item.PoppetItem
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.registry.WitcheryRecipeSerializers
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level

class TaglockDataComponentTransferRecipe : CustomRecipe(CraftingBookCategory.MISC) {

    override fun matches(input: CraftingInput, level: Level): Boolean {
        for (item in input.items()) {
            if (item.`is`(WitcheryItems.TAGLOCK.get())) {
                return true
            }
        }
        return false
    }

    override fun assemble(input: CraftingInput, registries: HolderLookup.Provider): ItemStack {
        var poppetItem: ItemStack? = null
        var taglockItem: ItemStack? = null

        for (item in input.items()) {
            if (item.item is PoppetItem) {
                poppetItem = item.copy()
            } else if (item.`is`(WitcheryItems.TAGLOCK.get())) {
                taglockItem = item.copy()
            }
        }

        if (poppetItem != null && taglockItem != null) {
            poppetItem.set(DataComponents.PROFILE, taglockItem.get(DataComponents.PROFILE))
            poppetItem.set(
                WitcheryDataComponents.ENTITY_NAME_COMPONENT.get(),
                taglockItem.get(WitcheryDataComponents.ENTITY_NAME_COMPONENT.get())
            )
            poppetItem.set(
                WitcheryDataComponents.ENTITY_ID_COMPONENT.get(),
                taglockItem.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
            )
        }

        return poppetItem ?: ItemStack.EMPTY
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width >= 2 && height >= 2
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return WitcheryRecipeSerializers.TAGLOCK_RECIPE_SERIALIZER.get()
    }

    companion object {
        const val NAME: String = "data_transfer"
    }
}