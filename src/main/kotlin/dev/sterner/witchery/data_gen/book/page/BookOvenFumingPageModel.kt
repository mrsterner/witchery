package dev.sterner.witchery.data_gen.book.page

import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel
import dev.sterner.witchery.integration.modonomicon.WitcheryPageRendererRegistry.OVEN_FUMING_RECIPE

class BookOvenFumingPageModel : BookRecipePageModel<BookOvenFumingPageModel>(OVEN_FUMING_RECIPE) {

    companion object {
        fun create(): BookOvenFumingPageModel {
            return BookOvenFumingPageModel()
        }


    }
}
