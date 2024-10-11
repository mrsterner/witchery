package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.MandrakeEntityModel
import dev.sterner.witchery.entity.MandrakeEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class MandrakeEntityRenderer(context: EntityRendererProvider.Context
) : LivingEntityRenderer<MandrakeEntity, MandrakeEntityModel<MandrakeEntity>>
        (context, MandrakeEntityModel(context.bakeLayer(MandrakeEntityModel.LAYER_LOCATION)), 0.1f) {

    override fun renderNameTag(
        entity: MandrakeEntity,
        displayName: Component,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        partialTick: Float
    ) {
        //No-Op
    }

    override fun getTextureLocation(entity: MandrakeEntity): ResourceLocation {
        return Witchery.id("textures/entity/mandrake.png")
    }
}