package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.EntEntityModel
import dev.sterner.witchery.entity.EntEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import kotlin.math.abs

class EntEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<EntEntity, EntEntityModel>(
        context,
        EntEntityModel(context.bakeLayer(EntEntityModel.LAYER_LOCATION)),
        0.8f
    ) {

    private val ROWAN = Witchery.id("textures/entity/rowan_ent.png")
    private val ALDER = Witchery.id("textures/entity/alder_ent.png")
    private val HAWTHORN = Witchery.id("textures/entity/hawthorn_ent.png")

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

    override fun setupRotations(
        entity: EntEntity,
        poseStack: PoseStack,
        bob: Float,
        yBodyRot: Float,
        partialTick: Float,
        scale: Float
    ) {
        super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale)
        if (!(entity.walkAnimation.speed().toDouble() < 0.01)) {
            val g = entity.walkAnimation.position(partialTick) + 6.0f
            val h = ((abs((g % 13.0f - 6.5f).toDouble()) - 3.25f) / 3.25f).toFloat()
            poseStack.mulPose(Axis.ZP.rotationDegrees(6.5f * h))
        }
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