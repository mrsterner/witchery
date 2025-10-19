package dev.sterner.witchery.content.recipe

import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryRecipeSerializers
import net.minecraft.core.HolderLookup
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level

class PendantDataComponentRecipe : CustomRecipe(CraftingBookCategory.MISC) {

    override fun matches(input: CraftingInput, level: Level): Boolean {
        var hasWineGlassWithBlood = false
        var hasQuartzSphereWithSun = false

        for (item in input.items()) {
            if (item.`is`(WitcheryItems.WINE_GLASS.get()) &&
                item.has(WitcheryDataComponents.BLOOD.get()) &&
                item.get(WitcheryDataComponents.BLOOD.get()) != null
            ) {
                hasWineGlassWithBlood = true
            }
            if (item.`is`(WitcheryItems.QUARTZ_SPHERE.get()) &&
                item.has(WitcheryDataComponents.HAS_SUN.get()) &&
                item.get(WitcheryDataComponents.HAS_SUN.get()) == true
            ) {
                hasQuartzSphereWithSun = true
            }
        }

        if (hasWineGlassWithBlood) {
            val requiredItems = listOf(
                WitcheryItems.WOOL_OF_BAT.get(),
                WitcheryItems.IMPREGNATED_FABRIC.get(),
                WitcheryItems.ATTUNED_STONE.get()
            )
            return input.items().map { it.item }.containsAll(requiredItems)
        }

        if (hasQuartzSphereWithSun) {
            val requiredItems = listOf(
                WitcheryItems.WOOL_OF_BAT.get(),
                WitcheryItems.IMPREGNATED_FABRIC.get(),
                WitcheryItems.NECROMANTIC_STONE.get()
            )
            return input.items().map { it.item }.containsAll(requiredItems)
        }

        return false
    }

    override fun assemble(input: CraftingInput, registries: HolderLookup.Provider): ItemStack {
        var wineGlass: ItemStack? = null
        var quartzSphere: ItemStack? = null

        for (item in input.items()) {
            if (item.`is`(WitcheryItems.WINE_GLASS.get()) &&
                item.has(WitcheryDataComponents.BLOOD.get()) &&
                item.get(WitcheryDataComponents.BLOOD.get()) != null
            ) {
                wineGlass = item.copy()
            }
            if (item.`is`(WitcheryItems.QUARTZ_SPHERE.get()) &&
                item.has(WitcheryDataComponents.HAS_SUN.get()) &&
                item.get(WitcheryDataComponents.HAS_SUN.get()) == true
            ) {
                quartzSphere = item.copy()
            }
        }

        if (wineGlass != null) {
            val output = ItemStack(WitcheryItems.BLOODSTONE_PENDANT.get())
            return output
        }

        if (quartzSphere != null) {
            val output = ItemStack(WitcheryItems.SUNSTONE_PENDANT.get())
            return output
        }

        return ItemStack.EMPTY
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width >= 2 && height >= 2
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return WitcheryRecipeSerializers.PENDANT_RECIPE_SERIALIZER.get()
    }

    companion object {
        const val NAME: String = "data_transfer"
    }
}