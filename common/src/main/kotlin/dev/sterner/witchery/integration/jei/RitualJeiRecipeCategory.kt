package dev.sterner.witchery.integration.jei

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.RenderUtils
import dev.sterner.witchery.recipe.ritual.RitualRecipe
import dev.sterner.witchery.registry.WitcheryItems
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.Block
import java.awt.Color

class RitualJeiRecipeCategory(var guiHelper: IGuiHelper) : IRecipeCategory<RitualRecipe> {

    override fun getRecipeType(): RecipeType<RitualRecipe> {
        return WitcheryJeiPlugin.RITUAL
    }

    override fun getTitle(): Component {
        return Component.translatable("witchery.ritual.category")
    }

    override fun getBackground(): IDrawable {
        return guiHelper.createBlankDrawable(18 * 9, 18 * 8)
    }

    override fun getIcon(): IDrawable? {
      return guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, WitcheryItems.GOLDEN_CHALK.get().defaultInstance)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: RitualRecipe, focuses: IFocusGroup) {
        val inputItems = recipe.inputItems
        val outputItems = recipe.outputItems

        val itemsPerRow = 6
        inputItems.forEachIndexed { index, stack ->
            val row = index / itemsPerRow
            val col = index % itemsPerRow
            val x = 9 + col * 18
            val y = 18 + row * 18 - 4

            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                .addItemStack(stack)
        }

        val outputX = background.width - 18 - 9
        outputItems.forEachIndexed { index, stack ->
            val y = background.height - (itemsPerRow * 18) - 18 - 4 + index * 18
            builder.addSlot(RecipeIngredientRole.OUTPUT, outputX, y)
                .addItemStack(stack)
        }
    }

    override fun draw(
        recipe: RitualRecipe,
        recipeSlotsView: IRecipeSlotsView,
        graphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val squareX = 18 * 3 - 9
        val squareY = background.height - (18 * 6)
        val squareSize = 92

        val pattern = recipe.pattern
        val blockMapping = recipe.blockMapping

        drawCirclePattern(graphics, pattern, blockMapping, squareX, squareY, squareSize)

        // Draw Celestial Icons
        val celestial = recipe.celestialConditions
        val all = celestial.isEmpty()

        drawCelestial(graphics, "sun", celestial.contains(RitualRecipe.Celestial.DAY) || all, 20, 64)
        drawCelestial(graphics, "full_moon", celestial.contains(RitualRecipe.Celestial.FULL_MOON) || celestial.contains(RitualRecipe.Celestial.NIGHT) || all, 20, 75)
        drawCelestial(graphics, "new_moon", celestial.contains(RitualRecipe.Celestial.NEW_MOON) || celestial.contains(RitualRecipe.Celestial.NIGHT) || all, 20, 86)
        drawCelestial(graphics, "waxing_moon", celestial.contains(RitualRecipe.Celestial.WAXING) || celestial.contains(RitualRecipe.Celestial.NIGHT) || all, 9, 81)
        drawCelestial(graphics, "waning_moon", celestial.contains(RitualRecipe.Celestial.WANING) || celestial.contains(RitualRecipe.Celestial.NIGHT) || all, 31, 81)

        val append = if (recipe.isInfinite) "/s" else ""
        graphics.drawString(Minecraft.getInstance().font, "Power", 7, background.height / 2 + 6, 0xffffff, true)
        graphics.drawString(
            Minecraft.getInstance().font,
            "${recipe.altarPower}$append",
            7,
            background.height / 2 + 18,
            0xffffff,
            true
        )
    }

    private fun drawChalk(poseStack: PoseStack, x: Int, y: Int, texturePath: String, patternSize: Int, color: Int?) {
        poseStack.pushPose()

        val scaleFactor = 7f / patternSize

        val pixelSize = patternSize * 2f

        val offsetX = x - (pixelSize / 2f) * scaleFactor
        val offsetY = y - (pixelSize / 2f) * scaleFactor

        poseStack.translate(offsetX.toDouble(), offsetY.toDouble(), 0.0)
        poseStack.scale(scaleFactor, scaleFactor, scaleFactor)

        if (color != null) {
            RenderUtils.renderChalk(poseStack, 0, 0, Witchery.id(texturePath), color)
        } else {
            RenderUtils.renderChalk(poseStack, 0, 0, Witchery.id(texturePath))
        }

        poseStack.popPose()
    }


    private fun drawCirclePattern(
        graphics: GuiGraphics,
        pattern: List<String>,
        blockMapping: Map<Char, Block>,
        squareX: Int,
        squareY: Int,
        squareSize: Int
    ) {
        if (pattern.isEmpty()) return

        val basePatternSize = 15
        val baseScale = 1 / 3.0
        val patternSize = pattern.size
        val scale = baseScale * (basePatternSize / patternSize.toDouble())
        val itemSize = (16 * scale).toInt()

        val totalWidth = pattern[0].length * itemSize
        val totalHeight = pattern.size * itemSize
        val centerX = squareX + squareSize / 2
        val centerY = squareY + squareSize / 2

        val startX = centerX - totalWidth / 2
        val startY = centerY - totalHeight / 2 - 16

        for (y in pattern.indices) {
            val row = pattern[y]
            for (x in row.indices) {
                val char = row[x]
                val block = blockMapping[char] ?: continue
                val stack = block.asItem().defaultInstance

                val px = startX + x * itemSize
                val py = startY + y * itemSize


                when {
                    stack.`is`(WitcheryItems.GOLDEN_CHALK.get()) -> {
                        drawChalk(graphics.pose(), px, py, "textures/block/golden_chalk.png", patternSize, null)
                    }
                    stack.`is`(WitcheryItems.RITUAL_CHALK.get()) -> {
                        drawChalk(graphics.pose(), px, py, "textures/block/chalk_${(y + x) % 15}.png", patternSize, null)
                    }
                    stack.`is`(WitcheryItems.OTHERWHERE_CHALK.get()) -> {
                        drawChalk(graphics.pose(), px, py, "textures/block/chalk_${(y + x) % 15}.png", patternSize, Color(190, 55, 250).rgb)
                    }
                    stack.`is`(WitcheryItems.INFERNAL_CHALK.get()) -> {
                        drawChalk(graphics.pose(), px, py, "textures/block/chalk_${(y + x) % 15}.png", patternSize, Color(230, 0, 75).rgb)
                    }
                    else -> {
                        graphics.renderItem(stack, px, py)
                    }
                }
            }
        }
    }

    private fun drawCelestial(graphics: GuiGraphics, type: String, enabled: Boolean, x: Int, y: Int) {
        val tex = if (enabled) type else "empty"
        graphics.blit(
            Witchery.id("textures/gui/celestial/$tex.png"),
            x,
            y,
            0f,
            0f,
            10,
            10,
            10,
            10
        )
    }

}