package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.effigy.EffigyBlockEntity
import dev.sterner.witchery.client.model.BansheeEntityModel
import dev.sterner.witchery.client.model.SpectreEntityModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction

class EffigyBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<EffigyBlockEntity> {

    var spectreHeadModel = SpectreEntityModel(ctx.bakeLayer(SpectreEntityModel.LAYER_LOCATION))
    var bansheeHeadModel = BansheeEntityModel(ctx.bakeLayer(BansheeEntityModel.LAYER_LOCATION))


    override fun render(
        blockEntity: EffigyBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val spectreCount = blockEntity.specterCount.coerceAtMost(5)
        val bansheeCount = blockEntity.bansheeCount.coerceAtMost(5)

        val spectreY = 1.2f
        val bansheeY = 0.8f
        val radius = 0.8f

        val spectreRenderType = RenderType.entityTranslucent(Witchery.id("textures/entity/spectre.png"))
        val bansheeRenderType = RenderType.entityTranslucent(Witchery.id("textures/entity/banshee.png"))

        val time = (blockEntity.level?.gameTime ?: 0L) + partialTick
        val rotationSpeed = 0.02f
        val rotationSpeedier = 0.03f

        for (i in 0 until spectreCount) {
            val angle = ((i.toFloat() / spectreCount) * 2 * Math.PI + time * rotationSpeedier) % (2 * Math.PI)

            val x = radius * kotlin.math.cos(angle)
            val z = radius * kotlin.math.sin(angle)

            poseStack.pushPose()
            poseStack.translate(0.5 + x, spectreY.toDouble(), 0.5 + z)
            poseStack.scale(0.5f, -0.5f, 0.5f)
            poseStack.mulPose(Axis.YP.rotationDegrees((-angle * (180 / Math.PI)).toFloat() + 180))
            spectreHeadModel.head.render(poseStack, bufferSource.getBuffer(spectreRenderType), packedLight, packedOverlay, 0x40FFFFFF)

            poseStack.popPose()
        }

        for (i in 0 until bansheeCount) {
            val angle = ((i.toFloat() / bansheeCount) * 2 * Math.PI + time * rotationSpeed) % (2 * Math.PI)

            val x = radius * kotlin.math.cos(angle)
            val z = radius * kotlin.math.sin(angle)

            poseStack.pushPose()
            poseStack.translate(0.5 + x, bansheeY.toDouble(), 0.5 + z)
            poseStack.scale(0.5f, -0.5f, 0.5f)
            poseStack.mulPose(Axis.YP.rotationDegrees((-angle * (180 / Math.PI)).toFloat() + 180))

            bansheeHeadModel.head.render(poseStack, bufferSource.getBuffer(bansheeRenderType), packedLight, packedOverlay, 0x40FFFFFF)
            bansheeHeadModel.headMain.render(poseStack, bufferSource.getBuffer(bansheeRenderType), packedLight, packedOverlay, 0x40FFFFFF)

            poseStack.popPose()
        }
    }

}