package dev.sterner.witchery.util

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import dev.sterner.witchery.Witchery.id
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import org.joml.Matrix4f

object RenderUtils {

    /**
     * Blit from GuiGraphics but also handles alpha and supports color
     */
    fun blitWithAlpha(
        poseStack: PoseStack,
        atlasLocation: ResourceLocation?,
        x: Int,
        y: Int,
        uOffset: Float,
        vOffset: Float,
        width: Int,
        height: Int,
        textureWidth: Int,
        textureHeight: Int,
        alpha: Float = 1.0f,
        color: Int = 0xFFFFFF
    ) {
        val red = (color shr 16 and 255) / 255.0f
        val green = (color shr 8 and 255) / 255.0f
        val blue = (color and 255) / 255.0f

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        if (atlasLocation != null) {
            RenderSystem.setShaderTexture(0, atlasLocation)
        }
        RenderSystem.setShader { GameRenderer.getPositionTexColorShader() }

        val matrix4f: Matrix4f = poseStack.last().pose()
        val bufferBuilder =
            Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
        val minU = uOffset / textureWidth.toFloat()
        val maxU = (uOffset + width) / textureWidth.toFloat()
        val minV = vOffset / textureHeight.toFloat()
        val maxV = (vOffset + height) / textureHeight.toFloat()

        bufferBuilder.addVertex(matrix4f, x.toFloat(), y.toFloat(), 0f).setColor(red, green, blue, alpha)
            .setUv(minU, minV)
        bufferBuilder.addVertex(matrix4f, x.toFloat(), (y + height).toFloat(), 0f).setColor(red, green, blue, alpha)
            .setUv(minU, maxV)
        bufferBuilder.addVertex(matrix4f, (x + width).toFloat(), (y + height).toFloat(), 0f)
            .setColor(red, green, blue, alpha).setUv(maxU, maxV)
        bufferBuilder.addVertex(matrix4f, (x + width).toFloat(), y.toFloat(), 0f).setColor(red, green, blue, alpha)
            .setUv(maxU, minV)

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())

        RenderSystem.disableBlend()
    }


    fun innerRenderBlood(guiGraphics: GuiGraphics, living: LivingEntity, y: Int, x: Int) {
        val data = BloodPoolLivingEntityAttachment.getData(living)
        val bloodPool = data.bloodPool
        val maxBlood = data.maxBlood
        innerRenderBlood(guiGraphics, maxBlood, bloodPool, y, x)
    }

    /**
     * Renders a living entity's blood drops. Used for vampire hot bar and also for hud element when looking at an entity with blood
     */
    fun innerRenderBlood(guiGraphics: GuiGraphics, maxBlood: Int, bloodPool: Int, y: Int, x: Int) {

        val dropCount = maxBlood / WitcheryConstants.BLOOD_DROP
        val fullIcons = bloodPool / WitcheryConstants.BLOOD_DROP
        val partialFill = bloodPool % WitcheryConstants.BLOOD_DROP
        val iconSize = 10
        for (i in 0 until dropCount) {
            val xPos = x - i * 7 - 8

            blitWithAlpha(
                guiGraphics.pose(),
                id("textures/gui/blood_pool_empty.png"),
                xPos,
                y - 1,
                0f,
                0f,
                iconSize,
                iconSize,
                iconSize,
                iconSize,
                1.0f,
                0xFFFFFF
            )

            if (i < fullIcons) {
                blitWithAlpha(
                    guiGraphics.pose(),
                    id("textures/gui/blood_pool_full.png"),
                    xPos,
                    y - 1,
                    0f,
                    0f,
                    iconSize,
                    iconSize,
                    iconSize,
                    iconSize,
                    1.0f,
                    0xFFFFFF
                )
            } else if (i == fullIcons && partialFill > 0) {
                val filledHeight = (partialFill * iconSize) / 300
                val emptyHeight = iconSize - filledHeight
                blitWithAlpha(
                    guiGraphics.pose(),
                    id("textures/gui/blood_pool_full.png"),
                    xPos,
                    y + emptyHeight - 1,
                    0f,
                    emptyHeight.toFloat(),
                    iconSize,
                    filledHeight,
                    iconSize,
                    iconSize,
                    1.0f,
                    0xFFFFFF
                )
            }
        }
    }

    /**
     * Render the bat HUD icons showing how much time bat form has left
     */
    fun innerRenderBat(guiGraphics: GuiGraphics, maxTicks: Int, ticks: Int, y: Int, x: Int) {
        val q = 60 * 20
        val dropCount = maxTicks / q
        val fullIcons = ticks / q
        val partialFill = ticks % q
        val width = 13
        val height = 7

        for (i in 0 until dropCount) {
            val xPos = x + i * 12 - 8

            // Draw empty icon first (for all icons)
            blitWithAlpha(
                guiGraphics.pose(),
                id("textures/gui/vampire_abilities/bat_form_empty.png"),
                xPos,
                y - 1,
                0f,
                0f,
                width,
                height,
                width,
                height,
                1.0f,
                0xFFFFFF
            )

            // Draw full icon if this icon is within the full range
            if (i < fullIcons) {
                blitWithAlpha(
                    guiGraphics.pose(),
                    id("textures/gui/vampire_abilities/bat_form_full.png"),
                    xPos,
                    y - 1,
                    0f,
                    0f,
                    width,
                    height,
                    width,
                    height,
                    1.0f,
                    0xFFFFFF
                )
            } else if (i == fullIcons && partialFill > 0) {
                // Calculate the filled width (instead of height)
                val filledWidth = (partialFill * width) / q
                val emptyWidth = width - filledWidth

                // Draw the full portion of the last icon (from left to right)
                blitWithAlpha(
                    guiGraphics.pose(),
                    id("textures/gui/vampire_abilities/bat_form_full.png"),
                    xPos,
                    y - 1,
                    0f,
                    0f,
                    filledWidth,
                    height,
                    width,
                    height,
                    1.0f,
                    0xFFFFFF
                )
            }
        }
    }

    fun renderChalk(
        poseStack: PoseStack,
        px: Int = 0,
        py: Int = 0,
        texture: ResourceLocation,
        color: Int
    ) {
        blitWithAlpha(poseStack, texture, 1 + px, 1 + 32 + py, 0f, 0f, 16, 16, 16, 16, 0.45f, 0x000000)
        blitWithAlpha(poseStack, texture, 0 + px, 0 + 32 + py, 0f, 0f, 16, 16, 16, 16, 1f, color)
    }

    fun renderChalk(
        poseStack: PoseStack,
        px: Int = 0,
        py: Int = 0,
        texture: ResourceLocation
    ) {
        blitWithAlpha(poseStack, texture, 1 + px, 1 + 32 + py, 0f, 0f, 16, 16, 16, 16, 0.45f, 0x000000)
        blitWithAlpha(poseStack, texture, 0 + px, 0 + 32 + py, 0f, 0f, 16, 16, 16, 16)
    }
}