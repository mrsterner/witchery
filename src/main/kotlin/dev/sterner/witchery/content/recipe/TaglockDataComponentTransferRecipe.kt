package dev.sterner.witchery.content.recipe

import dev.sterner.witchery.content.item.PoppetItem
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.registry.WitcheryRecipeSerializers
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level

class TaglockDataComponentTransferRecipe : CustomRecipe(CraftingBookCategory.MISC) {

    override fun matches(input: CraftingInput, level: Level): Boolean {
        var hasTaglock = false
        var hasPoppet = false

        for (item in input.items()) {
            if (item.`is`(WitcheryItems.TAGLOCK.get())) {
                hasTaglock = true
            }
            if (item.item is PoppetItem) {
                hasPoppet = true
            }
        }

        return hasTaglock && hasPoppet
    }

    override fun assemble(input: CraftingInput, registries: HolderLookup.Provider): ItemStack {
        var poppetItem: ItemStack? = null
        val taglockItems = mutableListOf<ItemStack>()

        for (item in input.items()) {
            if (item.item is PoppetItem) {
                poppetItem = item.copy()
            } else if (item.`is`(WitcheryItems.TAGLOCK.get())) {
                taglockItems.add(item.copy())
            }
        }

        if (poppetItem == null || taglockItems.isEmpty()) {
            return ItemStack.EMPTY
        }

        val isVampiricPoppet = poppetItem.`is`(WitcheryItems.VAMPIRIC_POPPET.get())

        if (isVampiricPoppet && taglockItems.size == 2) {
            val positions = mutableMapOf<Int, ItemStack>()

            for (i in 0 until input.size()) {
                val item = input.getItem(i)
                if (item.`is`(WitcheryItems.TAGLOCK.get())) {
                    positions[i] = item
                } else if (item.item is PoppetItem) {
                    positions[i] = item
                }
            }

            val sortedPositions = positions.entries.sortedBy { it.key }
            var ownerTaglock: ItemStack? = null
            var targetTaglock: ItemStack? = null

            for (entry in sortedPositions) {
                val item = entry.value
                if (item.`is`(WitcheryItems.TAGLOCK.get())) {
                    if (ownerTaglock == null) {
                        ownerTaglock = item
                    } else if (targetTaglock == null) {
                        targetTaglock = item
                    }
                }
            }

            if (ownerTaglock != null && targetTaglock != null) {
                poppetItem.set(DataComponents.PROFILE, ownerTaglock.get(DataComponents.PROFILE))
                poppetItem.set(
                    WitcheryDataComponents.ENTITY_NAME_COMPONENT.get(),
                    ownerTaglock.get(WitcheryDataComponents.ENTITY_NAME_COMPONENT.get())
                )
                poppetItem.set(
                    WitcheryDataComponents.ENTITY_ID_COMPONENT.get(),
                    ownerTaglock.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
                )

                poppetItem.set(
                    WitcheryDataComponents.VAMPIRIC_TARGET_PROFILE.get(),
                    targetTaglock.get(DataComponents.PROFILE)
                )
                poppetItem.set(
                    WitcheryDataComponents.VAMPIRIC_TARGET_NAME.get(),
                    targetTaglock.get(WitcheryDataComponents.ENTITY_NAME_COMPONENT.get())
                )
                poppetItem.set(
                    WitcheryDataComponents.VAMPIRIC_TARGET_ID.get(),
                    targetTaglock.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
                )
            }

            return poppetItem
        }

        val taglockItem = taglockItems.first()
        poppetItem.set(DataComponents.PROFILE, taglockItem.get(DataComponents.PROFILE))
        poppetItem.set(
            WitcheryDataComponents.ENTITY_NAME_COMPONENT.get(),
            taglockItem.get(WitcheryDataComponents.ENTITY_NAME_COMPONENT.get())
        )
        poppetItem.set(
            WitcheryDataComponents.ENTITY_ID_COMPONENT.get(),
            taglockItem.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
        )

        return poppetItem
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width >= 2 && height >= 2
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return WitcheryRecipeSerializers.TAGLOCK_RECIPE_SERIALIZER.get()
    }

    companion object {
        const val NAME: String = "data_transfer"
    }
}