package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.MirrorModel
import dev.sterner.witchery.content.block.mirror.MirrorBlockEntity
import dev.sterner.witchery.core.registry.WitcheryRenderTypes
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11

class MirrorBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<MirrorBlockEntity> {

    private val model = MirrorModel(ctx.bakeLayer(MirrorModel.LAYER_LOCATION))
    private val texture = Witchery.id("textures/block/mirror.png")
    private val portalTexture = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/stone.png")

    override fun getRenderBoundingBox(blockEntity: MirrorBlockEntity): AABB {
        val pos = blockEntity.blockPos
        return AABB(
            pos.x - 1.0, pos.y - 1.0, pos.z - 1.0,
            pos.x + 2.0, pos.y + 3.0, pos.z + 2.0
        )
    }

    override fun render(
        blockEntity: MirrorBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val dirYRot = dir.toYRot()
        val isDemon = blockEntity.mode == MirrorBlockEntity.Mode.DEMONIC || blockEntity.hasDemon

        if (isDemon) {
            renderDemonMirror(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, dirYRot)
        } else {
            renderNormalMirror(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, dirYRot)
        }
    }

    private fun renderNormalMirror(
        blockEntity: MirrorBlockEntity,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int,
        dirYRot: Float
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5, 0.0, 0.5)
        poseStack.mulPose(Axis.YP.rotationDegrees(-dirYRot))
        poseStack.scale(-1f, -1f, 1f)

