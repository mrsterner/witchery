package dev.sterner.witchery.datagen.book.page

import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel
import dev.sterner.witchery.integration.modonomicon.WitcheryPageRendererRegistry.CAULDRON_RECIPE

class BookCauldronCraftingPageModel : BookRecipePageModel<BookCauldronCraftingPageModel>(CAULDRON_RECIPE) {

    companion object {
        fun create(): BookCauldronCraftingPageModel {
            return BookCauldronCraftingPageModel()
        }


    }
}
