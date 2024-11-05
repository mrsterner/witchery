package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.BroomEntityModel
import dev.sterner.witchery.entity.BroomEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation

class BroomEntityRenderer(val ctx: EntityRendererProvider.Context) : EntityRenderer<BroomEntity>(ctx) {

    private val broomModel = BroomEntityModel(ctx.bakeLayer(BroomEntityModel.LAYER_LOCATION))

    override fun render(
        entity: BroomEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {

        poseStack.pushPose()
        poseStack.translate(0.0, -1.0, 0.0)
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - entityYaw))
        broomModel.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity))),
            packedLight,
            OverlayTexture.NO_OVERLAY
        )
        poseStack.popPose()

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
    }

    override fun getTextureLocation(entity: BroomEntity): ResourceLocation {
        return Witchery.id("textures/entity/broom.png")
    }
}