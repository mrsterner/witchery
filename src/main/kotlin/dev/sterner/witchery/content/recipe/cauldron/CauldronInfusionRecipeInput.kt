package dev.sterner.witchery.content.recipe.cauldron

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput

data class CauldronInfusionRecipeInput(
    val brewStack: ItemStack,
    val thrownItem: ItemStack
) : RecipeInput {

    override fun getItem(index: Int): ItemStack {
        return when (index) {
            0 -> brewStack
            1 -> thrownItem
            else -> ItemStack.EMPTY
        }
    }

    override fun size(): Int = 2
}