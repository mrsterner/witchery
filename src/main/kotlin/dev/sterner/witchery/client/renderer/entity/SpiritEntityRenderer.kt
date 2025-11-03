package dev.sterner.witchery.client.renderer.entity

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.SpectreEntityModel
import dev.sterner.witchery.client.model.SpiritEntityModel
import dev.sterner.witchery.content.entity.SpectreEntity
import dev.sterner.witchery.content.entity.SpiritEntity
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation

class SpiritEntityRenderer(context: EntityRendererProvider.Context) :
    AbstractGhostEntityRenderer<SpiritEntity, SpiritEntityModel>(
        context,
        SpiritEntityModel(context.bakeLayer(SpiritEntityModel.LAYER_LOCATION)),
        0.6f
    ) {

    override fun getTextureLocation(entity: SpiritEntity): ResourceLocation {
        return Witchery.id("textures/entity/spectre.png")
    }
}