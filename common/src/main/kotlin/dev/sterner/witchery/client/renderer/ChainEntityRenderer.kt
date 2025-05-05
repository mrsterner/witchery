package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.ChainModel
import dev.sterner.witchery.entity.ChainEntity
import dev.sterner.witchery.registry.WitcheryRenderTypes
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.sqrt

class ChainEntityRenderer(context: EntityRendererProvider.Context) : EntityRenderer<ChainEntity>(context) {

    private val chainModel = ChainModel(context.bakeLayer(ChainModel.LAYER_LOCATION))

    private val CHAIN_LINK_LENGTH = 0.35f * 1.5
    private val CHAIN_OVERLAP = 0.15f * 1.5
    private val EFFECTIVE_LINK_LENGTH = CHAIN_LINK_LENGTH - CHAIN_OVERLAP

    override fun render(
        entity: ChainEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()

        val targetEntity = entity.getTargetEntity()
        if (targetEntity != null) {
            val startPos = entity.getLockedPosition() ?: entity.position()
            val targetPos = targetEntity.position().add(0.0, targetEntity.bbHeight / 2.0, 0.0)

            val directionVec = targetPos.subtract(startPos)
            val distance = directionVec.length()

            val numLinks = max(ceil(distance / EFFECTIVE_LINK_LENGTH).toInt())

            val normalizedDir = directionVec.normalize()

            val yaw = atan2(normalizedDir.x, normalizedDir.z) * (180f / Math.PI)
            val pitch = atan2(normalizedDir.y, sqrt(normalizedDir.x * normalizedDir.x + normalizedDir.z * normalizedDir.z)) * (180f / Math.PI)

            poseStack.translate(-0.0, -0.75, -0.0)
            for (i in 1 until numLinks) {
                poseStack.pushPose()

                val linkPos = startPos.add(normalizedDir.scale((i * EFFECTIVE_LINK_LENGTH).toDouble()))
                poseStack.translate(
                    linkPos.x - entity.x,
                    linkPos.y - entity.y,
                    linkPos.z - entity.z
                )

                poseStack.mulPose(Axis.YP.rotationDegrees(yaw.toFloat() - 90f))
                poseStack.mulPose(Axis.ZP.rotationDegrees(pitch.toFloat()))

                poseStack.scale(0.75f, 0.75f, 0.75f)

                if (i % 2 == 1) {
                    poseStack.translate(-2.0f, 21/16f, 0.0f)
                    poseStack.mulPose(Axis.XP.rotationDegrees(90f))
                    poseStack.translate(2.0f, -21/16f, 0.0f)
                }

                chainModel.chain.render(
                    poseStack,
                    bufferSource.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(entity))),
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    -1
                )

                chainModel.overlay.render(
                    poseStack,
                    bufferSource.getBuffer(WitcheryRenderTypes.CHAIN.apply(getTextureLocation(entity))),
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    -1
                )

                poseStack.popPose()
            }
        }

        poseStack.popPose()

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
    }

    override fun getTextureLocation(entity: ChainEntity): ResourceLocation {
        return Witchery.id("textures/entity/chain.png")
    }

    override fun shouldRender(
        livingEntity: ChainEntity,
        camera: Frustum,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val aABB = livingEntity.boundingBoxForCulling.inflate(0.5).expandTowards(livingEntity.position())

        if (camera.isVisible(aABB)) {
            return true
        }

        return super.shouldRender(livingEntity, camera, camX, camY, camZ)
    }

    private fun max(b: Int): Int {
        return if (1 > b) 1 else b
    }
}