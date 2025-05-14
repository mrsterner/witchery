package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.entity.SpectralPigEntity
import net.minecraft.client.model.PigModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class SpectralPigRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<SpectralPigEntity, PigModel<SpectralPigEntity>>(
        context,
        PigModel(context.bakeLayer(ModelLayers.PIG)), 0.0f
    ) {

    override fun render(
        entity: SpectralPigEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        model.attackTime = this.getAttackAnim(entity, partialTicks)
        val f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot)
        val g = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot)
        var h = g - f

        var j = Mth.lerp(partialTicks, entity.xRotO, entity.xRot)
        if (isEntityUpsideDown(entity)) {
            j *= -1.0f
            h *= -1.0f
        }

        h = Mth.wrapDegrees(h)

        val ix: Float = entity.getScale()
        poseStack.scale(ix, ix, ix)
        val k = this.getBob(entity, partialTicks)
        this.setupRotations(entity, poseStack, k, f, partialTicks, ix)
        poseStack.scale(-1.0f, -1.0f, 1.0f)
        this.scale(entity, poseStack, partialTicks)
        poseStack.translate(0.0f, -1.501f, 0.0f)
        var l = 0.0f
        var m = 0.0f
        if (entity.isAlive) {
            l = entity.walkAnimation.speed(partialTicks)
            m = entity.walkAnimation.position(partialTicks)

            if (l > 1.0f) {
                l = 1.0f
            }
        }

        model.prepareMobModel(entity, m, l, partialTicks)
        model.setupAnim(entity, m, l, k, h, j)

        var alpha = 20

        if (entity.entityData.get(SpectralPigEntity.REVEALED)) {
            alpha = 160
        }

        val color = (alpha shl 24) or (170 shl 16) or (255 shl 8) or 255

        val resourceLocation = this.getTextureLocation(entity)

        val vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(resourceLocation))
        val n = getOverlayCoords(
            entity,
            this.getWhiteOverlayProgress(entity, partialTicks)
        )

        model.renderToBuffer(poseStack, vertexConsumer, packedLight, n, color)

        for (renderLayer in this.layers) {
            renderLayer.render(poseStack, buffer, packedLight, entity, m, l, partialTicks, k, h, j)
        }

        poseStack.popPose()
    }

    override fun getRenderType(
        livingEntity: SpectralPigEntity,
        bodyVisible: Boolean,
        translucent: Boolean,
        glowing: Boolean
    ): RenderType? {
        val resourceLocation = this.getTextureLocation(livingEntity)
        return RenderType.entityTranslucent(resourceLocation)
    }

    override fun getTextureLocation(entity: SpectralPigEntity): ResourceLocation {
        return ResourceLocation.withDefaultNamespace("textures/entity/pig/pig.png")
    }
}