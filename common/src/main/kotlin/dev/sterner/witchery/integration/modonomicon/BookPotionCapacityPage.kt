package dev.sterner.witchery.integration.modonomicon

import com.google.gson.JsonObject
import com.klikli_dev.modonomicon.book.BookTextHolder
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder
import com.klikli_dev.modonomicon.book.conditions.BookCondition
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition
import com.klikli_dev.modonomicon.book.page.BookPage
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer
import com.klikli_dev.modonomicon.util.BookGsonHelper
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import net.minecraft.client.resources.language.I18n
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.chat.Style
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.GsonHelper
import net.minecraft.world.item.ItemStack
import java.util.*

class BookPotionCapacityPage(
    var title: BookTextHolder,
    var text: BookTextHolder,
    var items: MutableList<Pair<ItemStack, BookTextHolder>>,
    anchor: String?,
    condition: BookCondition?
) :
    BookPage(anchor, condition) {

    fun hasTitle(): Boolean {
        return !title.isEmpty
    }

    override fun getType(): ResourceLocation {
        return Witchery.id("potion_model")
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
        ITEMS_WITH_TEXT_LIST_STREAM_CODEC.encode(buffer, items)
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

        val CODEC: Codec<BookTextHolder> = Codec.either(
            ComponentSerialization.CODEC,
            Codec.STRING
        ).xmap(
            { either -> either.map(::BookTextHolder, ::BookTextHolder) },
            { holder -> if (holder.hasComponent()) Either.left(holder.component) else Either.right(holder.string) }
        )

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BookTextHolder> =
            StreamCodec.of(
                { buffer, value -> value.toNetwork(buffer) },
                { buffer -> BookTextHolder.fromNetwork(buffer) }
            )

        private val ITEM_TEXT_PAIR_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Pair<ItemStack, BookTextHolder>> =
            StreamCodec.composite(
                ItemStack.STREAM_CODEC,
                { it.first },
                STREAM_CODEC,
                { it.second },
                ::Pair
            )

        private val BOOK_TEXT_PAIR_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Pair<BookTextHolder, BookTextHolder>> =
            StreamCodec.composite(
                STREAM_CODEC,
                { it.first },
                STREAM_CODEC,
                { it.second },
                ::Pair
            )

        private val ITEM_TEXT_TEXT_PAIR_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Pair<ItemStack, Pair<BookTextHolder, BookTextHolder>>> =
            StreamCodec.composite(
                ItemStack.STREAM_CODEC,
                { it.first },
                BOOK_TEXT_PAIR_STREAM_CODEC,
                { it.second },
                ::Pair
            )


        val ITEMS_WITH_TEXT_LIST_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, List<Pair<ItemStack, BookTextHolder>>> =
            StreamCodec.of(
                { buffer, list ->
                    buffer.writeVarInt(list.size)
                    list.forEach { ITEM_TEXT_PAIR_STREAM_CODEC.encode(buffer, it) }
                },
                { buffer ->
                    val size = buffer.readVarInt()
                    List(size) { ITEM_TEXT_PAIR_STREAM_CODEC.decode(buffer) }
                }
            )

        val ITEMS_WITH_TEXT_TEXT_LIST_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, List<Pair<ItemStack, Pair<BookTextHolder, BookTextHolder>>>> =
            StreamCodec.of(
                { buffer, list ->
                    buffer.writeVarInt(list.size)
                    list.forEach { ITEM_TEXT_TEXT_PAIR_STREAM_CODEC.encode(buffer, it) }
                },
                { buffer ->
                    val size = buffer.readVarInt()
                    List(size) { ITEM_TEXT_TEXT_PAIR_STREAM_CODEC.decode(buffer) }
                }
            )

        private val ITEM_TEXT_PAIR_CODEC: Codec<Pair<ItemStack, BookTextHolder>> =
            RecordCodecBuilder.create { instance ->
                instance.group(
                    ItemStack.CODEC.fieldOf("item").forGetter { it.first },
                    CODEC.fieldOf("text").forGetter { it.second }
                ).apply(instance, ::Pair)
            }

        private val TEXT_PAIR_PAIR_CODEC: Codec<Pair<BookTextHolder, BookTextHolder>> =
            RecordCodecBuilder.create { instance ->
                instance.group(
                    CODEC.fieldOf("text").forGetter { it.first },
                    CODEC.fieldOf("text_title").forGetter { it.second }
                ).apply(instance, ::Pair)
            }

        private val ITEM_TEXT_PAIR_PAIR_CODEC: Codec<Pair<ItemStack, Pair<BookTextHolder, BookTextHolder>>> =
            RecordCodecBuilder.create { instance ->
                instance.group(
                    ItemStack.CODEC.fieldOf("item").forGetter { it.first },
                    TEXT_PAIR_PAIR_CODEC.fieldOf("texts").forGetter { it.second }
                ).apply(instance, ::Pair)
            }

        val ITEMS_WITH_TEXT_LIST_CODEC: Codec<List<Pair<ItemStack, BookTextHolder>>> =
            ITEM_TEXT_PAIR_CODEC.listOf()

        val ITEMS_WITH_TEXT_LIST_PAIR_CODEC: Codec<List<Pair<ItemStack, Pair<BookTextHolder, BookTextHolder>>>> =
            ITEM_TEXT_PAIR_PAIR_CODEC.listOf()


        fun fromJson(
            entryId: ResourceLocation?,
            json: JsonObject,
            provider: HolderLookup.Provider
        ): BookPotionCapacityPage {
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

                val itemText = BookGsonHelper.getAsBookTextHolder(obj, "text", BookTextHolder.EMPTY, provider)
                stack to itemText
            }.toMutableList()

            return BookPotionCapacityPage(title, text, itemList, anchor, condition)
        }


        fun fromNetwork(buffer: RegistryFriendlyByteBuf): BookPotionCapacityPage {
            val title = BookTextHolder.fromNetwork(buffer)
            val itemList = ITEMS_WITH_TEXT_LIST_STREAM_CODEC.decode(buffer)
            val text = BookTextHolder.fromNetwork(buffer)
            val anchor = buffer.readUtf()
            val condition = BookCondition.fromNetwork(buffer)
            return BookPotionCapacityPage(title, text, itemList.toMutableList(), anchor, condition)
        }
    }
}
