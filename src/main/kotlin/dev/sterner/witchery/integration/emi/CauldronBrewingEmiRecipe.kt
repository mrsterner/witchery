package dev.sterner.witchery.integration.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.core.util.RenderUtils.blitWithAlpha
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient

class CauldronBrewingEmiRecipe(val recipeId: ResourceLocation, val recipe: CauldronBrewingRecipe) : EmiRecipe {

    override fun getCategory(): EmiRecipeCategory {
        return WitcheryEmiPlugin.CAULDRON_BREWING_CATEGORY
    }

    override fun getId(): ResourceLocation {
        return recipeId
    }

    override fun getInputs(): MutableList<EmiIngredient> {
        val mutableList = mutableListOf<EmiIngredient>()
        for (ingredients in recipe.inputItems.map { it.itemStack }) {
            mutableList.add(EmiIngredient.of(Ingredient.of(ingredients)))
        }
        return mutableList
    }

    override fun getOutputs(): MutableList<EmiStack> {
        val mutableList = mutableListOf<EmiStack>()
        mutableList.add(EmiStack.of(recipe.outputItem))
        return mutableList
    }

    override fun getDisplayWidth(): Int {
        return 18 * 8
    }

    override fun getDisplayHeight(): Int {
        return 18 * 7 + if (recipe.inputItems.size > 5) (recipe.inputItems.size - 5) * 18 else 0
    }

    override fun addWidgets(widgets: WidgetHolder) {
        for ((index, ingredient) in recipe.inputItems.withIndex()) {
            widgets.addDrawable(2, 20 * index, 48, 18) { ctx, _, _, _ ->
                ctx.blit(Witchery.id("textures/gui/order_widget.png"), 0, 0, 0f, 0f, 48, 18, 48, 18)
                blitWithAlpha(
                    ctx.pose(),
                    Witchery.id("textures/gui/index_${ingredient.order + 1}.png"),
                    2,
                    2,
                    0f,
                    0f,
                    13,
                    13,
                    13,
                    13
                )
            }

            widgets.add(
                WitcherySlotWidget(EmiStack.of(ingredient.itemStack), 2 + 2 + 18, 20 * index)
                    .drawBack(false).recipeContext(this)
            )
        }

        widgets.addDrawable(48 + 18 + 9, 20 * 1 - 18, 18, 18) { ctx, _, _, _ ->
            ctx.blit(Witchery.id("textures/gui/cauldron.png"), 0, 8, 0f, 0f, 35, 56, 35, 56)
        }

        widgets.add(
            WitcherySlotWidget(
                EmiStack.of(Items.GLASS_BOTTLE.defaultInstance),
                48 + 18 + 9 - 12,
                20 * 1 + 6 - 18
            )
                .drawBack(false)
        )

        widgets.add(
            WitcherySlotWidget(EmiStack.of(recipe.outputItem), 48 + 18 + 9 + 18 + 9 + 4, 20 * 1 + 6 - 18)
                .drawBack(false).recipeContext(this)
        )
        var bl = false
        if (recipe.dimensionKey.isNotEmpty()) {
            for ((index, key) in recipe.dimensionKey.withIndex()) {
                if (key.isNotEmpty()) {
                    bl = true
                }
                widgets.addText(
                    Component.translatable(key),
                    displayWidth / 4 + 18,
                    36 + 36 + 14 * index,
                    0xffffff,
                    true
                )
            }
        }
        if (!bl) {
            widgets.addText(
                Component.translatable("witchery:all_worlds"),
                displayWidth / 4 + 18,
                36 + 36,
                0xffffff,
                true
            )
        }
    }
}