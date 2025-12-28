package dev.sterner.witchery.integration.jei

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.WitcheryConfig
import dev.sterner.witchery.content.block.ritual.RitualHelper
import dev.sterner.witchery.content.recipe.ritual.RitualRecipe
import dev.sterner.witchery.integration.jei.wrapper.RitualJeiRecipe
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.util.RenderUtils
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.builder.ITooltipBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IJeiHelpers
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.block.Block
import java.awt.Color

class RitualJeiRecipeCategory(var guiHelper: IJeiHelpers) : IRecipeCategory<RitualJeiRecipe> {

    override fun getRecipeType(): RecipeType<RitualJeiRecipe> {
        return WitcheryJeiPlugin.RITUAL
    }

    override fun getTitle(): Component {
        return Component.translatable("witchery.ritual.category")
    }

    override fun getBackground(): IDrawable {
        return guiHelper.guiHelper.createBlankDrawable(18 * 9, 18 * 8)
    }

    override fun getIcon(): IDrawable? {
        return guiHelper.guiHelper.createDrawableIngredient(
            VanillaTypes.ITEM_STACK,
            WitcheryItems.GOLDEN_CHALK.get().defaultInstance
        )
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: RitualJeiRecipe, focuses: IFocusGroup) {
        val inputItems = recipe.recipe.inputItems
        val outputItems = recipe.recipe.outputItems

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

    override fun getTooltip(
        tooltip: ITooltipBuilder,
        recipe: RitualJeiRecipe,
        recipeSlotsView: IRecipeSlotsView,
        mouseX: Double,
        mouseY: Double
    ) {
        super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY)
        val font = Minecraft.getInstance().font
        val text = Component.translatable("${recipe.id}.tooltip")
        val textWidth = font.width(text)
        val textX = (width / 2) - (textWidth / 2)

        if (mouseX in textX.toDouble()..(textX + textWidth).toDouble() &&
            mouseY in 2.toDouble()..(2 + font.lineHeight).toDouble()
        ) {
            tooltip.add(text)
        }
    }

