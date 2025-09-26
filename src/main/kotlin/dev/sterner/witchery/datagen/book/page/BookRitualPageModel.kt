package dev.sterner.witchery.datagen.book.page

import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel
import dev.sterner.witchery.integration.modonomicon.WitcheryPageRendererRegistry

class BookRitualPageModel : BookRecipePageModel<BookRitualPageModel>(WitcheryPageRendererRegistry.RITUAL_RECIPE) {

    companion object {
        fun create(): BookRitualPageModel {
            return BookRitualPageModel()
        }
    }
}