package dev.sterner.witchery.fabric.datagen.book.page

import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.integration.modonomicon.WitcheryPageRendererRegistry.CAULDRON_RECIPE
import net.minecraft.resources.ResourceLocation

class BookCauldronCraftingPageModel : BookRecipePageModel<BookCauldronCraftingPageModel>(CAULDRON_RECIPE) {

    companion object {
        fun create(): BookCauldronCraftingPageModel {
            return BookCauldronCraftingPageModel()
        }


    }
}
