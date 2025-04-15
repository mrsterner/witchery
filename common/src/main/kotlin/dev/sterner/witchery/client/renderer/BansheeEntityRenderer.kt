package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.BansheeEntityModel
import dev.sterner.witchery.entity.BansheeEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class BansheeEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<BansheeEntity, BansheeEntityModel>(
        context,
        BansheeEntityModel(context.bakeLayer(BansheeEntityModel.LAYER_LOCATION)),
        0.6f
    ) {

    override fun render(
        entity: BansheeEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        val timeOfDay = entity.level().dayTime % 24000
        var alpha = if (timeOfDay in 0..12000) {
            10
        } else {
            100
        }

        if (entity.entityData.get(BansheeEntity.REVEALED)) {
            alpha = 255
        }

        val color = (alpha shl 24) or (255 shl 16) or (255 shl 8) or 255

        val vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)))
        poseStack.pushPose()
        val f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot)
        val ix = entity.scale
        poseStack.scale(ix, ix, ix)
        val k = this.getBob(entity, partialTicks)
        this.setupRotations(entity, poseStack, k, f, partialTicks, ix)

        poseStack.scale(-1.0f, -1.0f, 1.0f)
        this.scale(entity, poseStack, partialTicks)
        poseStack.translate(0.0f, -1.501f, 0.0f)
        var l = 0.0f
        var m = 0.0f

        if (!entity.isPassenger && entity.isAlive) {
            l = entity.walkAnimation.speed(partialTicks)
            m = entity.walkAnimation.position(partialTicks)
            if (entity.isBaby) {
                m *= 3.0f
            }

            if (l > 1.0f) {
                l = 1.0f
            }
        }
        val j = Mth.lerp(partialTicks, entity.xRotO, entity.xRot)
        val g = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot)
        val h: Float = g - f
        model.prepareMobModel(entity, m, l, partialTicks)
        model.setupAnim(entity, m, l, k, h, j)
        val minecraft = Minecraft.getInstance()
        val bl = this.isBodyVisible(entity)
        val bl2 = !bl && !entity.isInvisibleTo(minecraft.player)
        val bl3 = minecraft.shouldEntityAppearGlowing(entity)
        val renderType = this.getRenderType(entity, bl, bl2, bl3)
        if (renderType != null) {
            val n = getOverlayCoords(
                entity,
                this.getWhiteOverlayProgress(entity, partialTicks)
            )
            model.renderToBuffer(poseStack, vertexConsumer, packedLight, n, color)
        }

        poseStack.popPose()
    }


    override fun getTextureLocation(entity: BansheeEntity): ResourceLocation {
        return Witchery.id("textures/entity/banshee.png")
    }
}