package dev.sterner.witchery.fabric.datagen.book.util

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.page.*
import net.minecraft.world.item.ItemStack

class EntryBuilder(private val parent: CategoryProviderBase?) {
    private var id: String? = null
    private var icon: ItemStack? = null
    val pages = mutableListOf<PageConfig>()

    data class PageConfig(
        val pageId: String,
        val pageBuilder: () -> BookPageModel<*>
    )

    fun id(id: String) = apply { this.id = id }
    fun icon(icon: ItemStack) = apply { this.icon = icon }

    fun textPage(pageId: String = id!!, titleNum: Int = 1) = apply {
        pages.add(PageConfig(pageId) {
            BookTextPageModel.create()
                .withTitle("${parent?.categoryId()}.$pageId.title.$titleNum")
                .withText("${parent?.categoryId()}.$pageId.page.$titleNum")
        })
    }

    fun textPageFlat(pageId: String = id!!, titleNum: Int = 1) = apply {
        pages.add(PageConfig(pageId) {
            BookTextPageModel.create()
                .withText("${parent?.categoryId()}.$pageId.page.$titleNum")
        })
    }

    fun spotlightPage(pageId: String = id!!, item: ItemStack = icon!!, titleNum: Int = 1) = apply {
        pages.add(PageConfig(pageId) {
            BookSpotlightPageModel.create()
                .withItem(item)
                .withTitle("${parent?.categoryId()}.$pageId.title.$titleNum")
                .withText("${parent?.categoryId()}.$pageId.page.$titleNum")
        })
    }

    fun imagePage(pageId: String, imagePath: String) = apply {
        pages.add(PageConfig(pageId) {
            BookImagePageModel.create()
                .withImages(Witchery.id(imagePath))
        })
    }

    fun recipePage(
        pageId: String = "${parent?.categoryId()}.$id",
        recipeType: String,
        recipeId: String = "$recipeType/$id"
    ) = apply {
        pages.add(PageConfig(pageId) {
            createRecipePage(recipeType, recipeId, pageId)
        })
    }

    fun potionPage(
        pageId: String,
        title: String? = null,
        configure: BookPotionPageModel.() -> Unit
    ) = apply {
        pages.add(PageConfig(pageId) {
            BookPotionPageModel.create().apply {
                title?.let { withTitle(it) }
                configure()
            }
        })
    }

    private fun createRecipePage(recipeType: String, recipeId: String, pageId: String): BookPageModel<*> {
        val pageModel = when (recipeType) {
            "brazier_summoning" -> BookBrazierSummoningPageModel.create()
            "cauldron_brewing" -> BookCauldronBrewingPageModel.create()
            "cauldron_crafting" -> BookCauldronCraftingPageModel.create()
            "distillery_crafting" -> BookDistillingPageModel.create()
            "oven_fuming" -> BookOvenFumingPageModel.create()
            else -> throw IllegalArgumentException("Unknown recipe type: $recipeType")
        }

        return pageModel
            .withText("${parent?.categoryId()}.$id.title.1")
            .withRecipeId1(Witchery.id(recipeId))
            .withTitle1(pageId)
    }

    fun build(): EntryProvider {
        requireNotNull(id) { "ID must be set" }
        requireNotNull(icon) { "Icon must be set" }

        return object : BaseEntryProvider(id!!, icon!!, parent) {
            override fun generatePages() {
                pages.forEach { pageConfig ->
                    this.page(pageConfig.pageId) { pageConfig.pageBuilder() }
                }
            }
        }
    }

    fun cauldronCraftingPage(
        pageId: String,
        recipeId: String,
        titleKey: String? = null
    ) = apply {
        pages.add(PageConfig(pageId) {
            BookCauldronCraftingPageModel.create()
                .withRecipeId1(Witchery.id("cauldron_crafting/$recipeId"))
                .apply {
                    titleKey?.let { withTitle1(it) }
                    withText("${parent?.categoryId()}.$pageId.title.1")
                }
        })
    }

}

fun CategoryProviderBase?.entry(configure: EntryBuilder.() -> Unit): EntryProvider {
    return EntryBuilder(this).apply(configure).build()
}

