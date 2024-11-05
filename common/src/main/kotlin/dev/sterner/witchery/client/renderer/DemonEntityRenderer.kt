package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.DemonEntityModel
import dev.sterner.witchery.entity.DemonEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class DemonEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<DemonEntity, DemonEntityModel>(
        context,
        DemonEntityModel(context.bakeLayer(DemonEntityModel.LAYER_LOCATION)),
        0.8f
    ) {

    override fun render(
        entity: DemonEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
        poseStack.popPose()
    }

    override fun getTextureLocation(entity: DemonEntity): ResourceLocation {
        return Witchery.id("textures/entity/demon.png")
    }
}