    override fun draw(
        recipe: RitualJeiRecipe,
        recipeSlotsView: IRecipeSlotsView,
        graphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        graphics.drawCenteredString(
            Minecraft.getInstance().font,
            Component.translatable("${recipe.id}"), (width / 2), 2, -1
        )

        if (RitualHelper.usesCurseCommands(recipe.recipe) && !WitcheryConfig.ENABLE_CURSES.get()) {
            graphics.drawCenteredString(
                Minecraft.getInstance().font,
                Component.literal("Curses Disabled").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                (width / 2),
                14,
                0xFF0000
            )
        }

        val squareX = 18 * 4 - 12
        val squareY = background.height - (18 * 8) + 9
        val squareSize = 92

        val pattern = recipe.recipe.pattern
        val blockMapping = recipe.recipe.blockMapping

        drawCirclePattern(graphics, pattern, blockMapping, squareX, squareY, squareSize)

        val celestial = recipe.recipe.celestialConditions
        val all = celestial.isEmpty()

        drawCelestial(graphics, "sun", celestial.contains(RitualRecipe.Celestial.DAY) || all, 20, 64)
        drawCelestial(
            graphics,
            "full_moon",
            celestial.contains(RitualRecipe.Celestial.FULL_MOON) || celestial.contains(RitualRecipe.Celestial.NIGHT) || all,
            20,
            75
        )
        drawCelestial(
            graphics,
            "new_moon",
            celestial.contains(RitualRecipe.Celestial.NEW_MOON) || celestial.contains(RitualRecipe.Celestial.NIGHT) || all,
            20,
            86
        )
        drawCelestial(
            graphics,
            "waxing_moon",
            celestial.contains(RitualRecipe.Celestial.WAXING) || celestial.contains(RitualRecipe.Celestial.NIGHT) || all,
            9,
            81
        )
        drawCelestial(
            graphics,
            "waning_moon",
            celestial.contains(RitualRecipe.Celestial.WANING) || celestial.contains(RitualRecipe.Celestial.NIGHT) || all,
            31,
            81
        )

        if (recipe.recipe.requireCat) {
            graphics.blit(
                Witchery.id("textures/gui/cat.png"),
                31 - 11,
                64 - 20 + 10,
                0f,
                0f,
                10,
                10,
                10,
                10
            )
        }
        if (recipe.recipe.weather.contains(RitualRecipe.Weather.RAIN)) {
            graphics.blit(
                Witchery.id("textures/gui/weather/rain.png"),
                20 + 11, 64 + 3,
                0f,
                0f,
                10,
                10,
                10,
                10
            )
        }
        if (recipe.recipe.weather.contains(RitualRecipe.Weather.STORM)) {
            graphics.blit(
                Witchery.id("textures/gui/weather/storm.png"),
                20 - 11, 64 + 3,
                0f,
                0f,
                10,
                10,
                10,
                10
            )
        }

        var yOffset = background.height - 40

        if (recipe.recipe.altarPower > 0) {
            graphics.drawCenteredString(
                Minecraft.getInstance().font,
                "Initial Cost",
                24,
                yOffset,
                0xffffff
            )
            yOffset += 10

            graphics.drawCenteredString(
                Minecraft.getInstance().font,
                "${recipe.recipe.altarPower}",
                24,
                yOffset,
                0xFFD700
            )
            yOffset += 12
        }

        if (recipe.recipe.altarPowerPerSecond > 0) {
            graphics.drawCenteredString(
                Minecraft.getInstance().font,
                "Per Second",
                24,
                yOffset,
                0xffffff
            )
            yOffset += 10

            graphics.drawCenteredString(
                Minecraft.getInstance().font,
                "${recipe.recipe.altarPowerPerSecond}",
                24,
                yOffset,
                0x87CEEB
            )
            yOffset += 12
        }

        if (recipe.recipe.altarPower == 0 && recipe.recipe.altarPowerPerSecond == 0) {
            graphics.drawCenteredString(
                Minecraft.getInstance().font,
                "Power",
                24,
                yOffset,
                0xffffff
            )
            yOffset += 10

            graphics.drawCenteredString(
                Minecraft.getInstance().font,
                "0",
                24,
                yOffset,
                0xffffff
            )
            yOffset += 12
        }

        val size = recipe.recipe.covenCount
        if (size > 0) {
            graphics.drawCenteredString(
                Minecraft.getInstance().font,
                "Coven Size: $size",
                24,
                yOffset,
                0xffffff
            )
        }

        if (recipe.recipe.inputEntities.isNotEmpty()) {
            val minecraft = Minecraft.getInstance()
            val entityX = background.width / 2
            val entityY = background.height / 2

            for ((_, entityType) in recipe.recipe.inputEntities.withIndex()) {
                val entity = entityType.create(minecraft.level) as? LivingEntity ?: return

                val entityHeight = entity.boundingBox.ysize * 4
                val entityWidth = entity.boundingBox.xsize * 4

                val baseScale = when {
                    entityHeight > 2.0 -> 15
                    entityHeight > 1.0 -> 25
                    else -> 30
                }

                val widthAdjustment = if (entityWidth > 1.0) 0.8f else 1.0f
                val scale = baseScale * widthAdjustment
                val yOffset = if (entityHeight <= 1.0) 0f else -8f

                RenderUtils.renderEntityInInventoryFollowsMouse(
                    graphics,
                    entityX - 20,
                    entityY + 20,
                    entityX + 20,
                    entityY - 20,
                    scale.toInt(),
                    yOffset + 9,
                    mouseX.toFloat(),
                    mouseY.toFloat(),
                    entity
                )

                val entityName = Component.translatable(entityType.descriptionId)
                val textWidth = minecraft.font.width(entityName)
                graphics.drawString(
                    minecraft.font,
                    entityName,
                    entityX - textWidth / 2,
                    entityY + 30,
                    0xFFFFFF
                )
            }
        }
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

        val patternSize = pattern.size
        val itemSize = 16

        val scale = 7f / patternSize

        val poseStack = graphics.pose()
        poseStack.pushPose()

        val centerX = squareX + squareSize / 2
        val centerY = squareY + squareSize / 2
        val halfPatternPx = (patternSize * itemSize) / 2f

        val yOffsetCorrection = (patternSize - 7) * 2.5

        poseStack.translate(
            (centerX - halfPatternPx * scale).toDouble(),
            (centerY - halfPatternPx * scale + yOffsetCorrection),
            0.0
        )
        poseStack.scale(scale, scale, scale)

        for (y in pattern.indices) {
            val row = pattern[y]
            for (x in row.indices) {
                val char = row[x]
                val block = blockMapping[char] ?: continue
                val stack = block.asItem().defaultInstance

                val px = x * itemSize
                val py = y * itemSize

                when {
                    stack.`is`(WitcheryItems.GOLDEN_CHALK.get()) -> {
                        drawChalk(poseStack, px, py, "textures/block/golden_chalk.png", null)
                    }

                    stack.`is`(WitcheryItems.RITUAL_CHALK.get()) -> {
                        drawChalk(poseStack, px, py, "textures/block/chalk_${(y + x) % 15}.png", null)
                    }

                    stack.`is`(WitcheryItems.OTHERWHERE_CHALK.get()) -> {
                        drawChalk(
                            poseStack,
                            px,
                            py,
                            "textures/block/chalk_${(y + x) % 15}.png",
                            Color(190, 55, 250).rgb
                        )
                    }

                    stack.`is`(WitcheryItems.BINDING_CHALK.get()) -> {
                        drawChalk(
                            poseStack,
                            px,
                            py,
                            "textures/block/chalk_${(y + x) % 15}.png",
                            Color( 20, 55, 100).rgb
                        )
                    }

                    stack.`is`(WitcheryItems.INFERNAL_CHALK.get()) -> {
                        drawChalk(poseStack, px, py, "textures/block/chalk_${(y + x) % 15}.png", Color(230, 0, 75).rgb)
                    }

                    else -> {
                        graphics.renderItem(stack, px, py)
                    }
                }
            }
        }

        poseStack.popPose()
    }

    private fun drawChalk(poseStack: PoseStack, x: Int, y: Int, texturePath: String, color: Int?) {
        poseStack.pushPose()
        poseStack.translate(x.toDouble(), y.toDouble(), 0.0)

        if (color != null) {
            RenderUtils.renderChalk(poseStack, 0, 0, Witchery.id(texturePath), color)
        } else {
            RenderUtils.renderChalk(poseStack, 0, 0, Witchery.id(texturePath))
        }

        poseStack.popPose()
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