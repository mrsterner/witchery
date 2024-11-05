package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.poppet.PoppetBlockEntity
import dev.sterner.witchery.client.model.PoppetModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.properties.BlockStateProperties


class PoppetBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<PoppetBlockEntity> {

    val model = PoppetModel(ctx.bakeLayer(PoppetModel.LAYER_LOCATION))

    override fun render(
        blockEntity: PoppetBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5, 1.6, 0.5)
        val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val dirYRot = dir.toYRot()
        poseStack.mulPose(Axis.YP.rotationDegrees(-dirYRot))
        poseStack.scale(-1f, -1f, 1f)
        this.model.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/poppet.png"))),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()
    }
}