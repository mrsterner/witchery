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
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.sin
import kotlin.math.sqrt

class ChainEntityRenderer(context: EntityRendererProvider.Context) : EntityRenderer<ChainEntity>(context) {

    private val chainModel = ChainModel(context.bakeLayer(ChainModel.LAYER_LOCATION))

    private val chainLinkLength = 0.35f * 1.5
    private val chainOverlap = 0.15f * 1.5
    private val effectiveLinkLength = chainLinkLength - chainOverlap

    private val swayAmplitude = 5.0f
    private val swaySpeed = 0.05f

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

            val numLinks = max(ceil(distance / effectiveLinkLength).toInt())

            val normalizedDir = directionVec.normalize()

            val yaw = atan2(normalizedDir.x, normalizedDir.z) * (180f / Math.PI)
            val pitch = atan2(normalizedDir.y, sqrt(normalizedDir.x * normalizedDir.x + normalizedDir.z * normalizedDir.z)) * (180f / Math.PI)

            poseStack.translate(-0.0, -0.75, -0.0)

            val worldTime = entity.level().gameTime

            for (i in 1 until numLinks) {
                poseStack.pushPose()

                val linkPos = startPos.add(normalizedDir.scale(i * effectiveLinkLength))
                poseStack.translate(
                    linkPos.x - entity.x,
                    linkPos.y - entity.y,
                    linkPos.z - entity.z
                )

                poseStack.mulPose(Axis.YP.rotationDegrees(yaw.toFloat() - 90f))
                poseStack.mulPose(Axis.ZP.rotationDegrees(pitch.toFloat()))

                val animationOffset = (i * 0.5f) + (worldTime + partialTick) * swaySpeed
                val swayAngle = sin(animationOffset) * swayAmplitude * (i.toFloat() / numLinks)

                if (i % 2 == 0) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(swayAngle))
                } else {
                    poseStack.mulPose(Axis.ZP.rotationDegrees(swayAngle))
                }

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

                spawnChainParticles(entity, linkPos, 1)

                poseStack.popPose()
            }
        }

        poseStack.popPose()

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
    }

    /**
     * Spawn particles around the chain links
     */
    private fun spawnChainParticles(entity: ChainEntity, linkPos: Vec3, count: Int) {
        val level = entity.level()


        if (entity.random.nextFloat() > 0.1f) return

        for (i in 0 until count) {
            val offsetX: Float = (entity.random.nextFloat() - 0.5f) * 0.2f
            val offsetY: Float = (entity.random.nextFloat() - 0.5f) * 0.2f
            val offsetZ: Float = (entity.random.nextFloat() - 0.5f) * 0.2f

            val velX: Double = (entity.random.nextDouble() - 0.5f) * 0.02f
            val velY: Double = entity.random.nextDouble() * 0.02f
            val velZ: Double = (entity.random.nextDouble() - 0.5f) * 0.02f

            if (entity.random.nextDouble() < 0.2) {
                level.addParticle(
                    ParticleTypes.SMOKE,
                    linkPos.x + offsetX,
                    linkPos.y + offsetY + 0.25,
                    linkPos.z + offsetZ,
                    velX, velY, velZ
                )

                level.addParticle(
                    ParticleTypes.DRAGON_BREATH,
                    linkPos.x + offsetX,
                    linkPos.y + offsetY + 0.25,
                    linkPos.z + offsetZ,
                    velX, velY, velZ
                )
            }
        }
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