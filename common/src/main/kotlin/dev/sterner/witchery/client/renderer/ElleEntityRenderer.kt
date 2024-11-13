package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.ElleEntity
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class ElleEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<ElleEntity, PlayerModel<ElleEntity>>(
        context,
        PlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true),
        0.5f
    ) {

    override fun render(
        entity: ElleEntity,
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

    override fun getTextureLocation(entity: ElleEntity): ResourceLocation {
        return Witchery.id("textures/entity/elle.png")
    }
}