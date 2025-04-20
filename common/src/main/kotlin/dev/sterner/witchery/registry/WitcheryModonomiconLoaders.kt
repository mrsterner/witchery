package dev.sterner.witchery.registry

import com.google.gson.JsonObject
import com.klikli_dev.modonomicon.book.page.BookPage
import com.klikli_dev.modonomicon.data.BookPageJsonLoader
import com.klikli_dev.modonomicon.data.LoaderRegistry
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.integration.modonomicon.*
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation

object WitcheryModonomiconLoaders {

    fun register() {
        LoaderRegistry.registerPageLoader(
            WitcheryPageRendererRegistry.CAULDRON_RECIPE,
            BookPageJsonLoader<BookPage> { entryId: ResourceLocation?, json: JsonObject, provider: HolderLookup.Provider? ->
                BookCauldronCraftingRecipePage.fromJson(
                    entryId,
                    json,
                    provider
                )
            } as BookPageJsonLoader<*>
        ) { buffer: RegistryFriendlyByteBuf ->
            BookCauldronCraftingRecipePage.fromNetwork(
                buffer
            )
        }

        LoaderRegistry.registerPageLoader(
            WitcheryPageRendererRegistry.CAULDRON_BREWING_RECIPE,
            BookPageJsonLoader<BookPage> { entryId: ResourceLocation?, json: JsonObject, provider: HolderLookup.Provider? ->
                BookCauldronBrewingRecipePage.fromJson(
                    entryId,
                    json,
                    provider
                )
            } as BookPageJsonLoader<*>
        ) { buffer: RegistryFriendlyByteBuf ->
            BookCauldronBrewingRecipePage.fromNetwork(
                buffer
            )
        }

        LoaderRegistry.registerPageLoader(
            WitcheryPageRendererRegistry.OVEN_FUMING_RECIPE,
            BookPageJsonLoader<BookPage> { entryId: ResourceLocation?, json: JsonObject, provider: HolderLookup.Provider? ->
                BookOvenFumingRecipePage.fromJson(
                    entryId,
                    json,
                    provider
                )
            } as BookPageJsonLoader<*>
        ) { buffer: RegistryFriendlyByteBuf ->
            BookOvenFumingRecipePage.fromNetwork(
                buffer
            )
        }

        LoaderRegistry.registerPageLoader(
            WitcheryPageRendererRegistry.DISTILLING_RECIPE,
            BookPageJsonLoader<BookPage> { entryId: ResourceLocation?, json: JsonObject, provider: HolderLookup.Provider? ->
                BookDistillingRecipePage.fromJson(
                    entryId,
                    json,
                    provider
                )
            } as BookPageJsonLoader<*>
        ) { buffer: RegistryFriendlyByteBuf ->
            BookDistillingRecipePage.fromNetwork(
                buffer
            )
        }

        LoaderRegistry.registerPageLoader(
            WitcheryPageRendererRegistry.RITUAL_RECIPE,
            BookPageJsonLoader<BookPage> { entryId: ResourceLocation?, json: JsonObject, provider: HolderLookup.Provider? ->
                BookRitualRecipePage.fromJson(
                    entryId,
                    json,
                    provider
                )
            } as BookPageJsonLoader<*>
        ) { buffer: RegistryFriendlyByteBuf ->
            BookRitualRecipePage.fromNetwork(
                buffer
            )
        }

        LoaderRegistry.registerPageLoader(
            Witchery.id("potion_model"),
            BookPageJsonLoader<BookPage> { entryId: ResourceLocation, json: JsonObject, provider: HolderLookup.Provider ->
                BookPotionCapacityPage.fromJson(
                    entryId,
                    json,
                    provider
                )
            } as BookPageJsonLoader<*>
        ) { buffer: RegistryFriendlyByteBuf ->
            BookPotionCapacityPage.fromNetwork(
                buffer
            )
        }

        LoaderRegistry.registerPageLoader(
            Witchery.id("potion_effect"),
            BookPageJsonLoader<BookPage> { entryId: ResourceLocation, json: JsonObject, provider: HolderLookup.Provider ->
                BookPotionEffectPage.fromJson(
                    entryId,
                    json,
                    provider
                )
            } as BookPageJsonLoader<*>
        ) { buffer: RegistryFriendlyByteBuf ->
            BookPotionEffectPage.fromNetwork(
                buffer
            )
        }
    }
}