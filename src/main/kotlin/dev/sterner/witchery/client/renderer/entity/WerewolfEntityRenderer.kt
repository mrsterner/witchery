package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.WerewolfEntityModel
import dev.sterner.witchery.content.entity.WerewolfEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class WerewolfEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<WerewolfEntity, WerewolfEntityModel>(
        context,
        WerewolfEntityModel(context.bakeLayer(WerewolfEntityModel.LAYER_LOCATION)),
        0.6f
    ) {

    override fun render(
        entity: WerewolfEntity,
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

    override fun getTextureLocation(entity: WerewolfEntity): ResourceLocation {
        return Witchery.id("textures/entity/werewolf.png")
    }
}