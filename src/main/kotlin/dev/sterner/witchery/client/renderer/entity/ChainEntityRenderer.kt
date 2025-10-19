package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.ChainModel
import dev.sterner.witchery.content.entity.ChainEntity
import dev.sterner.witchery.features.chain.ChainType
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
import kotlin.math.*

class ChainEntityRenderer(context: EntityRendererProvider.Context) : EntityRenderer<ChainEntity>(context) {

    private val chainModel = ChainModel(context.bakeLayer(ChainModel.LAYER_LOCATION))

    private val swayAmplitude = 5.0f
    private val swaySpeed = 0.05f

    private val chainLinkLength = 0.35f * 1.5
    private val chainOverlap = 0.15f * 1.5
    private val linkLength = chainLinkLength - chainOverlap

    private val particleFrequency = 0.3f  // How often particles spawn (0-1)

    override fun getTextureLocation(entity: ChainEntity): ResourceLocation {
        return Witchery.id("textures/entity/chain.png")
    }

    private fun lerpVec3(t: Float, start: Vec3, end: Vec3): Vec3 {
        val x = start.x + (end.x - start.x) * t
        val y = start.y + (end.y - start.y) * t
        val z = start.z + (end.z - start.z) * t
        return Vec3(x, y, z)
    }

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
            val startPos = entity.position()
            val targetPos = targetEntity.position().add(0.0, targetEntity.bbHeight / 2.0, 0.0)

            val directionVec = targetPos.subtract(startPos)
            val distance = directionVec.length()
            val normalizedDir = directionVec.normalize()

            val chainState = entity.getChainState()
            val retractProgress = entity.getRetractProgress()
            val headPosition = entity.getHeadPosition()

            val rawLinkCount = entity.getRawLinkCount()
            val visibleLinks = floor(rawLinkCount).toInt()

            if (rawLinkCount > 0) {
                val yaw = atan2(normalizedDir.x, normalizedDir.z) * (180f / Math.PI)
                val pitch = atan2(
                    normalizedDir.y,
                    sqrt(normalizedDir.x * normalizedDir.x + normalizedDir.z * normalizedDir.z)
                ) * (180f / Math.PI)

                poseStack.translate(0.0, -0.85, 0.0)

                val worldTime = entity.level().gameTime

                when (chainState) {
                    ChainEntity.ChainState.EXTENDING -> {
                        val headDist = distance * headPosition

                        val currentDirection = targetPos.subtract(startPos)
                        val currentDistance = currentDirection.length()
                        val currentNormalizedDir =
                            if (currentDistance > 0) currentDirection.normalize() else normalizedDir

                        val dynamicHeadPos =
                            startPos.add(currentNormalizedDir.scale(headDist.coerceAtMost(currentDistance)))

                        val minLinks = 2
                        val currentVisibleLinks = max(
                            minLinks,
                            min(
                                ceil(headDist / linkLength).toInt(),
                                visibleLinks
                            )
                        )

                        val linkPositions = ArrayList<Vec3>()

                        if (currentVisibleLinks <= minLinks && headDist < linkLength) {
                            linkPositions.add(startPos)

                            if (currentVisibleLinks > 1) {
                                linkPositions.add(dynamicHeadPos)
                            }
                        } else {
                            for (i in 0 until currentVisibleLinks) {
                                val progress = i.toFloat() / (currentVisibleLinks - 1).coerceAtLeast(1).toFloat()
                                val cappedProgress = min(progress, headPosition)

                                val linkPos = lerpVec3(
                                    cappedProgress,
                                    startPos,
                                    dynamicHeadPos
                                )
                                linkPositions.add(linkPos)
                            }
                        }

                        for (i in linkPositions.indices.reversed()) {
                            renderSingleLink(
                                entity,
                                linkPositions[i],
                                i,
                                worldTime,
                                partialTick,
                                yaw.toFloat(),
                                pitch.toFloat(),
                                i.toFloat() / currentVisibleLinks.toFloat(),
                                poseStack,
                                bufferSource,
                                packedLight,
                                1.5f
                            )

                            if (i == 0 || entity.level().random.nextFloat() < particleFrequency) {
                                spawnChainParticles(entity, linkPositions[i])
                            }
                        }
                    }

                    ChainEntity.ChainState.CONNECTED -> {
                        val properLinkCount = max(2, visibleLinks)

                        val linkPositions = ArrayList<Vec3>()

                        for (i in 0 until properLinkCount) {
                            val linkProgress = i.toFloat() / (properLinkCount - 1).coerceAtLeast(1).toFloat()
                            val linkPos = lerpVec3(linkProgress, startPos, targetPos)
                            linkPositions.add(linkPos)
                        }

                        for (i in linkPositions.indices) {
                            renderSingleLink(
                                entity,
                                linkPositions[i],
                                i,
                                worldTime,
                                partialTick,
                                yaw.toFloat(),
                                pitch.toFloat(),
                                i.toFloat() / properLinkCount.toFloat(),
                                poseStack,
                                bufferSource,
                                packedLight,
                                1.0f
                            )

                            if (entity.level().random.nextFloat() < particleFrequency) {
                                spawnChainParticles(entity, linkPositions[i])
                            }
                        }
                    }

                    ChainEntity.ChainState.RETRACTING -> {
                        val retractFactor = 1.0f - retractProgress

                        val currentVisibleLinks = max(2, (visibleLinks * retractFactor).toInt())

                        if (currentVisibleLinks > 0) {
                            val linkPositions = ArrayList<Vec3>()

                            val currentDirection = targetPos.subtract(startPos)
                            val currentDistance = currentDirection.length()
                            val currentNormalizedDir =
                                if (currentDistance > 0) currentDirection.normalize() else normalizedDir

                            val pullBlend = min(retractProgress * 2.0f, 1.0f)

                            val adjustedTargetPos = lerpVec3(
                                pullBlend,
                                targetPos,
                                startPos.add(currentNormalizedDir.scale(currentDistance * 0.3f))
                            )

                            for (i in 0 until currentVisibleLinks) {
                                val linkProgress = i.toFloat() / (currentVisibleLinks - 1).coerceAtLeast(1).toFloat()

                                val linkPos = lerpVec3(
                                    linkProgress,
                                    startPos,
                                    adjustedTargetPos
                                )

                                linkPositions.add(linkPos)
                            }

                            for (i in linkPositions.indices) {
                                renderSingleLink(
                                    entity,
                                    linkPositions[i],
                                    i,
                                    worldTime,
                                    partialTick,
                                    yaw.toFloat(),
                                    pitch.toFloat(),
                                    i.toFloat() / currentVisibleLinks.toFloat(),
                                    poseStack,
                                    bufferSource,
                                    packedLight,
                                    1.8f
                                )

                                if (i == linkPositions.lastIndex || entity.level().random.nextFloat() < particleFrequency * 1.5f) {
                                    spawnChainParticles(entity, linkPositions[i])
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }

        poseStack.popPose()

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
    }

    private fun renderSingleLink(
        entity: ChainEntity,
        linkPos: Vec3,
        linkIndex: Int,
        worldTime: Long,
        partialTick: Float,
        yaw: Float,
        pitch: Float,
        linkProgress: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        swayMultiplier: Float
    ) {
        poseStack.pushPose()

        poseStack.translate(
            linkPos.x - entity.x,
            linkPos.y - entity.y,
            linkPos.z - entity.z
        )

        poseStack.translate(0.0, 1.5, 0.0)
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw - 90f))
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch))
        poseStack.translate(0.0, -1.5, 0.0)
        poseStack.translate(0.25, 0.0, 0.0)

        val animationOffset = (linkIndex * 0.5f) + (worldTime + partialTick) * swaySpeed
        val swayAngle = sin(animationOffset) * swayAmplitude * linkProgress * swayMultiplier

        if (linkIndex % 2 == 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees(swayAngle))
        } else {
            poseStack.mulPose(Axis.ZP.rotationDegrees(swayAngle))
        }

