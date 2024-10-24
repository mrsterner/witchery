package dev.sterner.witchery.recipe

import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryRecipeSerializers
import net.minecraft.core.HolderLookup
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level

class TaglockDataComponentTransferRecipe() : CustomRecipe(CraftingBookCategory.MISC) {

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
            if (item.`is`(WitcheryItems.VOODOO_POPPET.get())) {
                println("PoppetFound")
                poppetItem = item.copy()
            } else if (item.`is`(WitcheryItems.TAGLOCK.get())) {
                println("TaglockFound")
                taglockItem = item
            }
        }

        if (poppetItem != null && taglockItem != null) {
            println("Transfer")
            poppetItem.set(WitcheryDataComponents.PLAYER_UUID.get(), taglockItem.get(WitcheryDataComponents.PLAYER_UUID.get()))
            poppetItem.set(WitcheryDataComponents.ENTITY_NAME_COMPONENT.get(), taglockItem.get(WitcheryDataComponents.ENTITY_NAME_COMPONENT.get()))
        }
println("ReturnStack")
        return poppetItem ?: ItemStack.EMPTY
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width >= 2 && height >= 2
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return ItemStack.EMPTY
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return WitcheryRecipeSerializers.TAGLOCK_RECIPE_SERIALIZER.get()
    }
/*
    override fun getType(): RecipeType<*> {
        return WitcheryRecipeTypes.TAGLOCK_RECIPE_TYPE.get()
    }

 */


    companion object {
        const val NAME: String = "data_transfer"
    }
}