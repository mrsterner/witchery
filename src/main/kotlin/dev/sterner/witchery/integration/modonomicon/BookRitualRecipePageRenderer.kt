package dev.sterner.witchery.integration.modonomicon

import com.klikli_dev.modonomicon.book.page.BookRecipePage
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer
import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.WitcheryConfig
import dev.sterner.witchery.content.block.ritual.RitualHelper
import dev.sterner.witchery.content.recipe.ritual.RitualRecipe
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.util.RenderUtils
import dev.sterner.witchery.core.util.RenderUtils.blitWithAlpha
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.level.block.Block
import java.awt.Color
import kotlin.math.max


abstract class BookRitualRecipePageRenderer<T : Recipe<*>>(page: BookRitualRecipePage?) :
    BookRecipePageRenderer<RitualRecipe, BookRecipePage<RitualRecipe>>(page) {


    override fun getRecipeHeight(): Int {
        return 45
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, ticks: Float) {
        val recipeX = X - 9
        val recipeY = Y + 18

        this.drawRecipe(
            guiGraphics,
            page!!.recipe1, recipeX, recipeY, mouseX, mouseY, false
        )

        val style: Style? = this.getClickedComponentStyleAt(mouseX.toDouble(), mouseY.toDouble())
        if (style != null) parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY)
    }

    override fun drawRecipe(
        guiGraphics: GuiGraphics,
        recipeHolder: RecipeHolder<RitualRecipe>,
        recipeX: Int,
        recipeY: Int,
        mouseX: Int,
        mouseY: Int,
        second: Boolean
    ) {
        val pose = guiGraphics.pose()
        pose.pushPose()

        val recipe = recipeHolder.value

        if (RitualHelper.usesCurseCommands(recipe) && !WitcheryConfig.ENABLE_CURSES.get()) {
            val warningText = Component.literal("Curses Disabled")
                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
            val textWidth = Minecraft.getInstance().font.width(warningText)

            guiGraphics.drawStringWithBackdrop(
                Minecraft.getInstance().font,
                warningText,
                recipeX + 46 - (textWidth / 2),
                recipeY - 40,
                textWidth,
                0xFF0000
            )
        }

        val itemsPerRow = 2
        val itemSpacing = 18
        val startX = recipeX + 2 + 2 + 18 + 64 + 9
        val startY = recipeY - 32 + 9

        for ((index, item) in recipe.inputItems.withIndex()) {
            val row = index / itemsPerRow
            val col = index % itemsPerRow

            val x = startX - col * itemSpacing
            val y = startY + row * itemSpacing

            this.parentScreen.renderItemStack(guiGraphics, x, y, mouseX, mouseY, item)
        }

        val squareSize = 92
        renderRitualCircle(
            guiGraphics,
            recipe.pattern,
            recipe.blockMapping,
            recipeX,
            recipeY,
            squareSize
        )

        blitWithAlpha(
            pose,
            Witchery.id("textures/gui/altar_power_modonomicon.png"),
            recipeX + 9, recipeY + 18 * 6 - 9,
            0f, 0f,
            96, 23,
            96, 23,
        )

        val powerTextY = recipeY + 18 * 6 - 2

        val leftText = Component.literal("${recipe.altarPower}")
        val colonText = Component.literal("   ")
        val rightText = Component.literal("${recipe.altarPowerPerSecond}/s")

        val leftWidth = Minecraft.getInstance().font.width(leftText)
        val colonWidth = Minecraft.getInstance().font.width(colonText)

        val colonX = recipeX + 36 + 15

        guiGraphics.drawStringWithBackdrop(
            Minecraft.getInstance().font,
            leftText,
            colonX - leftWidth - 2,
            powerTextY,
            leftWidth,
            0xffffff
        )

        guiGraphics.drawStringWithBackdrop(
            Minecraft.getInstance().font,
            colonText,
            colonX,
            powerTextY,
            colonWidth,
            0xffffff
        )

        val rightWidth = Minecraft.getInstance().font.width(rightText)
        guiGraphics.drawStringWithBackdrop(
            Minecraft.getInstance().font,
            rightText,
            colonX + colonWidth + 2,
            powerTextY,
            rightWidth,
            0xffffff
        )

        val fullMoon = recipe.celestialConditions.contains(RitualRecipe.Celestial.FULL_MOON)
        val newMoon = recipe.celestialConditions.contains(RitualRecipe.Celestial.NEW_MOON)
        val night = recipe.celestialConditions.contains(RitualRecipe.Celestial.NIGHT)
        val day = recipe.celestialConditions.contains(RitualRecipe.Celestial.DAY)
        val waxing = recipe.celestialConditions.contains(RitualRecipe.Celestial.WAXING)
        val waning = recipe.celestialConditions.contains(RitualRecipe.Celestial.WANING)

        val all = recipe.celestialConditions.isEmpty()

        val iconSize = 10
        var x = 20
        var y = 0

        if (recipe.requireCat) {
            val catX = 45
            val catY = 0
            blitWithAlpha(pose, Witchery.id("textures/gui/cat.png"), catX, catY, 0f, 0f, 10, 10, 10, 10)
            if (mouseX in catX..(catX + iconSize) && mouseY in catY..(catY + iconSize)) {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal("Requires Familiar Cat"), mouseX, mouseY)
            }
        }

        if (recipe.weather.contains(RitualRecipe.Weather.RAIN)) {
            val rainX = 45
            val rainY = 11
            blitWithAlpha(pose, Witchery.id("textures/gui/weather/rain.png"), rainX, rainY, 0f, 0f, 10, 10, 10, 10)
            if (mouseX in rainX..(rainX + iconSize) && mouseY in rainY..(rainY + iconSize)) {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal("Requires Rain"), mouseX, mouseY)
            }
        }

        if (recipe.weather.contains(RitualRecipe.Weather.STORM)) {
            val stormX = 45
            val stormY = 22
            blitWithAlpha(pose, Witchery.id("textures/gui/weather/storm.png"), stormX, stormY, 0f, 0f, 10, 10, 10, 10)
            if (mouseX in stormX..(stormX + iconSize) && mouseY in stormY..(stormY + iconSize)) {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal("Requires Thunderstorm"), mouseX, mouseY)
            }
        }

        val showSun = day || all
        blitWithAlpha(
            pose, Witchery.id("textures/gui/celestial/${if (showSun) "sun" else "empty"}.png"),
            x, y, 0f, 0f, iconSize, iconSize, iconSize, iconSize, 1f
        )
        if (mouseX in x..(x + iconSize) && mouseY in y..(y + iconSize)) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal("Day"), mouseX, mouseY)
        }

        y += 11

        val showFullMoon = fullMoon || night || all
        blitWithAlpha(
            pose, Witchery.id("textures/gui/celestial/${if (showFullMoon) "full_moon" else "empty"}.png"),
            x, y, 0f, 0f, iconSize, iconSize, iconSize, iconSize, 1f
        )
        if (mouseX in x..(x + iconSize) && mouseY in y..(y + iconSize)) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal("Full Moon"), mouseX, mouseY)
        }

        y += 11

        val showNewMoon = newMoon || night || all
        blitWithAlpha(
            pose, Witchery.id("textures/gui/celestial/${if (showNewMoon) "new_moon" else "empty"}.png"),
            x, y, 0f, 0f, iconSize, iconSize, iconSize, iconSize, 1f
        )
        if (mouseX in x..(x + iconSize) && mouseY in y..(y + iconSize)) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal("New Moon"), mouseX, mouseY)
        }

        x = 9
        y = 16
        val showWaxing = waxing || night || all
        blitWithAlpha(
            pose, Witchery.id("textures/gui/celestial/${if (showWaxing) "waxing_moon" else "empty"}.png"),
            x, y, 0f, 0f, iconSize, iconSize, iconSize, iconSize, 1f
        )
        if (mouseX in x..(x + iconSize) && mouseY in y..(y + iconSize)) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal("Waxing Moon"), mouseX, mouseY)
        }

        x = 31
        y = 16
        val showWaning = waning || night || all
        blitWithAlpha(
            pose, Witchery.id("textures/gui/celestial/${if (showWaning) "waning_moon" else "empty"}.png"),
            x, y, 0f, 0f, iconSize, iconSize, iconSize, iconSize, 1f
        )
        if (mouseX in x..(x + iconSize) && mouseY in y..(y + iconSize)) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal("Waning Moon"), mouseX, mouseY)
        }

        if (recipe.inputEntities.isNotEmpty()) {
            val minecraft = Minecraft.getInstance()
            val entityX = startX - 60
            val entityY = startY + 50

            for ((_, entityType) in recipe.inputEntities.withIndex()) {
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
                    guiGraphics,
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
            }
        }

        pose.popPose()
    }

    private fun renderRitualCircle(
        guiGraphics: GuiGraphics,
        pattern: List<String>,
        blockMapping: Map<Char, Block>,
        squareX: Int,
        squareY: Int,
        squareSize: Int
    ) {
        if (pattern.isNotEmpty()) {
            val poseStack = guiGraphics.pose()

            val itemSize = 16
            val patternWidth = pattern[0].length * itemSize
            val patternHeight = pattern.size * itemSize

            val centerX = squareX + squareSize / 2
            val centerY = squareY + squareSize / 2

            val padding = 8
            val targetSize = squareSize - padding * 2
            val scale = targetSize.toFloat() / max(patternWidth, patternHeight).toFloat()

            poseStack.pushPose()
            poseStack.translate(centerX.toDouble(), centerY.toDouble(), 0.0)
            poseStack.scale(scale, scale, 1f)
            poseStack.translate(
                (12 - patternWidth / 2).toDouble(),
                (-16 - patternHeight / 2).toDouble(),
                0.0
            )

            for (y in pattern.indices) {
                val row = pattern[y]
                for (x in row.indices) {
                    val char = row[x]
                    val block = blockMapping[char]
                    val itemStack = block?.asItem()?.defaultInstance ?: continue

                    val posX = x * itemSize
                    val posY = y * itemSize

                    renderItem(guiGraphics, itemStack, posX, posY, y + x)
                }
            }

            poseStack.popPose()
        }
    }


    private fun renderItem(
        guiGraphics: GuiGraphics,
        itemStack: ItemStack,
        posX: Int,
        posY: Int,
        index: Int
    ) {
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()
        poseStack.translate(posX.toDouble(), posY.toDouble(), 0.0)

        when {
            itemStack.`is`(WitcheryItems.GOLDEN_CHALK.get()) -> {
                renderChalk(poseStack, Witchery.id("textures/block/golden_chalk.png"))
            }

            itemStack.`is`(WitcheryItems.RITUAL_CHALK.get()) -> {
                renderChalk(poseStack, Witchery.id("textures/block/chalk_${index % 15}.png"))
            }

            itemStack.`is`(WitcheryItems.OTHERWHERE_CHALK.get()) -> {
                renderChalk(
                    poseStack,
                    Witchery.id("textures/block/chalk_${index % 15}.png"),
                    Color(190, 55, 250).rgb
                )
            }

            itemStack.`is`(WitcheryItems.INFERNAL_CHALK.get()) -> {
                renderChalk(
                    poseStack,
                    Witchery.id("textures/block/chalk_${index % 15}.png"),
                    Color(230, 0, 75).rgb
                )
            }

            else -> {
                guiGraphics.renderItem(itemStack, posX, posY, index)
            }
        }

        poseStack.popPose()
    }

    companion object {
        fun renderChalk(
            poseStack: PoseStack,
            texture: ResourceLocation,
            color: Int
        ) {
            blitWithAlpha(poseStack, texture, 1, 1 + 32, 0f, 0f, 16, 16, 16, 16, 0.45f, 0x000000)
            blitWithAlpha(poseStack, texture, 0, 0 + 32, 0f, 0f, 16, 16, 16, 16, 1f, color)
        }

        fun renderChalk(
            poseStack: PoseStack,
            texture: ResourceLocation
        ) {
            blitWithAlpha(poseStack, texture, 1, 1 + 32, 0f, 0f, 16, 16, 16, 16, 0.45f, 0x000000)
            blitWithAlpha(poseStack, texture, 0, 0 + 32, 0f, 0f, 16, 16, 16, 16)
        }
    }
}