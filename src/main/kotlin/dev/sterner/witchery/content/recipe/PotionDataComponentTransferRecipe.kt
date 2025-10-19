package dev.sterner.witchery.content.recipe

import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.registry.WitcheryRecipeSerializers
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.PotionItem
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level
import java.util.*

class PotionDataComponentTransferRecipe : CustomRecipe(CraftingBookCategory.MISC) {

    override fun matches(input: CraftingInput, level: Level): Boolean {
        for (item in input.items()) {
            if (item.`is`(WitcheryItems.BITING_BELT.get())) {
                return true
            }
        }
        return false
    }

    override fun assemble(input: CraftingInput, registries: HolderLookup.Provider): ItemStack {
        var potionItem: ItemStack? = null
        var bitingBelt: ItemStack? = null

        for (item in input.items()) {
            if (item.item is PotionItem) {
                potionItem = item
            } else if (item.`is`(WitcheryItems.BITING_BELT.get())) {
                bitingBelt = item
            }
        }

        if (potionItem != null && bitingBelt != null) {
            val positiveEffects = mutableListOf<MobEffectInstance>()
            val negativeEffects = mutableListOf<MobEffectInstance>()

            val potionContent = potionItem.get(DataComponents.POTION_CONTENTS)

            potionContent?.allEffects?.forEach { effect ->
                if (effect.effect.value().isBeneficial) {
                    positiveEffects.add(effect)
                } else {
                    negativeEffects.add(effect)
                }
            }

            val positivePotionContents = if (positiveEffects.isNotEmpty()) {
                PotionContents.EMPTY.withEffectAdded(positiveEffects[0])
            } else {
                null
            }

            val negativePotionContents = if (negativeEffects.isNotEmpty()) {
                PotionContents.EMPTY.withEffectAdded(negativeEffects[0])
            } else {
                null
            }

            val resultBelt = bitingBelt.copy()
            val dualPotionContents = WitcheryDataComponents.DualPotionContents(
                positive = Optional.ofNullable(positivePotionContents),
                negative = Optional.ofNullable(negativePotionContents)
            )
            resultBelt.set(WitcheryDataComponents.DUAL_POTION_CONTENT.get(), dualPotionContents)

            return resultBelt
        }

        return ItemStack.EMPTY
    }


    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width >= 2 && height >= 2
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return WitcheryRecipeSerializers.POTION_RECIPE_SERIALIZER.get()
    }

    companion object {
        const val NAME: String = "potion_data_transfer"
    }
}