        if (blockEntity.isSmallMirror) {
            model.single.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                packedOverlay,
                -1
            )
        } else {
            model.full.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                packedOverlay,
                -1
            )
        }

        poseStack.popPose()
    }

    private fun renderDemonMirror(
        blockEntity: MirrorBlockEntity,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int,
        dirYRot: Float
    ) {
        if (bufferSource is MultiBufferSource.BufferSource) {
            bufferSource.endBatch()
        }

        poseStack.pushPose()
        poseStack.translate(0.5, 0.0, 0.5)
        poseStack.mulPose(Axis.YP.rotationDegrees(-dirYRot))
        poseStack.scale(-1f, -1f, 1f)

        if (blockEntity.isSmallMirror) {
            model.frame2.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                packedOverlay,
                -1
            )
        } else {
            model.frame.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                packedOverlay,
                -1
            )
        }

        if (bufferSource is MultiBufferSource.BufferSource) {
            bufferSource.endBatch()
        }

        RenderSystem.enableCull()
        poseStack.pushPose()
        poseStack.translate(0.0, -2.0, -1.0)

        RenderSystem.enableDepthTest()
        RenderSystem.depthFunc(GL11.GL_LEQUAL)

        renderPortalEffect(blockEntity, poseStack, bufferSource)

        poseStack.popPose()

        if (bufferSource is MultiBufferSource.BufferSource) {
            bufferSource.endBatch()
        }

        poseStack.popPose()
    }

    private fun renderPortalEffect(
        blockEntity: MirrorBlockEntity,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource
    ) {
        val buffer = bufferSource.getBuffer(WitcheryRenderTypes.MIRROR_PORTAL.apply(portalTexture))
        val pose = poseStack.last()
        val matrix = pose.pose()

        val (x1, x2, y1, y2, z) = if (blockEntity.isSmallMirror) {
            Tuple5(-7.0f / 16.0f, 7.0f / 16.0f, 9.0f / 16.0f, 23.0f / 16.0f, 1.0f / 16.0f)
        } else {
            Tuple5(-7.0f / 16.0f, 7.0f / 16.0f, 1.0f / 16.0f, 31.0f / 16.0f, 8.0f / 16.0f)
        }

        val depth = 1.5f
        val segments = 12

        val time = System.currentTimeMillis() / 1000.0f

        for (i in 0 until segments) {
            val t = i.toFloat() / segments.toFloat()
            val nextT = (i + 1).toFloat() / segments.toFloat()

            val currentZ = z - t * depth
            val nextZ = z - nextT * depth

            val scale = 1.0f - t * 0.3f
            val nextScale = 1.0f - nextT * 0.3f

            val currentX1 = x1 * scale
            val currentX2 = x2 * scale
            val currentY1 = y1 + (y2 - y1) * (1.0f - scale) * 0.5f
            val currentY2 = y2 - (y2 - y1) * (1.0f - scale) * 0.5f

            val nextX1 = x1 * nextScale
            val nextX2 = x2 * nextScale
            val nextY1 = y1 + (y2 - y1) * (1.0f - nextScale) * 0.5f
            val nextY2 = y2 - (y2 - y1) * (1.0f - nextScale) * 0.5f

            val uvOffset = t * 2.0f + time * 0.5f
            val nextUvOffset = nextT * 2.0f + time * 0.5f

            val alpha = (255 * (1.0f - t * 0.7f)).toInt().coerceIn(0, 255)

            addVerticalQuad(
                buffer, matrix, pose,
                currentX1, currentY2, currentZ,
                currentX1, currentY1, currentZ,
                nextX1, nextY1, nextZ,
                nextX1, nextY2, nextZ,
                uvOffset, nextUvOffset, alpha
            )

            addVerticalQuad(
                buffer, matrix, pose,
                currentX2, currentY1, currentZ,
                currentX2, currentY2, currentZ,
                nextX2, nextY2, nextZ,
                nextX2, nextY1, nextZ,
                uvOffset, nextUvOffset, alpha
            )

            addVerticalQuad(
                buffer, matrix, pose,
                currentX2, currentY2, currentZ,
                currentX1, currentY2, currentZ,
                nextX1, nextY2, nextZ,
                nextX2, nextY2, nextZ,
                uvOffset, nextUvOffset, alpha
            )

            addVerticalQuad(
                buffer, matrix, pose,
                currentX1, currentY1, currentZ,
                currentX2, currentY1, currentZ,
                nextX2, nextY1, nextZ,
                nextX1, nextY1, nextZ,
                uvOffset, nextUvOffset, alpha
            )
        }

        val endZ = z - depth
        val endScale = 1.0f - 0.3f
        val endX1 = x1 * endScale
        val endX2 = x2 * endScale
        val endY1 = y1 + (y2 - y1) * (1.0f - endScale) * 0.5f
        val endY2 = y2 - (y2 - y1) * (1.0f - endScale) * 0.5f

        buffer.addVertex(matrix, endX1, endY1, endZ).setColor(20, 20, 40, 255).setUv(0f, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0f, 0f, 1f)
        buffer.addVertex(matrix, endX2, endY1, endZ).setColor(20, 20, 40, 255).setUv(1f, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0f, 0f, 1f)
        buffer.addVertex(matrix, endX2, endY2, endZ).setColor(20, 20, 40, 255).setUv(1f, 1f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0f, 0f, 1f)
        buffer.addVertex(matrix, endX1, endY2, endZ).setColor(20, 20, 40, 255).setUv(0f, 1f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0f, 0f, 1f)
    }

    private fun addVerticalQuad(
        buffer: VertexConsumer,
        matrix: Matrix4f,
        pose: PoseStack.Pose,
        x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float,
        x3: Float, y3: Float, z3: Float,
        x4: Float, y4: Float, z4: Float,
        uvOffset: Float,
        nextUvOffset: Float,
        alpha: Int
    ) {

        val v1x = x2 - x1
        val v1y = y2 - y1
        val v1z = z2 - z1
        val v2x = x3 - x1
        val v2y = y3 - y1
        val v2z = z3 - z1

        var nx = v1y * v2z - v1z * v2y
        var ny = v1z * v2x - v1x * v2z
        var nz = v1x * v2y - v1y * v2x

        val length = kotlin.math.sqrt(nx * nx + ny * ny + nz * nz)
        if (length > 0.001f) {
            nx /= length
            ny /= length
            nz /= length
        }

        buffer.addVertex(matrix, x1, y1, z1).setColor(255, 255, 255, alpha)
            .setUv(0f, uvOffset).setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT).setNormal(pose, nx, ny, nz)
        buffer.addVertex(matrix, x2, y2, z2).setColor(255, 255, 255, alpha)
            .setUv(1f, uvOffset).setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT).setNormal(pose, nx, ny, nz)
        buffer.addVertex(matrix, x3, y3, z3).setColor(255, 255, 255, alpha)
            .setUv(1f, nextUvOffset).setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT).setNormal(pose, nx, ny, nz)
        buffer.addVertex(matrix, x4, y4, z4).setColor(255, 255, 255, alpha)
            .setUv(0f, nextUvOffset).setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT).setNormal(pose, nx, ny, nz)
    }

    data class Tuple5(val v1: Float, val v2: Float, val v3: Float, val v4: Float, val v5: Float)
}