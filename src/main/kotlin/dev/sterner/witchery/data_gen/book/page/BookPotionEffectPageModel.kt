package dev.sterner.witchery.data_gen.book.page

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel
import com.klikli_dev.modonomicon.book.BookTextHolder
import com.mojang.serialization.JsonOps
import dev.sterner.witchery.Witchery
import net.minecraft.core.HolderLookup
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike

class BookPotionEffectPageModel :
    BookPageModel<BookPotionEffectPageModel?>(Witchery.id("potion_effect")) {

    var items: MutableList<Pair<ItemStack, Triple<Int, BookTextHolder, BookTextHolder>>> = mutableListOf()
    var title: BookTextHolderModel = BookTextHolderModel("")
    var text: BookTextHolderModel = BookTextHolderModel("")

    override fun toJson(entryId: ResourceLocation, provider: HolderLookup.Provider): JsonObject {
        val json = super.toJson(entryId, provider)
        json.add("title", title.toJson(provider))

        val itemsArray = JsonArray()
        for ((stack, triple) in this.items) {
            val itemObj = JsonObject()

            val stackJson = ItemStack.CODEC.encodeStart(
                provider.createSerializationContext(JsonOps.INSTANCE),
                stack
            ).getOrThrow()
            itemObj.add("item", stackJson)

            itemObj.addProperty("capacity", triple.first)

            val textsObj = JsonObject()

            if (triple.second.hasComponent()) {
                textsObj.add("text", ComponentSerialization.CODEC.encodeStart(
                    provider.createSerializationContext(JsonOps.INSTANCE),
                    triple.second.component
                ).getOrThrow())
            } else {
                textsObj.addProperty("text", triple.second.string)
            }

            // For text_title
            if (triple.third.hasComponent()) {
                textsObj.add("text_title", ComponentSerialization.CODEC.encodeStart(
                    provider.createSerializationContext(JsonOps.INSTANCE),
                    triple.third.component
                ).getOrThrow())
            } else {
                textsObj.addProperty("text_title", triple.third.string)
            }

            itemObj.add("texts", textsObj)

            itemsArray.add(itemObj)
        }
        json.add("items", itemsArray)

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

    fun addItem(item: ItemStack, capacity: Int): BookPotionEffectPageModel {
        this.addItem(
            item,
            capacity,
            BookTextHolder(
                Component.translatable("potion_effect." + item.item.toString().substringAfter(":"))
            ),
            BookTextHolder(
                Component.translatable("potion_effect." + item.item.toString().substringAfter(":") + ".title.1")
            )
        )
        return this
    }

    fun addItem(item: ItemStack, capacity: Int, holder: BookTextHolder, holderTitle: BookTextHolder): BookPotionEffectPageModel {
        this.items.add(item to Triple(capacity, holder, holderTitle))
        return this
    }

    fun addItem(item: ItemStack, capacity: Int, text: Component, textTitle: Component): BookPotionEffectPageModel {
        this.items.add(item to Triple(capacity, BookTextHolder(text), BookTextHolder(textTitle)))
        return this
    }

    fun addItem(item: ItemLike, capacity: Int, text: Component, textTitle: Component): BookPotionEffectPageModel {
        return addItem(ItemStack(item), capacity, text, textTitle)
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