package dev.sterner.witchery.datagen.book.util

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.mojang.datafixers.util.Pair
import net.minecraft.world.item.ItemStack

abstract class BaseEntryProvider(
    protected val id: String,
    protected val icon: ItemStack,
    parent: CategoryProviderBase?
) : EntryProvider(parent) {

    override fun entryName(): String = id.replaceFirstChar { it.uppercaseChar() }
    override fun entryDescription(): String = ""
    override fun entryBackground(): Pair<Int, Int> = EntryBackground.DEFAULT
    override fun entryIcon(): BookIconModel = BookIconModel.create(icon)
    override fun entryId(): String = id
}