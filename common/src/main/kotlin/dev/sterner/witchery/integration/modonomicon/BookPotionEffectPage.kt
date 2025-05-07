package dev.sterner.witchery.integration.modonomicon

import com.google.gson.JsonObject
import com.klikli_dev.modonomicon.book.BookTextHolder
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder
import com.klikli_dev.modonomicon.book.conditions.BookCondition
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition
import com.klikli_dev.modonomicon.book.page.BookPage
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer
import com.klikli_dev.modonomicon.util.BookGsonHelper
import com.mojang.serialization.JsonOps
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.integration.modonomicon.BookPotionCapacityPage.Companion.ITEMS_WITH_TEXT_TEXT_LIST_STREAM_CODEC
import net.minecraft.client.resources.language.I18n
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.GsonHelper
import net.minecraft.world.item.ItemStack
import java.util.*

class BookPotionEffectPage(
    var title: BookTextHolder,
    var text: BookTextHolder,
    var items: MutableList<Pair<ItemStack, Pair<BookTextHolder, BookTextHolder>>>,
    anchor: String?,
    condition: BookCondition?
) :
    BookPage(anchor, condition) {

    fun hasTitle(): Boolean {
        return !title.isEmpty
    }

    override fun getType(): ResourceLocation {
        return Witchery.id("potion_effect")
    }

    override fun prerenderMarkdown(textRenderer: BookTextRenderer) {
        super.prerenderMarkdown(textRenderer)

        if (!title.hasComponent()) {
            this.title = BookTextHolder(
                Component.translatable(
                    title.key
                )
                    .withStyle(
                        Style.EMPTY
                            .withBold(true)
                            .withColor(getParentEntry().book.defaultTitleColor)
                    )
            )
        }
        if (!text.hasComponent()) {
            this.text = RenderedBookTextHolder(
                this.text, textRenderer.render(
                    text.string
                )
            )
        }
    }

    override fun toNetwork(buffer: RegistryFriendlyByteBuf) {
        title.toNetwork(buffer)
        ITEMS_WITH_TEXT_TEXT_LIST_STREAM_CODEC.encode(buffer, items)
        text.toNetwork(buffer)
        buffer.writeUtf(anchor ?: "")
        BookCondition.toNetwork(condition, buffer)
    }


    override fun matchesQuery(query: String): Boolean {
        return (title.string.lowercase(Locale.getDefault()).contains(query)
                || this.itemStackMatchesQuery(query)
                || text.string.lowercase(Locale.getDefault()).contains(query))
    }

    fun itemStackMatchesQuery(query: String?): Boolean {
        if (query.isNullOrBlank()) return true

        return items.any { (stack, _) ->
            this.matchesQuery(stack, query)
        }
    }

    fun matchesQuery(stack: ItemStack, query: String?): Boolean {
        return I18n.get(stack.descriptionId).lowercase(Locale.getDefault()).contains(
            query!!
        )
    }

    companion object {

        fun fromJson(
            entryId: ResourceLocation?,
            json: JsonObject,
            provider: HolderLookup.Provider
        ): BookPotionEffectPage {
            val title = BookGsonHelper.getAsBookTextHolder(json, "title", BookTextHolder.EMPTY, provider)
            val text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY, provider)
            val anchor = GsonHelper.getAsString(json, "anchor", "")
            val condition = if (json.has("condition"))
                BookCondition.fromJson(entryId, json.getAsJsonObject("condition"), provider)
            else BookNoneCondition()

            val itemList = GsonHelper.getAsJsonArray(json, "items").map { element ->
                val obj = element.asJsonObject
                val stack = ItemStack.CODEC.parse(
                    provider.createSerializationContext(JsonOps.INSTANCE),
                    obj.get("item")
                ).result().orElse(ItemStack.EMPTY)

                val textsObj = obj.getAsJsonObject("texts")
                val itemText = BookGsonHelper.getAsBookTextHolder(textsObj, "text", BookTextHolder.EMPTY, provider)
                val itemText1 =
                    BookGsonHelper.getAsBookTextHolder(textsObj, "text_title", BookTextHolder.EMPTY, provider)

                stack to (itemText to itemText1)
            }.toMutableList()

            return BookPotionEffectPage(title, text, itemList, anchor, condition)
        }


        fun fromNetwork(buffer: RegistryFriendlyByteBuf): BookPotionEffectPage {
            val title = BookTextHolder.fromNetwork(buffer)
            val itemList = ITEMS_WITH_TEXT_TEXT_LIST_STREAM_CODEC.decode(buffer)
            val text = BookTextHolder.fromNetwork(buffer)
            val anchor = buffer.readUtf()
            val condition = BookCondition.fromNetwork(buffer)
            return BookPotionEffectPage(title, text, itemList.toMutableList(), anchor, condition)
        }
    }
}
