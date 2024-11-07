package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexBuffer
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlockEntity
import dev.sterner.witchery.client.model.SpiritPortalBlockEntityModel
import dev.sterner.witchery.client.model.SpiritPortalPortalModel
import dev.sterner.witchery.registry.WitcheryRenderTypes
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class SpiritPortalBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<SpiritPortalBlockEntity> {

    private val model = SpiritPortalBlockEntityModel(ctx.bakeLayer(SpiritPortalBlockEntityModel.LAYER_LOCATION))
    private val modelShaderModel = SpiritPortalPortalModel(ctx.bakeLayer(SpiritPortalPortalModel.LAYER_LOCATION))

    override fun render(
        blockEntity: SpiritPortalBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()

        poseStack.translate(0.5, 1.5, 0.0)
        val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - dir))
        poseStack.scale(-1f, -1f, 1f)
        poseStack.translate(0.0, 0.01,-0.025)
        poseStack.scale(0.96f, 0.96f, 0.96f)
        poseStack.mulPose(Axis.YP.rotationDegrees(180f))
        if (false) {
            renderPentagon(poseStack, bufferSource, packedLight, packedOverlay)
        } else {
            modelShaderModel.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(WitcheryRenderTypes.SPIRIT_PORTAL.apply(Witchery.id("textures/block/spirit_door_portal.png"))),
                packedLight,
                packedOverlay
            )
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(-180f))

        poseStack.scale(1.04f, 1.04f, 1.04f)
        poseStack.translate(0.0, -0.01,0.025)
        val progress = blockEntity.getRenderProgress(partialTick)
        val doorAngle = progress * 90.0f * (-1)

        poseStack.pushPose()
        poseStack.translate(-1.1, 0.0, 0.0)
        poseStack.mulPose(Axis.YP.rotationDegrees(doorAngle))
        poseStack.translate(1.1, 0.0, 0.0)
        model.lDoor.render(
            poseStack,
            bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/spirit_door.png"))),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()

        poseStack.pushPose()
        poseStack.translate(1.1, 0.0, 0.0)
        poseStack.mulPose(Axis.YP.rotationDegrees(-doorAngle))
        poseStack.translate(-1.1, 0.0, 0.0)
        model.rDoor.render(
            poseStack,
            bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/spirit_door.png"))),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()

        poseStack.popPose()
    }

    private fun renderPentagon(
        pose: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val radius = 1.0f
        val centerX = 0.0f
        val centerY = 0.0f
        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Int>()
        val colors = mutableListOf<Float>()
        val uvs = mutableListOf<Float>()

        for (i in 0..5) {
            val angle = Math.toRadians((360.0 / 5.0) * i.toDouble()).toFloat()
            val x = centerX + radius * Math.cos(angle.toDouble()).toFloat()
            val y = centerY + radius * Math.sin(angle.toDouble()).toFloat()
            vertices.add(x)
            vertices.add(y)
            vertices.add(0.0f)

            colors.add(1.0f)
            colors.add(1.0f)
            colors.add(1.0f)
            colors.add(1.0f)

            uvs.add(i.toFloat() / 5.0f)
            uvs.add(0.0f)
        }

        for (i in 1..4) {
            indices.add(0)
            indices.add(i)
            indices.add(i + 1)
        }
        indices.add(0)
        indices.add(4)
        indices.add(1)

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        val renderType = WitcheryRenderTypes.SPIRIT_PORTAL.apply(Witchery.id("textures/block/spirit_door_portal.png"))
        val vertexConsumer = bufferSource.getBuffer(renderType)

        val mat =  pose.last().pose()

        for (i in indices.indices step 3) {
            val x1 = vertices[indices[i] * 3]
            val y1 = vertices[indices[i] * 3 + 1]
            val x2 = vertices[indices[i + 1] * 3]
            val y2 = vertices[indices[i + 1] * 3 + 1]
            val x3 = vertices[indices[i + 2] * 3]
            val y3 = vertices[indices[i + 2] * 3 + 1]

            vertexConsumer.addVertex(mat, x1, y1, 0.0f)
                .setColor(colors[indices[i] * 4], colors[indices[i] * 4 + 1], colors[indices[i] * 4 + 2], colors[indices[i] * 4 + 3])
                .setUv(uvs[indices[i] * 2], uvs[indices[i] * 2 + 1])
                .setUv1(packedLight, packedOverlay)
                .setUv2(packedOverlay, packedOverlay)
                .setNormal(1f, 1f, 1f)

            vertexConsumer.addVertex(mat, x2, y2, 0.0f)
                .setColor(colors[indices[i + 1] * 4], colors[indices[i + 1] * 4 + 1], colors[indices[i + 1] * 4 + 2], colors[indices[i + 1] * 4 + 3])
                .setUv(uvs[indices[i + 1] * 2], uvs[indices[i + 1] * 2 + 1])
                .setUv1(packedLight, packedOverlay)
                .setUv2(packedOverlay, packedOverlay)
                .setNormal(1f, 1f, 1f)

            vertexConsumer.addVertex(mat, x3, y3, 0.0f)
                .setColor(colors[indices[i + 2] * 4], colors[indices[i + 2] * 4 + 1], colors[indices[i + 2] * 4 + 2], colors[indices[i + 2] * 4 + 3])
                .setUv(uvs[indices[i + 2] * 2], uvs[indices[i + 2] * 2 + 1])
                .setUv1(packedLight, packedOverlay)
                .setUv2(packedOverlay, packedOverlay)
                .setNormal(1f, 1f, 1f)
        }
    }
}