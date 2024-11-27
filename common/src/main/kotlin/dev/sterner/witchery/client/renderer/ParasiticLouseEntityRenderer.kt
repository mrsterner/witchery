package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.ParasiticLouseEntityModel
import dev.sterner.witchery.entity.ParasiticLouseEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class ParasiticLouseEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<ParasiticLouseEntity, ParasiticLouseEntityModel>(
        context,
        ParasiticLouseEntityModel(context.bakeLayer(ParasiticLouseEntityModel.LAYER_LOCATION)),
        0.1f
    ) {

    override fun render(
        entity: ParasiticLouseEntity,
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

    override fun getTextureLocation(entity: ParasiticLouseEntity): ResourceLocation {
        return Witchery.id("textures/entity/parasitic_louse.png")
    }
}