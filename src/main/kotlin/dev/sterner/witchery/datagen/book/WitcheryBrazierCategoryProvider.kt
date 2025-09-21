package dev.sterner.witchery.fabric.datagen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAdvancementConditionModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.entry.SoulCageEntryProvider
import dev.sterner.witchery.fabric.datagen.book.util.EntryProviders
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.alchemy.Potions


class WitcheryBrazierCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "brazier"
    }

    override fun generateEntryMap(): Array<String> {
        return arrayOf(
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "_________________t________________",
            "_______________p__________________",
            "__________________________________",
            "_______________b_s_i______________",
            "__________________________________",
            "_______________c___j______________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________"

        )
    }

    override fun generateEntries() {
        var index = 0

        fun addEntry(entry: BookEntryModel) {
            this.add(entry.withSortNumber(index))
            index++
        }

        val brazier = EntryProviders.singleItem(this, "brazier", WitcheryItems.BRAZIER.get()).generate("b")
        brazier
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("brazier"))
            )

        addEntry(brazier)

        val potionItem = PotionContents.createItemStack(Items.POTION, Potions.SWIFTNESS)

        val potion = EntryProviders.singleItem(this, "potion", potionItem).generate("p")
        potion.addParent(BookEntryParentModel.create(brazier.id).withDrawArrow(true))

        addEntry(potion)

        val summon = EntryProviders.singleItem(this, "summon", WitcheryItems.WORMWOOD.get()).generate("s")
        summon.addParent(BookEntryParentModel.create(brazier.id).withDrawArrow(true))
        addEntry(summon)

        val censer = EntryProviders.singleItemTwoPages(this, "censer", WitcheryItems.CENSER.get().defaultInstance).generate("c")
        censer.addParent(BookEntryParentModel.create(brazier.id).withDrawArrow(true))
        addEntry(censer)

        val soulCage = SoulCageEntryProvider(this, "soul_cage").generate("t")
        soulCage.addParent(BookEntryParentModel.create(summon.id).withDrawArrow(true))
        addEntry(soulCage)

        val banshee = EntryProviders.recipe(this, "summon_banshee", WitcheryItems.CONDENSED_FEAR.get(), "brazier_summoning").generate("i")
        banshee.addParent(BookEntryParentModel.create(summon.id).withDrawArrow(true))

        addEntry(banshee)

        val spectre = EntryProviders.recipe(this, "summon_spectre", WitcheryItems.WOOL_OF_BAT.get(), "brazier_summoning").generate("j")
        spectre.addParent(BookEntryParentModel.create(summon.id).withDrawArrow(true))

        addEntry(spectre)
    }

    override fun categoryName(): String {
        return "brazier"
    }

    override fun categoryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.BRAZIER.get())
    }

    override fun additionalSetup(category: BookCategoryModel?): BookCategoryModel {
        return super.additionalSetup(category)
            .withCondition(BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("brazier")))
            .withBackground(Witchery.id("textures/gui/modonomicon/parallax.png"))
    }
}