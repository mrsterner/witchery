package dev.sterner.witchery.api

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

    fun innerRenderBlood(guiGraphics: GuiGraphics, maxBlood: Int, bloodPool: Int , y: Int, x: Int) {

        val dropCount = maxBlood / 300
        val fullIcons = bloodPool / 300
        val partialFill = bloodPool % 300
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
}