package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.Witchery
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import mezz.jei.api.runtime.IJeiRuntime
import net.minecraft.resources.ResourceLocation

@JeiPlugin
class WitcheryJeiPlugin : IModPlugin {
    override fun registerCategories(registration: IRecipeCategoryRegistration) {

    }

    override fun registerRecipes(registration: IRecipeRegistration) {

    }

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {

        println("JEI_RUNTIME")
    }

    companion object {
        val ID: ResourceLocation = Witchery.id("main")
    }

    override fun getPluginUid(): ResourceLocation {
        return ID
    }
}