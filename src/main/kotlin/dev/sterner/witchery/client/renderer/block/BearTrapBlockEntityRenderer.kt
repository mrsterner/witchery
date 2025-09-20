package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.bear_trap.BearTrapBlockEntity
import dev.sterner.witchery.client.model.BearTrapModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.Mth

class BearTrapBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<BearTrapBlockEntity> {

    private val coreModel = BearTrapModel(ctx.bakeLayer(BearTrapModel.LAYER_LOCATION))

    override fun render(
        blockEntity: BearTrapBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {

        poseStack.pushPose()
        val smoothRot = Mth.lerp(partialTick, blockEntity.prevAngle.toFloat(), blockEntity.angle.toFloat()) - 90

        val rad = Math.toRadians(smoothRot.toDouble()).toFloat()

        coreModel.leftClaw.xRot = rad
        coreModel.rightClaw.xRot = -rad

        coreModel.crank.zRot = -20 - rad / 2

        val plateOffset = Mth.lerp(smoothRot / 80f, 0f, 0.0625f)
        coreModel.plate.y = plateOffset


        poseStack.scale(-1f, -1f, 1f)
        poseStack.translate(-0.5, -1.5, 0.5)

        coreModel.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/block/bear_trap.png"))),
            packedLight,
            packedOverlay,
            -0x1
        )
        poseStack.popPose()
    }

}