package dev.sterner.witchery.datagen.book.page

import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel
import dev.sterner.witchery.integration.modonomicon.WitcheryPageRendererRegistry.DISTILLING_RECIPE

class BookDistillingPageModel : BookRecipePageModel<BookDistillingPageModel>(DISTILLING_RECIPE) {

    companion object {
        fun create(): BookDistillingPageModel {
            return BookDistillingPageModel()
        }
    }
}
