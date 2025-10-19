package dev.sterner.witchery.client

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.BindingCurseAttachment
import dev.sterner.witchery.registry.WitcheryShaders
import dev.sterner.witchery.features.ritual.BindingRitual
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.entity.LivingEntity
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.sin

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Witchery.MODID, bus = EventBusSubscriber.Bus.GAME, value = [Dist.CLIENT])
object BindingBoxRenderer {

    private val TEXTURE_FRONT = Witchery.id("textures/entity/soul_lantern_front.png")
    private val TEXTURE_BACK = Witchery.id("textures/entity/soul_lantern_front.png")
    private val TEXTURE_LEFT = Witchery.id("textures/entity/soul_lantern_right.png")
    private val TEXTURE_RIGHT = Witchery.id("textures/entity/soul_lantern_right.png")
    private val TEXTURE_TOP = Witchery.id("textures/entity/soul_lantern_top.png")
    private val TEXTURE_BOTTOM = Witchery.id("textures/entity/soul_lantern_top.png")
    private val TEXTURE_CORE = Witchery.id("textures/entity/soul_lantern_core.png")

    @SubscribeEvent
    fun onRenderLevel(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return

        val minecraft = Minecraft.getInstance()
        val level = minecraft.level ?: return
        val camera = minecraft.gameRenderer.mainCamera
        val cameraPos = camera.position
        val poseStack = event.poseStack
        val bufferSource = minecraft.renderBuffers().bufferSource()

        val nearbyEntities = level.entitiesForRendering().filter { entity ->
            entity is LivingEntity && entity.distanceToSqr(cameraPos) < 16384.0 // 128 blocks
        }

        for (entity in nearbyEntities) {
            if (entity !is LivingEntity) continue

            val bindingData = BindingCurseAttachment.getData(entity)
            if (!bindingData.isActive) continue

            val centerPos = bindingData.centerPos
            val radius = bindingData.radius

            poseStack.pushPose()

            poseStack.translate(
                centerPos.x + 0.5 - cameraPos.x,
                centerPos.y + 0.5 - cameraPos.y,
                centerPos.z + 0.5 - cameraPos.z
            )

            val fadeProgress = (bindingData.duration / BindingRitual.BINDING_DURATION.toFloat()).coerceIn(0f, 1f)

            renderBindingBox(radius.toFloat(), fadeProgress, poseStack, bufferSource)

            poseStack.popPose()
        }
    }

    private fun renderBindingBox(
        radius: Float,
        fadeProgress: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource
    ) {
        val shader = WitcheryShaders.soulLantern ?: return

        val time = System.currentTimeMillis() / 1000.0
        val wobble = sin(time * 2.0).toFloat() * (radius * 0.01f)
        val wobble2 = cos(time * 1.5).toFloat() * (radius * 0.01f)

        val min = -radius + wobble
        val max = radius + wobble2

        val matrix = poseStack.last().pose()

        if (bufferSource is MultiBufferSource.BufferSource) {
            bufferSource.endBatch()
        }

        val prevShader = RenderSystem.getShader()
        val prevCull = GL11.glIsEnabled(GL11.GL_CULL_FACE)
        val prevDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK)

        RenderSystem.enableBlend()
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE)
        RenderSystem.disableCull()
        RenderSystem.depthMask(false)
        RenderSystem.enableDepthTest()
        RenderSystem.depthFunc(GL11.GL_LEQUAL)

        shader.safeGetUniform("Alpha").set(fadeProgress * 0.5f)
        RenderSystem.setShader { shader }

        RenderSystem.setShaderTexture(0, TEXTURE_FRONT)
        var builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builder, matrix, min, min, max, max, min, max, max, max, max, min, max, max)
        BufferUploader.drawWithShader(builder.buildOrThrow())

        RenderSystem.setShaderTexture(0, TEXTURE_BACK)
        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builder, matrix, max, min, min, min, min, min, min, max, min, max, max, min)
        BufferUploader.drawWithShader(builder.buildOrThrow())

        RenderSystem.setShaderTexture(0, TEXTURE_LEFT)
        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builder, matrix, min, min, min, min, min, max, min, max, max, min, max, min)
        BufferUploader.drawWithShader(builder.buildOrThrow())

        RenderSystem.setShaderTexture(0, TEXTURE_RIGHT)
        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builder, matrix, max, min, max, max, min, min, max, max, min, max, max, max)
        BufferUploader.drawWithShader(builder.buildOrThrow())

        RenderSystem.setShaderTexture(0, TEXTURE_TOP)
        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builder, matrix, min, max, max, max, max, max, max, max, min, min, max, min)
        BufferUploader.drawWithShader(builder.buildOrThrow())

        RenderSystem.setShaderTexture(0, TEXTURE_BOTTOM)
        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builder, matrix, min, min, min, max, min, min, max, min, max, min, min, max)
        BufferUploader.drawWithShader(builder.buildOrThrow())

        val coreSize = radius * 0.97f
        RenderSystem.setShaderTexture(0, TEXTURE_CORE)

        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builder, matrix, -coreSize, -coreSize, coreSize, coreSize, -coreSize, coreSize, coreSize, coreSize, coreSize, -coreSize, coreSize, coreSize)
        BufferUploader.drawWithShader(builder.buildOrThrow())

        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builder, matrix, coreSize, -coreSize, -coreSize, -coreSize, -coreSize, -coreSize, -coreSize, coreSize, -coreSize, coreSize, coreSize, -coreSize)
        BufferUploader.drawWithShader(builder.buildOrThrow())

        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builder, matrix, -coreSize, -coreSize, -coreSize, -coreSize, -coreSize, coreSize, -coreSize, coreSize, coreSize, -coreSize, coreSize, -coreSize)
        BufferUploader.drawWithShader(builder.buildOrThrow())

        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builder, matrix, coreSize, -coreSize, coreSize, coreSize, -coreSize, -coreSize, coreSize, coreSize, -coreSize, coreSize, coreSize, coreSize)
        BufferUploader.drawWithShader(builder.buildOrThrow())

        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builder, matrix, -coreSize, coreSize, coreSize, coreSize, coreSize, coreSize, coreSize, coreSize, -coreSize, -coreSize, coreSize, -coreSize)
        BufferUploader.drawWithShader(builder.buildOrThrow())

        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builder, matrix, -coreSize, -coreSize, -coreSize, coreSize, -coreSize, -coreSize, coreSize, -coreSize, coreSize, -coreSize, -coreSize, coreSize)
        BufferUploader.drawWithShader(builder.buildOrThrow())

        RenderSystem.defaultBlendFunc()
        RenderSystem.disableBlend()
        RenderSystem.depthMask(prevDepthMask)
        if (prevCull) {
            RenderSystem.enableCull()
        }
        if (prevShader != null) {
            RenderSystem.setShader { prevShader }
        }
    }

    private fun addGlowFace(
        builder: BufferBuilder,
        matrix: Matrix4f,
        x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float,
        x3: Float, y3: Float, z3: Float,
        x4: Float, y4: Float, z4: Float
    ) {
        val light = 240

        builder.addVertex(matrix, x1, y1, z1).setColor(255, 255, 255, 255).setUv(0f, 0f).setUv2(light, light)
        builder.addVertex(matrix, x2, y2, z2).setColor(255, 255, 255, 255).setUv(1f, 0f).setUv2(light, light)
        builder.addVertex(matrix, x3, y3, z3).setColor(255, 255, 255, 255).setUv(1f, 1f).setUv2(light, light)
        builder.addVertex(matrix, x4, y4, z4).setColor(255, 255, 255, 255).setUv(0f, 1f).setUv2(light, light)
    }
}