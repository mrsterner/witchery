package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.ImpEntityModel
import dev.sterner.witchery.client.model.LeechEntityModel
import dev.sterner.witchery.entity.ImpEntity
import dev.sterner.witchery.entity.LeechEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class LeechEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<LeechEntity, LeechEntityModel>(
        context,
        LeechEntityModel(context.bakeLayer(LeechEntityModel.LAYER_LOCATION)),
        0.1f
    ) {

    override fun render(
        entity: LeechEntity,
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

    override fun getTextureLocation(entity: LeechEntity): ResourceLocation {
        return Witchery.id("textures/entity/parasytic_louse.png")
    }
}