package dev.sterner.witchery.api

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.resources.ResourceLocation
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

        RenderSystem.setShaderTexture(0, atlasLocation)
        RenderSystem.setShader { GameRenderer.getPositionTexColorShader() }

        val matrix4f: Matrix4f = poseStack.last().pose()
        val bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
        val minU = uOffset / textureWidth.toFloat()
        val maxU = (uOffset + width) / textureWidth.toFloat()
        val minV = vOffset / textureHeight.toFloat()
        val maxV = (vOffset + height) / textureHeight.toFloat()

        bufferBuilder.addVertex(matrix4f, x.toFloat(), y.toFloat(), 0f).setColor(red, green, blue, alpha).setUv(minU, minV)
        bufferBuilder.addVertex(matrix4f, x.toFloat(), (y + height).toFloat(), 0f).setColor(red, green, blue, alpha).setUv(minU, maxV)
        bufferBuilder.addVertex(matrix4f, (x + width).toFloat(), (y + height).toFloat(), 0f).setColor(red, green, blue, alpha).setUv(maxU, maxV)
        bufferBuilder.addVertex(matrix4f, (x + width).toFloat(), y.toFloat(), 0f).setColor(red, green, blue, alpha).setUv(maxU, minV)

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())

        RenderSystem.disableBlend()
    }
}