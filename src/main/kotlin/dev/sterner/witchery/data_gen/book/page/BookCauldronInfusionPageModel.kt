package dev.sterner.witchery.data_gen.book.page

import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel
import dev.sterner.witchery.integration.modonomicon.WitcheryPageRendererRegistry.CAULDRON_INFUSION_RECIPE

class BookCauldronInfusionPageModel : BookRecipePageModel<BookCauldronInfusionPageModel>(CAULDRON_INFUSION_RECIPE) {

    companion object {
        fun create(): BookCauldronInfusionPageModel {
            return BookCauldronInfusionPageModel()
        }
    }
}
