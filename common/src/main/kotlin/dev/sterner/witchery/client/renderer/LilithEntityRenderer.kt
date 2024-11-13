package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.DemonEntityModel
import dev.sterner.witchery.client.model.LilithEntityModel
import dev.sterner.witchery.entity.DemonEntity
import dev.sterner.witchery.entity.LilithEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class LilithEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<LilithEntity, LilithEntityModel>(
        context,
        LilithEntityModel(context.bakeLayer(LilithEntityModel.LAYER_LOCATION)),
        0.8f
    ) {

    override fun render(
        entity: LilithEntity,
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

    override fun getTextureLocation(entity: LilithEntity): ResourceLocation {
        return Witchery.id("textures/entity/lilith.png")
    }
}