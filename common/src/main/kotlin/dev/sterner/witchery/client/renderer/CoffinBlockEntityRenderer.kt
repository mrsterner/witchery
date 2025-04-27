package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.coffin.CoffinBlock
import dev.sterner.witchery.block.coffin.CoffinBlockEntity
import dev.sterner.witchery.client.model.CoffinModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.BedBlock.PART
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BedPart
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import kotlin.math.pow

class CoffinBlockEntityRenderer(private val ctx: BlockEntityRendererProvider.Context) : BlockEntityRenderer<CoffinBlockEntity> {

    private val model = CoffinModel(ctx.bakeLayer(CoffinModel.LAYER_LOCATION))
    private var openProgress = 0f

    override fun render(
        blockEntity: CoffinBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val state = blockEntity.blockState
        if (state.block is CoffinBlock) {
            poseStack.pushPose()

            val rotation = state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()
            poseStack.translate(0.5, 0.5, 0.5)
            poseStack.mulPose(Axis.YP.rotationDegrees(-rotation))
            poseStack.translate(-0.5, -0.5, -0.5)

            poseStack.mulPose(Axis.YP.rotationDegrees(180f))
            poseStack.mulPose(Axis.ZP.rotationDegrees(180f))
            poseStack.translate(0.5, -1.5, -0.5)

            val vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/coffin.png")))
            val isOpen = state.getValue(CoffinBlock.OPEN)
            renderCoffin(poseStack, vertexConsumer, isOpen, packedLight, packedOverlay, state)

            poseStack.popPose()
        }
    }

    private fun degToRad(degrees: Float): Float = degrees * (Math.PI.toFloat() / 180f)

    private fun easeInOut(x: Float): Float {
        val v = 45
        val c = -v / 2
        val g = -2
        val s = 16
        return (v / (1 + 10.0.pow((g * (c + x) / s).toDouble()))).toFloat()
    }

    private fun renderCoffin(
        matrices: PoseStack,
        vertices: VertexConsumer,
        isOpen: Boolean,
        light: Int,
        overlay: Int,
        blockState: BlockState
    ) {
        if (blockState.getValue(PART) == BedPart.FOOT) {
            openProgress = when {
                isOpen && openProgress < 45f -> openProgress + 1f
                !isOpen && openProgress > 0f -> openProgress - 1f
                else -> openProgress
            }

            model.top.zRot = -degToRad(easeInOut(openProgress))
            model.bone.render(matrices, vertices, light, overlay)
        }
    }
}