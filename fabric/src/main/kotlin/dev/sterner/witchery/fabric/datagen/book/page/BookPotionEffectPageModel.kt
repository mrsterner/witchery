package dev.sterner.witchery.fabric.datagen.book.page

import com.google.gson.JsonObject
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel
import com.klikli_dev.modonomicon.book.BookTextHolder
import com.mojang.serialization.JsonOps
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.integration.modonomicon.BookPotionCapacityPage
import net.minecraft.core.HolderLookup
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike

class BookPotionEffectPageModel :
    BookPageModel<BookPotionEffectPageModel?>(Witchery.id("potion_effect")) {

    var items: MutableList<Pair<ItemStack, Pair<BookTextHolder, BookTextHolder>>> = mutableListOf()
    var title: BookTextHolderModel = BookTextHolderModel("")
    var text: BookTextHolderModel = BookTextHolderModel("")

    override fun toJson(entryId: ResourceLocation, provider: HolderLookup.Provider): JsonObject {
        val json = super.toJson(entryId, provider)
        json.add("title", title.toJson(provider))
        json.add(
            "items",
            BookPotionCapacityPage.ITEMS_WITH_TEXT_LIST_PAIR_CODEC.encodeStart(
                provider.createSerializationContext(JsonOps.INSTANCE),
                this.items
            ).getOrThrow()
        )
        json.add("text", text.toJson(provider))
        return json
    }

    fun withTitle(title: String?): BookPotionEffectPageModel {
        this.title = BookTextHolderModel(title!!)
        return this
    }

    fun withTitle(title: Component?): BookPotionEffectPageModel {
        this.title = BookTextHolderModel(title)
        return this
    }

    fun addItem(item: ItemStack): BookPotionEffectPageModel {
        this.addItem(
            item, BookTextHolder(
                Component.translatable("potion_effect." + item.item.toString().substringAfter(":"))
            ), BookTextHolder(
                Component.translatable("potion_effect." + item.item.toString().substringAfter(":") + ".title.1")
            )
        )
        return this
    }

    fun addItem(item: ItemStack, holder: BookTextHolder, holderTitle: BookTextHolder): BookPotionEffectPageModel {
        this.items.add(item to (holder to holderTitle))
        return this
    }

    fun addItem(item: ItemStack, text: Component, textTitle: Component): BookPotionEffectPageModel {
        this.items.add(item to (BookTextHolder(text) to (BookTextHolder(textTitle))))
        return this
    }

    fun addItem(item: ItemLike, text: Component, textTitle: Component): BookPotionEffectPageModel {
        return addItem(ItemStack(item), text, textTitle)
    }

    fun withText(text: String?): BookPotionEffectPageModel {
        this.text = BookTextHolderModel(text!!)
        return this
    }

    fun withText(text: Component?): BookPotionEffectPageModel {
        this.text = BookTextHolderModel(text)
        return this
    }

    companion object {
        fun create(): BookPotionEffectPageModel {
            return BookPotionEffectPageModel()
        }
    }
}
