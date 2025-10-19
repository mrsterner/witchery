package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.block.coffin.CoffinBlock
import dev.sterner.witchery.content.block.coffin.CoffinBlockEntity
import dev.sterner.witchery.client.model.CoffinModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BedPart
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB

class CoffinBlockEntityRenderer(private val ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<CoffinBlockEntity> {

    private val model = CoffinModel(ctx.bakeLayer(CoffinModel.LAYER_LOCATION))

    override fun getRenderBoundingBox(blockEntity: CoffinBlockEntity): AABB {
        val pos = blockEntity.blockPos
        return AABB(
            pos.x - 0.5,
            pos.y - 0.0,
            pos.z - 0.5,
            pos.x + 0.5,
            pos.y + 0.0,
            pos.z + 0.5
        )
    }

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

            val vertexConsumer =
                bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/coffin.png")))

            renderCoffin(poseStack, vertexConsumer, blockEntity, packedLight, packedOverlay, state)

            poseStack.popPose()
        }
    }

    private fun degToRad(degrees: Float): Float = degrees * (Math.PI.toFloat() / 180f)

    private fun renderCoffin(
        matrices: PoseStack,
        vertices: VertexConsumer,
        blockEntity: CoffinBlockEntity,
        light: Int,
        overlay: Int,
        blockState: BlockState
    ) {
        if (blockState.getValue(BlockStateProperties.BED_PART) == BedPart.FOOT) {

            val easedOpenProgress = blockEntity.getEasedOpenProgress()

            model.top.zRot = -degToRad(easedOpenProgress)
            model.bone.render(matrices, vertices, light, overlay)
        }
    }
}