package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.ritual.RitualRecipe
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import mezz.jei.api.runtime.IJeiRuntime
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.ResourceLocation
import java.util.stream.Collectors


@JeiPlugin
class WitcheryJeiPlugin : IModPlugin {

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        val guiHelper = registration.jeiHelpers.guiHelper

        registration.addRecipeCategories(RitualJeiRecipeCategory(guiHelper))
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val level: ClientLevel? = Minecraft.getInstance().level
        if (level != null) {
            registration.addRecipes(RITUAL,
                level.recipeManager.getAllRecipesFor(WitcheryRecipeTypes.RITUAL_RECIPE_TYPE.get())
                    .stream().map { it.value }.collect(Collectors.toList())
            )
        }
    }

    companion object {
        val ID: ResourceLocation = Witchery.id("main")

        val RITUAL: RecipeType<RitualRecipe> = RecipeType(Witchery.id("ritual"), RitualRecipe::class.java)
    }

    override fun getPluginUid(): ResourceLocation {
        return ID
    }
}