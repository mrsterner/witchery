package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.DemonEntityModel
import dev.sterner.witchery.client.model.EntEntityModel
import dev.sterner.witchery.client.model.ImpEntityModel
import dev.sterner.witchery.entity.DemonEntity
import dev.sterner.witchery.entity.EntEntity
import dev.sterner.witchery.entity.ImpEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class EntEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<EntEntity, EntEntityModel>(
        context,
        EntEntityModel(context.bakeLayer(EntEntityModel.LAYER_LOCATION)),
        0.8f
    ) {

    val ROWAN = Witchery.id("textures/entity/rowan_ent.png")
    val ALDER = Witchery.id("textures/entity/alder_ent.png")
    val HAWTHORN = Witchery.id("textures/entity/hawthorn_ent.png")

    override fun render(
        entity: EntEntity,
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

    override fun getTextureLocation(entity: EntEntity): ResourceLocation {
        val variant = entity.getVariant()
        if (variant == EntEntity.Type.ALDER) {
            return ALDER
        }
        if (variant == EntEntity.Type.HAWTHORN) {
            return HAWTHORN
        }
        return ROWAN
    }
}