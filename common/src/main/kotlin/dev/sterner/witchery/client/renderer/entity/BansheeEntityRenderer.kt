package dev.sterner.witchery.client.renderer.entity

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.BansheeEntityModel
import dev.sterner.witchery.entity.BansheeEntity
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation

class BansheeEntityRenderer(context: EntityRendererProvider.Context) :
    AbstractGhostEntityRenderer<BansheeEntity, BansheeEntityModel>(
        context,
        BansheeEntityModel(context.bakeLayer(BansheeEntityModel.LAYER_LOCATION)),
        0.6f
    ) {

    override fun getTextureLocation(entity: BansheeEntity): ResourceLocation {
        return Witchery.id("textures/entity/banshee.png")
    }
}