package dev.sterner.witchery.client.renderer

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.SpectreEntityModel
import dev.sterner.witchery.entity.SpectreEntity
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation

class SpectreEntityRenderer(context: EntityRendererProvider.Context) :
    AbstractGhostEntityRenderer<SpectreEntity, SpectreEntityModel>(
        context,
        SpectreEntityModel(context.bakeLayer(SpectreEntityModel.LAYER_LOCATION)),
        0.6f
    ) {

    override fun getTextureLocation(entity: SpectreEntity): ResourceLocation {
        return Witchery.id("textures/entity/spectre.png")
    }
}