package dev.sterner.witchery.client.renderer

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.BansheeEntityModel
import dev.sterner.witchery.client.model.EntEntityModel
import dev.sterner.witchery.entity.BansheeEntity
import dev.sterner.witchery.entity.EntEntity
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class BansheeEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<BansheeEntity, BansheeEntityModel>(
        context,
        BansheeEntityModel(context.bakeLayer(BansheeEntityModel.LAYER_LOCATION)),
        0.8f
    ) {
    override fun getTextureLocation(entity: BansheeEntity): ResourceLocation {
        return Witchery.id("textures/entity/banshee.png")
    }
}