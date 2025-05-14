package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.DeathEntityModel
import dev.sterner.witchery.entity.DeathEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class DeathEntityRenderer(var context: EntityRendererProvider.Context) :
    LivingEntityRenderer<DeathEntity, DeathEntityModel>(
        context, DeathEntityModel(context.bakeLayer(DeathEntityModel.LAYER_LOCATION)), 0.5f
    ) {

    init {
        this.addLayer(ItemInHandLayer<DeathEntity, DeathEntityModel>(this, context.itemInHandRenderer))
    }
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

    override fun renderNameTag(
        entity: DeathEntity,
        displayName: Component,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        partialTick: Float
    ) {

    }
}