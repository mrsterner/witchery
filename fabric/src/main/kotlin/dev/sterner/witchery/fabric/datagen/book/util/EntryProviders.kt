package dev.sterner.witchery.fabric.datagen.book.util

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.page.BookRitualPageModel
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

object EntryProviders {

    fun single(parent: CategoryProviderBase?, id: String, icon: Item) =
        single(parent, id, ItemStack(icon))

    fun single(parent: CategoryProviderBase?, id: String, icon: ItemStack) =
        parent.entry {
            id(id)
            icon(icon)
            textPage()
        }

    fun double(parent: CategoryProviderBase?, id: String, icon: Item) =
        double(parent, id, ItemStack(icon))

    fun double(parent: CategoryProviderBase?, id: String, icon: ItemStack) =
        parent.entry {
            id(id)
            icon(icon)
            textPage(titleNum = 1)
            textPage("${id}_2", titleNum = 2)
        }

    fun singleItem(parent: CategoryProviderBase?, id: String, icon: Item) =
        singleItem(parent, id, ItemStack(icon))

    fun singleItem(parent: CategoryProviderBase?, id: String, icon: ItemStack) : EntryProvider {
        return EntryBuilder(parent)
            .id(id)
            .icon(icon)
            .spotlightPage()
            .build()
    }

    fun doubleItem(parent: CategoryProviderBase?,
                   id: String,
                   item1: Item,
                   item2: Item,
                   noSecondTitle: Boolean = false) =
        doubleItem(parent, id, ItemStack(item1), ItemStack(item2), noSecondTitle)


    fun doubleItem(
        parent: CategoryProviderBase?,
        id: String,
        item1: ItemStack,
        item2: ItemStack,
        noSecondTitle: Boolean = false
    ) = parent.entry {
        id(id)
        icon(item1)
        spotlightPage(item = item1, titleNum = 1)
        if (noSecondTitle) {
            pages.add(EntryBuilder.PageConfig("${id}_2") {
                BookSpotlightPageModel.create().withItem(item2)
            })
        } else {
            spotlightPage("${id}_2", item2, titleNum = 2)
        }
    }

    fun recipe(parent: CategoryProviderBase?, id: String, icon: Item, recipeType: String) =
        recipe(parent, id, ItemStack(icon), recipeType)

    fun recipe(parent: CategoryProviderBase?, id: String, icon: ItemStack, recipeType: String) =
        parent.entry {
            id(id)
            icon(icon)
            textPage()
            recipePage(recipeType = recipeType)
        }

    fun spotlightWithImage(
        parent: CategoryProviderBase?,
        id: String,
        icon: ItemStack,
        imagePath: String
    ) = parent.entry {
        id(id)
        icon(icon)
        spotlightPage()
        imagePage("${id}_image", imagePath)
    }

    fun textWithImage(
        parent: CategoryProviderBase?,
        id: String,
        icon: Item
    ) = textWithImage(parent, id, ItemStack(icon))


    fun textWithImage(
        parent: CategoryProviderBase?,
        id: String,
        icon: ItemStack
    ) = parent.entry {
        id(id)
        icon(icon)
        textPage()
        imagePage("${id}_image", "textures/gui/modonomicon/images/${id}.png")
    }

    fun spotlightWithCauldronRecipes(
        parent: CategoryProviderBase?,
        id: String,
        icon: Item,
        vararg recipeIds: String
    ) = spotlightWithCauldronRecipes(parent, id, ItemStack(icon), *recipeIds)


    fun spotlightWithCauldronRecipes(
        parent: CategoryProviderBase?,
        id: String,
        icon: ItemStack,
        vararg recipeIds: String
    ): EntryProvider {
        return EntryBuilder(parent)
            .id(id)
            .icon(icon)
            .spotlightPage()
            .apply {
                recipeIds.forEach { recipeId ->
                    cauldronCraftingPage(
                        pageId = "${parent?.categoryId()}.$id.$recipeId",
                        recipeId = recipeId,
                        titleKey = "${parent?.categoryId()}.$id.$recipeId.title.1"
                    )
                }
            }
            .build()
    }

    fun ritual(parent: CategoryProviderBase?, id: String, icon: Item) =
        ritual(parent, id, ItemStack(icon))

    fun ritual(parent: CategoryProviderBase?, id: String, icon: ItemStack): EntryProvider {
        return EntryBuilder(parent)
            .id(id)
            .icon(icon)
            .textPage()
            .apply {
                pages.add(EntryBuilder.PageConfig("${id}_2") {
                    BookRitualPageModel.Companion.create()
                        .withRecipeId1(Witchery.id("ritual/$id"))
                        .withText("${parent?.categoryId()}.$id.page.2")
                })
            }
            .build()
    }
}