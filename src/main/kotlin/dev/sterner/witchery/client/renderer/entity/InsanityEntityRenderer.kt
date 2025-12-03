package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.client.layer.InsanityEndermanEyesLayer
import dev.sterner.witchery.content.entity.InsanityEntity
import net.minecraft.client.model.CreeperModel
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class InsanityEntityRenderer(ctx: EntityRendererProvider.Context) :
    MobRenderer<InsanityEntity, EntityModel<InsanityEntity>>(ctx, DummyModel(), 0.5f) {

    private val creeperModel = CreeperModel<InsanityEntity>(ctx.bakeLayer(ModelLayers.CREEPER))
    private val zombieModel = createZombieModel(ctx.bakeLayer(ModelLayers.ZOMBIE))
    private val skeletonModel = HumanoidModel<InsanityEntity>(ctx.bakeLayer(ModelLayers.SKELETON))
    private val endermanModel = createEndermanModel(ctx.bakeLayer(ModelLayers.ENDERMAN))


    private val creeperTexture = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper.png")
    private val zombieTexture = ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png")
    private val skeletonTexture = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png")
    private val endermanTexture = ResourceLocation.withDefaultNamespace("textures/entity/enderman/enderman.png")
    private val endermanEyesTexture = ResourceLocation.withDefaultNamespace("textures/entity/enderman/enderman_eyes.png")

    init {
        addLayer(InsanityEndermanEyesLayer(this))
    }

    private class DummyModel : EntityModel<InsanityEntity>() {
        override fun setupAnim(
            entity: InsanityEntity,
            limbSwing: Float,
            limbSwingAmount: Float,
            ageInTicks: Float,
            netHeadYaw: Float,
            headPitch: Float
        ) {
        }

        override fun renderToBuffer(
            poseStack: PoseStack,
            buffer: VertexConsumer,
            packedLight: Int,
            packedOverlay: Int,
            color: Int
        ) {
        }
    }

    private fun createZombieModel(modelPart: ModelPart): HumanoidModel<InsanityEntity> {
        return object : HumanoidModel<InsanityEntity>(modelPart) {
            override fun setupAnim(
                entity: InsanityEntity,
                limbSwing: Float,
                limbSwingAmount: Float,
                ageInTicks: Float,
                netHeadYaw: Float,
                headPitch: Float
            ) {
                super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)

                rightArm.xRot = -1.5f
                rightArm.yRot = 0.0f
                rightArm.zRot = 0.0f

                leftArm.xRot = -1.5f
                leftArm.yRot = 0.0f
                leftArm.zRot = 0.0f

                rightArm.xRot += Mth.cos(limbSwing * 0.6662f) * 0.25f * limbSwingAmount
                leftArm.xRot += Mth.cos(limbSwing * 0.6662f) * 0.25f * limbSwingAmount

                head.zRot = Mth.cos(limbSwing * 0.3f) * 0.15f * limbSwingAmount
            }
        }
    }

    private fun createEndermanModel(modelPart: ModelPart): EntityModel<InsanityEntity> {
        return object : EntityModel<InsanityEntity>() {
            private val root = modelPart
            private val head = modelPart.getChild("head")
            private val body = modelPart.getChild("body")
            private val rightArm = modelPart.getChild("right_arm")
            private val leftArm = modelPart.getChild("left_arm")
            private val rightLeg = modelPart.getChild("right_leg")
            private val leftLeg = modelPart.getChild("left_leg")

            override fun setupAnim(
                entity: InsanityEntity, limbSwing: Float, limbSwingAmount: Float,
                ageInTicks: Float, netHeadYaw: Float, headPitch: Float
            ) {
                head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
                head.xRot = headPitch * (Math.PI.toFloat() / 180f)

                rightArm.xRot = Mth.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 2.0f * limbSwingAmount * 0.5f
                leftArm.xRot = Mth.cos(limbSwing * 0.6662f) * 2.0f * limbSwingAmount * 0.5f
                rightLeg.xRot = Mth.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount
                leftLeg.xRot = Mth.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount


                this.head.visible = true
                this.body.xRot = 0.0F
                this.body.y = -14.0F
                this.body.z = -0.0F
                this.rightLeg.xRot -= 0.0F
                this.leftLeg.xRot -= 0.0F
                this.rightArm.xRot *= 0.5F
                this.leftArm.xRot *= 0.5F
                this.rightLeg.xRot *= 0.5F
                this.leftLeg.xRot *= 0.5F

                if (this.rightArm.xRot > 0.4F) {
                    this.rightArm.xRot = 0.4F
                }

                if (this.leftArm.xRot > 0.4F) {
                    this.leftArm.xRot = 0.4F
                }

                if (this.rightArm.xRot < -0.4F) {
                    this.rightArm.xRot = -0.4F
                }

                if (this.leftArm.xRot < -0.4F) {
                    this.leftArm.xRot = -0.4F
                }

                if (this.rightLeg.xRot > 0.4F) {
                    this.rightLeg.xRot = 0.4F
                }

                if (this.leftLeg.xRot > 0.4F) {
                    this.leftLeg.xRot = 0.4F
                }

                if (this.rightLeg.xRot < -0.4F) {
                    this.rightLeg.xRot = -0.4F
                }

                if (this.leftLeg.xRot < -0.4F) {
                    this.leftLeg.xRot = -0.4F
                }

                this.rightLeg.z = 0.0F
                this.leftLeg.z = 0.0F
                this.rightLeg.y = -5.0F
                this.leftLeg.y = -5.0F
                this.head.z = -0.0F
                this.head.y = -13.0F

                this.head.y -= 5.0F

                this.rightArm.setPos(-5.0F, -12.0F, 0.0F)
                this.leftArm.setPos(5.0F, -12.0F, 0.0F)
            }

            override fun renderToBuffer(
                poseStack: PoseStack, buffer: VertexConsumer, packedLight: Int,
                packedOverlay: Int, color: Int
            ) {
                root.render(poseStack, buffer, packedLight, packedOverlay, color)
            }
        }
    }

    override fun getTextureLocation(entity: InsanityEntity): ResourceLocation {
        return when (entity.entityData.get(InsanityEntity.DATA_MIMIC)) {
            "creeper" -> creeperTexture
            "zombie" -> zombieTexture
            "skeleton" -> skeletonTexture
            else -> endermanTexture
        }
    }

    override fun render(
        entity: InsanityEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        val insanityModel = when (entity.entityData.get(InsanityEntity.DATA_MIMIC)) {
            "creeper" -> creeperModel
            "zombie" -> zombieModel
            "skeleton" -> skeletonModel
            else -> endermanModel
        }

        insanityModel.let { currentModel ->
            model = currentModel
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)

            if (entity.entityData.get(InsanityEntity.DATA_MIMIC) == "enderman") {
                val vertexConsumer = buffer.getBuffer(RenderType.eyes(endermanEyesTexture))
                currentModel.renderToBuffer(poseStack, vertexConsumer, 15728640, OverlayTexture.NO_OVERLAY)

                if (entity.isAggressive && entity.level().isClientSide) {
                    if (entity.level().random.nextFloat() < 0.3f) {
                        entity.level().addParticle(
                            ParticleTypes.PORTAL,
                            entity.x + (entity.level().random.nextDouble() - 0.5) * entity.bbWidth,
                            entity.y + entity.level().random.nextDouble() * entity.bbHeight,
                            entity.z + (entity.level().random.nextDouble() - 0.5) * entity.bbWidth,
                            (entity.level().random.nextDouble() - 0.5) * 2.0,
                            -entity.level().random.nextDouble(),
                            (entity.level().random.nextDouble() - 0.5) * 2.0
                        )
                    }
                }
            }
        }
    }

    override fun scale(livingEntity: InsanityEntity, poseStack: PoseStack, partialTickTime: Float) {
        var f = livingEntity.getSwelling(partialTickTime)
        val g = 1.0f + Mth.sin(f * 100.0f) * f * 0.01f
        f = Mth.clamp(f, 0.0f, 1.0f)
        f *= f
        f *= f
        val h = (1.0f + f * 0.4f) * g
        val i = (1.0f + f * 0.1f) / g
        poseStack.scale(h, i, h)
    }

    override fun getWhiteOverlayProgress(livingEntity: InsanityEntity, partialTicks: Float): Float {
        val f = livingEntity.getSwelling(partialTicks)
        return if ((f * 10.0f).toInt() % 2 == 0) 0.0f else Mth.clamp(f, 0.5f, 1.0f)
    }

    override fun getRenderType(
        entity: InsanityEntity,
        visibility: Boolean,
        transparentVisibility: Boolean,
        glowing: Boolean
    ): RenderType {
        return RenderType.entityTranslucent(getTextureLocation(entity))
    }

    override fun getShadowRadius(entity: InsanityEntity): Float {
        return when (entity.entityData.get(InsanityEntity.DATA_MIMIC)) {
            "enderman" -> 0.7f
            "creeper" -> 0.5f
            else -> 0.5f
        }
    }
}