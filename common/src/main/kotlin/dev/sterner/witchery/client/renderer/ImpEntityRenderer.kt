package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.ImpEntityModel
import dev.sterner.witchery.entity.ImpEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class ImpEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<ImpEntity, ImpEntityModel>(context, ImpEntityModel(context.bakeLayer(ImpEntityModel.LAYER_LOCATION)), 0.1f) {

    override fun render(
        entity: ImpEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        poseStack.scale(1.0f, -1.0f, 1.0f)
        poseStack.translate(0.0,-0.25,0.0)
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
        poseStack.popPose()
    }

    override fun getTextureLocation(entity: ImpEntity): ResourceLocation {
        return Witchery.id("textures/entity/imp.png")
    }
}