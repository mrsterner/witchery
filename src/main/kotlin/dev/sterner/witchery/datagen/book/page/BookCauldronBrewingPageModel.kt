package dev.sterner.witchery.datagen.book.page

import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel
import dev.sterner.witchery.integration.modonomicon.WitcheryPageRendererRegistry.CAULDRON_BREWING_RECIPE

class BookCauldronBrewingPageModel : BookRecipePageModel<BookCauldronBrewingPageModel>(CAULDRON_BREWING_RECIPE) {

    companion object {
        fun create(): BookCauldronBrewingPageModel {
            return BookCauldronBrewingPageModel()
        }
    }
}
