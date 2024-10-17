package dev.sterner.witchery.integration.emi

import com.mojang.blaze3d.vertex.*
import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.TextWidget
import dev.emi.emi.api.widget.WidgetHolder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.RenderUtils
import dev.sterner.witchery.recipe.ritual.RitualRecipe
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.block.Block
import java.awt.Color

class RitualEmiRecipe(val recipeId: ResourceLocation, val recipe: RitualRecipe) : EmiRecipe {
    override fun getCategory(): EmiRecipeCategory {
        return WitcheryEmiPlugin.RITUAL_CATEGORY
    }

    override fun getId(): ResourceLocation {
        return recipeId
    }

    override fun getInputs(): MutableList<EmiIngredient> {
        val mutableList = mutableListOf<EmiIngredient>()
        for (ingredients in recipe.inputItems) {
            mutableList.add(EmiIngredient.of(Ingredient.of(ingredients)))
        }
        return mutableList
    }

    override fun getOutputs(): MutableList<EmiStack> {
        val mutableList = mutableListOf<EmiStack>()
        for (itemStacks in recipe.outputItems) {
            mutableList.add(EmiStack.of(itemStacks))
        }
        return mutableList
    }

    override fun getDisplayWidth(): Int {
        return 18 * 8
    }

    override fun getDisplayHeight(): Int {
        return 18 * 8
    }


    override fun addWidgets(widgets: WidgetHolder) {
        val blockMapping: Map<Char, Block> = recipe.blockMapping
        val pattern: List<String> = recipe.pattern
        widgets.addText(Component.translatable(id.toString()), displayWidth / 2, 8, 0xffffff, true).horizontalAlign(TextWidget.Alignment.CENTER)

        val itemsPerRow = 6
        val itemSize = 18
        var rowIndex = 0
        var colIndex = 0

        for (item in recipe.inputItems) {
            val posX = 9 + (colIndex * itemSize)
            val posY = 18 + (rowIndex * itemSize)

            widgets.add(WitcherySlotWidget(EmiStack.of(item), posX, posY))

            colIndex++
            if (colIndex >= itemsPerRow) {
                colIndex = 0
                rowIndex++
            }
        }

        val scale = 1 / 3.0
        val ritualCircleOffsetY = (rowIndex * itemSize) * scale + 36
        renderRitualCircle(widgets, pattern, blockMapping, ritualCircleOffsetY)

        val colXOffset = displayWidth - itemSize - 9
        rowIndex = 0

        for (item in recipe.outputItems) {
            val posY = (rowIndex * itemSize) + displayHeight - (itemsPerRow * itemSize) - 18

            widgets.add(WitcherySlotWidget(EmiStack.of(item), colXOffset, posY).recipeContext(this))

            rowIndex++
        }

        widgets.addText(Component.literal("Altar Power: ${recipe.altarPower}"), displayWidth / 4, displayHeight - 18, 0xffffff, true)
    }

    private fun renderRitualCircle(
        widgets: WidgetHolder,
        pattern: List<String>,
        blockMapping: Map<Char, Block>,
        offsetY: Double
    ) {
        if (pattern.isNotEmpty()) {
            val scale = 1 / 3.0
            val itemSize = (16 * scale).toInt()
            val totalWidth = (pattern[0].length * itemSize) // Calculate total width based on first row length
            val startingX = (widgets.width - totalWidth) / 2 // Calculate the starting X position to center

            for (y in pattern.indices) {
                val row = pattern[y]
                for (x in row.indices) {
                    val char = row[x]
                    val block = blockMapping[char]
                    val itemStack = block?.asItem()?.defaultInstance ?: continue

                    // Calculate position based on centered X position and offsetY
                    val posX = startingX + (x * itemSize)
                    val posY = (y * itemSize) + offsetY // Apply the offset to the Y position

                    renderItem(widgets, itemStack, posX, posY.toInt(), itemSize, y + x)
                }
            }
        }
    }


    private fun addChalkCircleWidget(
        widgets: WidgetHolder,
        posX: Int,
        posY: Int,
        size: Int,
        texturePath: String,
        color: Int? = null
    ) {
        widgets.addDrawable(posX, posY, size, size) { graphics, _, _, _ ->
            val poseStack = graphics.pose()
            poseStack.pushPose()

            // Scaling for chalk circle
            poseStack.scale((1 / 3.0).toFloat(), (1 / 3.0).toFloat(), (1 / 3.0).toFloat())

            // Render the chalk circle, optionally with color
            if (color != null) {
                renderChalk(poseStack, Witchery.id(texturePath), color)
            } else {
                renderChalk(poseStack, Witchery.id(texturePath))
            }

            poseStack.popPose()
        }
    }

    private fun addItemCircleWidget(
        widgets: WidgetHolder,
        itemStack: ItemStack,
        posX: Int,
        posY: Int
    ) {
        widgets.add(WitcherySlotWidget(EmiStack.of(itemStack), posX - 5, posY + 5, 0.3).drawBack(false))
    }

    private fun renderItem(
        widgets: WidgetHolder,
        itemStack: ItemStack,
        posX: Int,
        posY: Int,
        size: Int,
        index: Int
    ) {
        when {
            itemStack.`is`(WitcheryItems.GOLDEN_CHALK.get()) -> {
                addChalkCircleWidget(
                    widgets, posX, posY, size,
                    "textures/block/golden_chalk.png"
                )
            }
            itemStack.`is`(WitcheryItems.RITUAL_CHALK.get()) -> {
                addChalkCircleWidget(
                    widgets, posX, posY, size,
                    "textures/block/chalk_${index % 15}.png"
                )
            }
            itemStack.`is`(WitcheryItems.OTHERWHERE_CHALK.get()) -> {
                addChalkCircleWidget(
                    widgets, posX, posY, size,
                    "textures/block/chalk_${index % 15}.png",
                    Color(190, 55, 250).rgb
                )
            }
            itemStack.`is`(WitcheryItems.INFERNAL_CHALK.get()) -> {
                addChalkCircleWidget(
                    widgets, posX, posY, size,
                    "textures/block/chalk_${index % 15}.png",
                    Color(230, 0, 75).rgb
                )
            }
            else -> {
                addItemCircleWidget(widgets, itemStack, posX, posY)
            }
        }
    }

    private fun renderChalk(
        poseStack: PoseStack,
        texture: ResourceLocation,
        color: Int
    ) {
        RenderUtils.blitWithAlpha(poseStack, texture, 1, 1 + 32, 0f, 0f, 16, 16, 16, 16, 0.45f, 0x000000)
        RenderUtils.blitWithAlpha(poseStack, texture, 0, 0 + 32, 0f, 0f, 16, 16, 16, 16, 1f, color)
    }
    private fun renderChalk(
        poseStack: PoseStack,
        texture: ResourceLocation
    ) {
        RenderUtils.blitWithAlpha(poseStack, texture, 1, 1 + 32, 0f, 0f, 16, 16, 16, 16, 0.45f, 0x000000)
        RenderUtils.blitWithAlpha(poseStack, texture, 0, 0 + 32, 0f, 0f, 16, 16, 16, 16)
    }
}