        val baseScale = 0.75f
        poseStack.scale(baseScale, baseScale, baseScale)

        if (linkIndex % 2 == 1) {
            poseStack.translate(-2.0f, 21 / 16f, 0.0f)
            poseStack.mulPose(Axis.XP.rotationDegrees(90f))
            poseStack.translate(2.0f, -21 / 16f, 0.0f)
        }

        chainModel.chain.render(
            poseStack,
            bufferSource.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(entity))),
            packedLight,
            OverlayTexture.NO_OVERLAY,
            -1
        )

        val ov = if (entity.entityData.get(ChainEntity.TYPE) == ChainType.SPIRIT.index) {
            WitcheryRenderTypes.SPIRIT_CHAIN.apply(getTextureLocation(entity))
        } else WitcheryRenderTypes.SOUL_CHAIN.apply(getTextureLocation(entity))
        chainModel.overlay.render(
            poseStack,
            bufferSource.getBuffer(ov),
            packedLight,
            OverlayTexture.NO_OVERLAY,
            -1
        )

        poseStack.popPose()
    }

    /**
     * Spawns particles around a position based on chain state
     */
    private fun spawnChainParticles(entity: ChainEntity, pos: Vec3, count: Int = 1) {
        val level = entity.level()
        val random = level.random

        for (i in 0 until count) {
            val offsetX = (random.nextFloat() - 0.5f) * 0.5f
            val offsetY = (random.nextFloat() - 0.5f) * 0.5f
            val offsetZ = (random.nextFloat() - 0.5f) * 0.5f

            val chainState = entity.getChainState()

            val particlePos = Vec3(
                pos.x + offsetX,
                pos.y + offsetY,
                pos.z + offsetZ
            )

            if (random.nextFloat() < 0.1f) {
                level.addParticle(
                    ParticleTypes.SMOKE,
                    particlePos.x, particlePos.y, particlePos.z,
                    0.0, 0.0, 0.0
                )

                when (chainState) {
                    ChainEntity.ChainState.EXTENDING -> {
                        level.addParticle(
                            ParticleTypes.WITCH,
                            particlePos.x, particlePos.y, particlePos.z,
                            0.0, 0.05, 0.0
                        )
                    }

                    ChainEntity.ChainState.RETRACTING -> {
                        level.addParticle(
                            ParticleTypes.WITCH,
                            particlePos.x, particlePos.y, particlePos.z,
                            offsetX * 0.1, 0.05, offsetZ * 0.1
                        )
                    }

                    else -> {
                    }
                }
            }
        }
    }

    override fun shouldRender(
        livingEntity: ChainEntity,
        camera: Frustum,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val aABB = livingEntity.boundingBoxForCulling.inflate(1.5).expandTowards(livingEntity.position())

        if (camera.isVisible(aABB)) {
            return true
        }

        return super.shouldRender(livingEntity, camera, camX, camY, camZ)
    }
}