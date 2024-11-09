package dev.sterner.witchery.integration.emi

import com.mojang.blaze3d.vertex.PoseStack
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
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
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
        widgets.addText(Component.translatable(id.toString()), displayWidth / 2, 2, 0xffffff, true)
            .horizontalAlign(TextWidget.Alignment.CENTER)
        widgets.addTooltipText(listOf(Component.translatable("$id.tooltip")), 9, 2, 18 * 7, 18)

        widgets.addTexture(Witchery.id("textures/gui/black_square.png"),
            18 * 3 - 9,
            displayHeight - (18 * 6),
            92,
            92,
            0,
            0,
            72,
            72,
            72,
            72
            )

        val fullMoon = recipe.celestialConditions.contains(RitualRecipe.Celestial.FULL_MOON)
        val newMoon = recipe.celestialConditions.contains(RitualRecipe.Celestial.NEW_MOON)
        val night = recipe.celestialConditions.contains(RitualRecipe.Celestial.NIGHT)
        val day = recipe.celestialConditions.contains(RitualRecipe.Celestial.DAY)
        val waxing = recipe.celestialConditions.contains(RitualRecipe.Celestial.WAXING)
        val waning = recipe.celestialConditions.contains(RitualRecipe.Celestial.WANING)

        val all = recipe.celestialConditions.isEmpty()

        widgets.addTexture(Witchery.id("textures/gui/celestial/${if(day || all) "sun" else "empty"}.png"),
            20, 4 + 20 + 20,10,10, 0,0, 10, 10, 10, 10)
            .tooltip(listOf(ClientTooltipComponent.create(FormattedCharSequence.forward("Day", Style.EMPTY))))
        widgets.addTexture(Witchery.id("textures/gui/celestial/${if(fullMoon || night || all) "full_moon" else "empty"}.png"),
            20, 4 + 11 + 20 + 20,10,10, 0,0, 10, 10, 10, 10)
            .tooltip(listOf(ClientTooltipComponent.create(FormattedCharSequence.forward("Full Moon", Style.EMPTY))))
        widgets.addTexture(Witchery.id("textures/gui/celestial/${if(newMoon || night || all) "new_moon" else "empty"}.png"),
            20, 4 + 11 + 11 + 20 + 20,10,10, 0,0, 10, 10, 10, 10)
            .tooltip(listOf(ClientTooltipComponent.create(FormattedCharSequence.forward("New Moon", Style.EMPTY))))
        widgets.addTexture(Witchery.id("textures/gui/celestial/${if(waxing || night || all) "waxing_moon" else "empty"}.png"),
            20 - 11, 4 + 6 + 20 + 20 + 10,10,10, 0,0, 10, 10, 10, 10)
            .tooltip(listOf(ClientTooltipComponent.create(FormattedCharSequence.forward("Waxing Moon", Style.EMPTY))))
        widgets.addTexture(Witchery.id("textures/gui/celestial/${if(waning || night || all) "waning_moon" else "empty"}.png"),
            20 + 11, 4 + 6 + 20 + 20 + 10,10,10, 0,0, 10, 10, 10, 10)
            .tooltip(listOf(ClientTooltipComponent.create(FormattedCharSequence.forward("Waning Moon", Style.EMPTY))))

        val itemsPerRow = 6
        val itemSize = 18
        var rowIndex = 0
        var colIndex = 0

        for (item in recipe.inputItems) {
            val posX = 9 + (colIndex * itemSize)
            val posY = 18 + (rowIndex * itemSize) - 4

            widgets.add(WitcherySlotWidget(EmiStack.of(item), posX, posY))

            colIndex++
            if (colIndex >= itemsPerRow) {
                colIndex = 0
                rowIndex++
            }
        }

        val squareX = 18 * 3 - 9
        val squareY = displayHeight - (18 * 6)
        val squareSize = 92

        renderRitualCircle(widgets, pattern, blockMapping, squareX, squareY, squareSize)

        val colXOffset = displayWidth - itemSize - 9
        rowIndex = 0

        for (item in recipe.outputItems) {
            val posY = (rowIndex * itemSize) + displayHeight - (itemsPerRow * itemSize) - 18

            widgets.add(WitcherySlotWidget(EmiStack.of(item), colXOffset, posY).recipeContext(this))

            rowIndex++
        }
        val append = if (recipe.isInfinite) "/s" else ""
        widgets.addText(
            Component.literal("Altar Power: ${recipe.altarPower}$append"),
            displayWidth / 4 - 9,
            displayHeight - 18 - 9 + 9,
            0xffffff,
            true
        )
    }

    private fun renderRitualCircle(
        widgets: WidgetHolder,
        pattern: List<String>,
        blockMapping: Map<Char, Block>,
        squareX: Int,
        squareY: Int,
        squareSize: Int
    ) {
        if (pattern.isNotEmpty()) {

            val basePatternSize = 15
            val baseScale = 1 / 3.0
            val patternSize = pattern.size
            val scale = baseScale * (basePatternSize / patternSize.toDouble())
            val itemSize = (16 * scale).toInt()

            val totalWidth = pattern[0].length * itemSize
            val totalHeight = pattern.size * itemSize

            val squareCenterX = squareX + squareSize / 2
            val squareCenterY = squareY + squareSize / 2

            val startingX = squareCenterX - (totalWidth / 2)
            val startingY = squareCenterY - (totalHeight / 2) - 16

            for (y in pattern.indices) {
                val row = pattern[y]
                for (x in row.indices) {
                    val char = row[x]
                    val block = blockMapping[char]
                    val itemStack = block?.asItem()?.defaultInstance ?: continue

                    val posX = startingX + (x * itemSize)
                    val posY = startingY + (y * itemSize)

                    renderItem(widgets, itemStack, posX, posY, itemSize, y + x, patternSize)
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
        patternSize: Int,
        color: Int? = null,
    ) {
        widgets.addDrawable(posX, posY, size, size) { graphics, _, _, _ ->
            val poseStack = graphics.pose()
            poseStack.pushPose()

            val basePatternSize = 4
            val scaleFactor = basePatternSize.toFloat() / patternSize

            poseStack.scale(scaleFactor, scaleFactor, scaleFactor)

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
        index: Int,
        patternSize: Int
    ) {
        when {
            itemStack.`is`(WitcheryItems.GOLDEN_CHALK.get()) -> {
                addChalkCircleWidget(
                    widgets, posX, posY, size,
                    "textures/block/golden_chalk.png", patternSize
                )
            }

            itemStack.`is`(WitcheryItems.RITUAL_CHALK.get()) -> {
                addChalkCircleWidget(
                    widgets, posX, posY, size,
                    "textures/block/chalk_${index % 15}.png", patternSize
                )
            }

            itemStack.`is`(WitcheryItems.OTHERWHERE_CHALK.get()) -> {
                addChalkCircleWidget(
                    widgets, posX, posY, size,
                    "textures/block/chalk_${index % 15}.png", patternSize,
                    Color(190, 55, 250).rgb
                )
            }

            itemStack.`is`(WitcheryItems.INFERNAL_CHALK.get()) -> {
                addChalkCircleWidget(
                    widgets, posX, posY, size,
                    "textures/block/chalk_${index % 15}.png", patternSize,
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