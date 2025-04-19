package dev.sterner.witchery.fabric.datagen.book.page

import com.google.gson.JsonObject
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel
import com.klikli_dev.modonomicon.book.BookTextHolder
import com.mojang.serialization.JsonOps
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.integration.modonomicon.BookPotionPage
import net.minecraft.core.HolderLookup
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike

class BookPotionPageModel :
    BookPageModel<BookPotionPageModel?>(Witchery.id("potion_model")) {

    var items: MutableList<Pair<ItemStack, BookTextHolder>> = mutableListOf()
    var title: BookTextHolderModel = BookTextHolderModel("")
    var text: BookTextHolderModel = BookTextHolderModel("")

    override fun toJson(entryId: ResourceLocation, provider: HolderLookup.Provider): JsonObject {
        val json = super.toJson(entryId, provider)
        json.add("title", title.toJson(provider))
        json.add(
            "items",
            BookPotionPage.ITEMS_WITH_TEXT_LIST_CODEC.encodeStart(
                provider.createSerializationContext(JsonOps.INSTANCE),
                this.items
            ).getOrThrow()
        )
        json.add("text", text.toJson(provider))
        return json
    }

    fun withTitle(title: String?): BookPotionPageModel {
        this.title = BookTextHolderModel(title!!)
        return this
    }

    fun withTitle(title: Component?): BookPotionPageModel {
        this.title = BookTextHolderModel(title)
        return this
    }

    fun addItem(item: ItemStack, holder: BookTextHolder): BookPotionPageModel {
        this.items.add(item to holder)
        return this
    }

    fun addItem(item: ItemStack, text: Component): BookPotionPageModel {
        this.items.add(item to BookTextHolder(text))
        return this
    }

    fun addItem(item: ItemLike, text: Component): BookPotionPageModel {
        return addItem(ItemStack(item), text)
    }

    fun withText(text: String?): BookPotionPageModel {
        this.text = BookTextHolderModel(text!!)
        return this
    }

    fun withText(text: Component?): BookPotionPageModel {
        this.text = BookTextHolderModel(text)
        return this
    }

    companion object {
        fun create(): BookPotionPageModel {
            return BookPotionPageModel()
        }
    }
}
