package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.VampireEntityModel
import dev.sterner.witchery.entity.VampireEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class VampireEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<VampireEntity, VampireEntityModel>(
        context,
        VampireEntityModel(context.bakeLayer(VampireEntityModel.LAYER_LOCATION)),
        0.6f
    ) {

    override fun render(
        entity: VampireEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        model.root.getChild("arms").visible = false
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
        poseStack.popPose()
    }

    override fun getTextureLocation(entity: VampireEntity): ResourceLocation {
        return Witchery.id("textures/entity/vampire.png")
    }
}