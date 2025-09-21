package dev.sterner.witchery.fabric.datagen.book.util

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAdvancementConditionModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAndConditionModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel
import net.minecraft.resources.ResourceLocation

fun BookEntryModel.requiresAndFollows(parentEntry: BookEntryModel): BookEntryModel {
    this.withCondition(
        BookAndConditionModel.create().withChildren(
            BookEntryReadConditionModel.create()
                .withEntry(parentEntry.id)
        )
    )
    this.addParent(BookEntryParentModel.create(parentEntry.id).withDrawArrow(true))
    return this
}

fun BookEntryModel.requiresAndFollows(parentEntryId: ResourceLocation): BookEntryModel {
    this.withCondition(
        BookAndConditionModel.create().withChildren(
            BookEntryReadConditionModel.create()
                .withEntry(parentEntryId)
        )
    )
    this.addParent(BookEntryParentModel.create(parentEntryId).withDrawArrow(true))
    return this
}

fun BookEntryModel.requiresAndFollows(
    parentEntry: BookEntryModel,
    vararg additionalConditions: BookConditionModel<*>
): BookEntryModel {
    val allConditions = mutableListOf<BookConditionModel<*>>()
    allConditions.add(BookEntryReadConditionModel.create().withEntry(parentEntry.id))
    allConditions.addAll(additionalConditions)

    this.withCondition(
        BookAndConditionModel.create().withChildren(*allConditions.toTypedArray())
    )
    this.addParent(BookEntryParentModel.create(parentEntry.id).withDrawArrow(true))
    return this
}

fun BookEntryModel.alsoFollows(additionalParent: BookEntryModel): BookEntryModel {
    this.addParent(BookEntryParentModel.create(additionalParent.id).withDrawArrow(true))
    return this
}

fun BookEntryModel.alsoFollows(additionalParentId: ResourceLocation): BookEntryModel {
    this.addParent(BookEntryParentModel.create(additionalParentId).withDrawArrow(true))
    return this
}

fun advancement(advancementId: ResourceLocation): BookAdvancementConditionModel {
    return BookAdvancementConditionModel.create().withAdvancementId(advancementId)
}