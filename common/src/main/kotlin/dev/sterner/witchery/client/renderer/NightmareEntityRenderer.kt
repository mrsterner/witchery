package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.BansheeEntityModel
import dev.sterner.witchery.client.model.NightmareEntityModel
import dev.sterner.witchery.client.model.VampireEntityModel
import dev.sterner.witchery.client.model.WerewolfEntityModel
import dev.sterner.witchery.entity.BansheeEntity
import dev.sterner.witchery.entity.NightmareEntity
import dev.sterner.witchery.entity.VampireEntity
import dev.sterner.witchery.entity.WerewolfEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class NightmareEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<NightmareEntity, NightmareEntityModel>(
        context,
        NightmareEntityModel(context.bakeLayer(NightmareEntityModel.LAYER_LOCATION)),
        0.3f
    ) {

    override fun render(
        entity: NightmareEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        var alpha =   255

        if (entity.entityData.get(NightmareEntity.INTANGIBLE)) {
            alpha = 100
        }

        val color = (alpha shl 24) or (255 shl 16) or (255 shl 8) or 255

        val vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)))
        poseStack.pushPose()
        val f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot)
        val ix = entity.scale
        poseStack.scale(ix, ix, ix)
        poseStack.scale(1.5f, 1.5f, 1.5f)
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

    override fun getTextureLocation(entity: NightmareEntity): ResourceLocation {
        return Witchery.id("textures/entity/nightmare.png")
    }
}