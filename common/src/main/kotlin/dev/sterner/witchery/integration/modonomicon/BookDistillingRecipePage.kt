package dev.sterner.witchery.integration.modonomicon

import com.google.gson.JsonObject
import com.klikli_dev.modonomicon.book.BookTextHolder
import com.klikli_dev.modonomicon.book.conditions.BookCondition
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition
import com.klikli_dev.modonomicon.book.page.BookProcessingRecipePage
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipe
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.GsonHelper

class BookDistillingRecipePage(
    title1: BookTextHolder?,
    recipeId1: ResourceLocation?,
    title2: BookTextHolder?,
    recipeId2: ResourceLocation?,
    text: BookTextHolder?,
    anchor: String?,
    condition: BookCondition?
) :
    BookProcessingRecipePage<DistilleryCraftingRecipe>(
        WitcheryRecipeTypes.DISTILLERY_RECIPE_TYPE.get(),
        title1,
        recipeId1,
        title2,
        recipeId2,
        text,
        anchor,
        condition
    ) {
    override fun getType(): ResourceLocation {
        return WitcheryPageRendererRegistry.DISTILLING_RECIPE
    }

    companion object {
        fun fromJson(
            entryId: ResourceLocation?,
            json: JsonObject,
            provider: HolderLookup.Provider?
        ): BookDistillingRecipePage {
            val common = commonFromJson(json, provider)
            val anchor = GsonHelper.getAsString(json, "anchor", "")
            val condition = if (json.has("condition")
            ) BookCondition.fromJson(entryId, json.getAsJsonObject("condition"), provider)
            else BookNoneCondition()
            return BookDistillingRecipePage(
                common.title1(),
                common.recipeId1(),
                common.title2(),
                common.recipeId2(),
                common.text(),
                anchor,
                condition
            )
        }

        fun fromNetwork(buffer: RegistryFriendlyByteBuf): BookDistillingRecipePage {
            val common = commonFromNetwork(buffer)
            val anchor = buffer.readUtf()
            val condition = BookCondition.fromNetwork(buffer)
            return BookDistillingRecipePage(
                common.title1(),
                common.recipeId1(),
                common.title2(),
                common.recipeId2(),
                common.text(),
                anchor,
                condition
            )
        }
    }
}
