package dev.sterner.witchery.integration.modonomicon

import com.klikli_dev.modonomicon.book.page.BookRecipePage
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer
import com.mojang.blaze3d.vertex.PoseStack
import dev.emi.emi.api.widget.WidgetHolder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.RenderUtils
import dev.sterner.witchery.api.RenderUtils.blitWithAlpha
import dev.sterner.witchery.recipe.ritual.RitualRecipe
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.level.block.Block
import java.awt.Color
import kotlin.math.max


abstract class BookRitualRecipePageRenderer<T : Recipe<*>?>(page: BookRitualRecipePage?) :
    BookRecipePageRenderer<RitualRecipe?, BookRecipePage<RitualRecipe?>?>(page) {


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
        recipeHolder: RecipeHolder<RitualRecipe?>,
        recipeX: Int,
        recipeY: Int,
        mouseX: Int,
        mouseY: Int,
        second: Boolean
    ) {
        val pose = guiGraphics.pose()
        pose.pushPose()

        val recipe = recipeHolder.value!!

        if (!this.page!!.title1.isEmpty) {
            this.renderTitle(guiGraphics, this.page!!.title1, false, BookEntryScreen.PAGE_WIDTH / 2, 0)
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

        val append = if (recipe.isInfinite) "/s" else ""

        val c = Component.literal("Power: ${recipe.altarPower}$append")
        val i: Int = Minecraft.getInstance().font.width(c)
        guiGraphics.drawStringWithBackdrop(
            Minecraft.getInstance().font,
            c,
            recipeX + (c.toString().length) + 8,
            recipeY + 18 * 6 - 2,
            i,
            0xffffff
        )

        val fullMoon = recipe.celestialConditions.contains(RitualRecipe.Celestial.FULL_MOON)
        val newMoon = recipe.celestialConditions.contains(RitualRecipe.Celestial.NEW_MOON)
        val night = recipe.celestialConditions.contains(RitualRecipe.Celestial.NIGHT)
        val day = recipe.celestialConditions.contains(RitualRecipe.Celestial.DAY)
        val waxing = recipe.celestialConditions.contains(RitualRecipe.Celestial.WAXING)
        val waning = recipe.celestialConditions.contains(RitualRecipe.Celestial.WANING)

        val all = recipe.celestialConditions.isEmpty()



        var x = 20
        var y = 0
        blitWithAlpha(pose, Witchery.id("textures/gui/celestial/${if (day || all) "sun" else "empty"}.png"),
            x,
            y,
            0f,
            0f,
            10,
            10,
            10,
            10,
            1f
        )
        //var tooltipText = listOf(FormattedCharSequence.forward("Day", Style.EMPTY))
        //guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltipText, x, y)

        y += 11
        blitWithAlpha(pose, Witchery.id("textures/gui/celestial/${if (fullMoon || night || all) "full_moon" else "empty"}.png"),
            x,
            y,
            0f,
            0f,
            10,
            10,
            10,
            10,
            1f
        )
        //tooltipText = listOf(FormattedCharSequence.forward("Full Moon", Style.EMPTY))
        //guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltipText, x, y)

        y += 11
        blitWithAlpha(pose,  Witchery.id("textures/gui/celestial/${if (newMoon || night || all) "new_moon" else "empty"}.png"),
            x,
            y,
            0f,
            0f,
            10,
            10,
            10,
            10,
            1f
        )
        //tooltipText = listOf(FormattedCharSequence.forward("New Moon", Style.EMPTY))
        //guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltipText, x, y)

        x = 20 - 11
        y = 0 + 16
        blitWithAlpha(pose, Witchery.id("textures/gui/celestial/${if (waxing || night || all) "waxing_moon" else "empty"}.png"),
            x,
            y,
            0f,
            0f,
            10,
            10,
            10,
            10,
            1f
        )
        //tooltipText = listOf(FormattedCharSequence.forward("Waxing Moon", Style.EMPTY))
        //guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltipText, x, y)

        x = 20 + 11
        y = 0 + 16
        blitWithAlpha(pose, Witchery.id("textures/gui/celestial/${if (waning || night || all) "waning_moon" else "empty"}.png"),
            x,
            y,
            0f,
            0f,
            10,
            10,
            10,
            10,
            1f
        )
        //tooltipText = listOf(FormattedCharSequence.forward("Waning Moon", Style.EMPTY))
        //guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltipText, x, y)

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
                (- 16 - patternHeight / 2).toDouble(),
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
            RenderUtils.blitWithAlpha(poseStack, texture, 1, 1 + 32, 0f, 0f, 16, 16, 16, 16, 0.45f, 0x000000)
            RenderUtils.blitWithAlpha(poseStack, texture, 0, 0 + 32, 0f, 0f, 16, 16, 16, 16, 1f, color)
        }

        fun renderChalk(
            poseStack: PoseStack,
            texture: ResourceLocation
        ) {
            RenderUtils.blitWithAlpha(poseStack, texture, 1, 1 + 32, 0f, 0f, 16, 16, 16, 16, 0.45f, 0x000000)
            RenderUtils.blitWithAlpha(poseStack, texture, 0, 0 + 32, 0f, 0f, 16, 16, 16, 16)
        }
    }
}