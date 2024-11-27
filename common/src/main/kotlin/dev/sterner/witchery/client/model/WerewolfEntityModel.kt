package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.WerewolfEntity
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import kotlin.math.cos
import kotlin.math.sin


class WerewolfEntityModel(root: ModelPart) : EntityModel<WerewolfEntity>() {

    private val body: ModelPart = root.getChild("body")
    private val hip: ModelPart = body.getChild("hip")
    private val Tail: ModelPart = hip.getChild("Tail")
    private val left_leg: ModelPart = root.getChild("left_leg")
    private val right_leg: ModelPart = root.getChild("right_leg")
    private val head: ModelPart = root.getChild("head")
    private val left_arm: ModelPart = root.getChild("left_arm")
    private val right_arm: ModelPart = root.getChild("right_arm")

    override fun setupAnim(
        entity: WerewolfEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        right_arm.xRot = sin(ageInTicks * 0.1f) * 0.1f
        right_arm.zRot = cos(ageInTicks * 0.1f) * 0.05f

        left_arm.xRot = cos(ageInTicks * 0.1f) * 0.1f
        left_arm.zRot = sin(ageInTicks * 0.1f) * 0.05f

        left_leg.xRot = cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount
        right_leg.xRot = cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount

        left_arm.xRot =+ cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.2f * limbSwingAmount
        right_arm.xRot =+ cos(limbSwing * 0.6662f) * 1.2f * limbSwingAmount

        head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        head.xRot = headPitch * (Math.PI.toFloat() / 180f)

        head.yRot += cos(ageInTicks * 0.05f) * 0.02f
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("werewolf"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 19)
                    .addBox(-5.0f, -3.0f, 0.0f, 10.0f, 10.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -1.0f, 0.0f, 0.5672f, 0.0f, 0.0f)
            )

            val hip = body.addOrReplaceChild(
                "hip",
                CubeListBuilder.create().texOffs(0, 39)
                    .addBox(-4.0f, -5.0f, 0.0f, 8.0f, 12.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 7.0f, 0.0f, -0.5672f, 0.0f, 0.0f)
            )

            val Tail = hip.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0f, 5.0f, 7.0f))

            val Tail_r1 = Tail.addOrReplaceChild(
                "Tail_r1",
                CubeListBuilder.create().texOffs(38, 14)
                    .addBox(-1.5f, -3.0f, -1.5f, 3.0f, 12.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.1745f, 0.0f, 0.0f)
            )

            val left_leg = partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(3.0f, 11.0f, 6.0f, 0.0873f, 0.0f, 0.0f)
            )

            val Leg_r1 = left_leg.addOrReplaceChild(
                "Leg_r1",
                CubeListBuilder.create().texOffs(44, 44).mirror()
                    .addBox(-2.0f, 4.0f, 2.0f, 4.0f, 10.0f, 4.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(44, 29).mirror().addBox(-2.0f, -2.0f, -3.0f, 4.0f, 10.0f, 5.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.5236f, 0.0f, 0.0f)
            )

            val right_leg = partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(-3.0f, 11.0f, 6.0f, 0.0873f, 0.0f, 0.0f)
            )

            val Leg_r2 = right_leg.addOrReplaceChild(
                "Leg_r2",
                CubeListBuilder.create().texOffs(44, 44)
                    .addBox(-2.0f, 4.0f, 2.0f, 4.0f, 10.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(44, 29).addBox(-2.0f, -2.0f, -3.0f, 4.0f, 10.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.5236f, 0.0f, 0.0f)
            )

            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.5f, -6.0f, -10.0f, 7.0f, 7.0f, 12.0f, CubeDeformation(0.0f))
                    .texOffs(26, 0).addBox(-1.5f, -4.0f, -15.0f, 3.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -3.0f, 3.0f)
            )

            val RightEar_r1 = head.addOrReplaceChild(
                "RightEar_r1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-6.0f, -4.0f, -7.0f, 1.0f, 7.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.6981f, -0.3927f, 0.0f)
            )

            val LeftEar_r1 = head.addOrReplaceChild(
                "LeftEar_r1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(5.0f, -4.0f, -7.0f, 1.0f, 7.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.6981f, 0.3927f, 0.0f)
            )

            val RightTuff_r1 = head.addOrReplaceChild(
                "RightTuff_r1",
                CubeListBuilder.create().texOffs(0, 19)
                    .addBox(1.0f, -4.0f, -7.9f, 3.0f, 6.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.8727f, 0.0f)
            )

            val LeftTuff_r1 = head.addOrReplaceChild(
                "LeftTuff_r1",
                CubeListBuilder.create().texOffs(0, 19).mirror()
                    .addBox(-4.0f, -4.0f, -7.9f, 3.0f, 6.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, -0.8727f, 0.0f)
            )

            val left_arm = partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(30, 39).mirror()
                    .addBox(0.0f, -2.0f, -2.0f, 3.0f, 15.0f, 4.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(24, 39).mirror().addBox(1.0f, 13.0f, -2.0f, 2.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(24, 39).mirror().addBox(1.0f, 13.0f, 0.0f, 2.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(24, 39).mirror().addBox(1.0f, 13.0f, 2.0f, 2.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offset(5.0f, -1.0f, 3.0f)
            )

            val right_arm = partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(30, 39)
                    .addBox(-3.0f, -2.0f, -2.0f, 3.0f, 15.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(24, 39).addBox(-3.0f, 13.0f, -2.0f, 2.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(24, 39).addBox(-3.0f, 13.0f, 0.0f, 2.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(24, 39).addBox(-3.0f, 13.0f, 2.0f, 2.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, -1.0f, 3.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}