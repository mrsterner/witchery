package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.spining_wheel.SpinningWheelBlockEntity
import dev.sterner.witchery.client.model.SpinningWheelBlockEntityModel
import dev.sterner.witchery.client.model.SpinningWheelWheelBlockEntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import kotlin.math.sin

class SpinningWheelBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<SpinningWheelBlockEntity> {

    private var wheelModel =
        SpinningWheelWheelBlockEntityModel(ctx.bakeLayer(SpinningWheelWheelBlockEntityModel.LAYER_LOCATION))
    private var baseModel = SpinningWheelBlockEntityModel(ctx.bakeLayer(SpinningWheelBlockEntityModel.LAYER_LOCATION))

    override fun render(
        blockEntity: SpinningWheelBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {

        val gameTime = blockEntity.level?.gameTime ?: 0
        val rotation = if (blockEntity.dataAccess.get(0) > 0) ((gameTime + partialTick) % 360) else 0f

        poseStack.pushPose()
        poseStack.scale(-1f, -1f, 1f)
        poseStack.translate(-0.5, -1.5, 0.5)
        baseModel.base.render(
            poseStack,
            bufferSource.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/block/spinner.png"))),
            packedLight,
            packedOverlay
        )
        val oscillationAngle = 10.0 * sin(Math.toRadians(rotation.toDouble() * 8))

        poseStack.pushPose()

        poseStack.translate(0.0, -0.4, -0.25)
        poseStack.translate(0.0, 1.0, 0.0)
        poseStack.mulPose(Axis.XP.rotationDegrees(oscillationAngle.toFloat()))
        poseStack.translate(0.0, -0.55, 0.2)

        baseModel.string.render(
            poseStack,
            bufferSource.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/block/spinner.png"))),
            packedLight,
            packedOverlay
        )

        poseStack.popPose()
        poseStack.popPose()

        poseStack.pushPose()



        poseStack.translate(0.5, -0.7, 0.8)

        poseStack.pushPose()
        poseStack.translate(0.0, 1.5, 0.0)
        poseStack.mulPose(Axis.XP.rotationDegrees(rotation))
        poseStack.translate(0.0, -1.5, 0.0)

        wheelModel.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/block/spinning_wheel_wheel.png"))),
            packedLight,
            packedOverlay
        )

        poseStack.popPose()

        poseStack.popPose()
    }
}