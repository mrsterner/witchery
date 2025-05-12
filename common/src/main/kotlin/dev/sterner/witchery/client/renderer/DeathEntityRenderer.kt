package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.DeathEntityModel
import dev.sterner.witchery.client.model.OwlEntityModel
import dev.sterner.witchery.entity.DeathEntity
import dev.sterner.witchery.entity.ElleEntity
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class DeathEntityRenderer(var context: EntityRendererProvider.Context) :
    LivingEntityRenderer<DeathEntity, DeathEntityModel>(
        context, DeathEntityModel(context.bakeLayer(DeathEntityModel.LAYER_LOCATION)), 0.5f
    ) {

    override fun render(
        entity: DeathEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
    }

    override fun getTextureLocation(entity: DeathEntity): ResourceLocation? {
        return Witchery.id("textures/entity/death.png")
    }
}