package dev.sterner.witchery.datagen.book.page

import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel
import dev.sterner.witchery.integration.modonomicon.WitcheryPageRendererRegistry

class BookBrazierSummoningPageModel :
    BookRecipePageModel<BookBrazierSummoningPageModel>(WitcheryPageRendererRegistry.BRAZIER_RECIPE) {

    companion object {
        fun create(): BookBrazierSummoningPageModel {
            return BookBrazierSummoningPageModel()
        }
    }
}
