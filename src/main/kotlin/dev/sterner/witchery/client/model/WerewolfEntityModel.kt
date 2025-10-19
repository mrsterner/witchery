package dev.sterner.witchery.client.model

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.entity.WerewolfEntity
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import kotlin.math.cos
import kotlin.math.sin


class WerewolfEntityModel(root: ModelPart) : HumanoidModel<WerewolfEntity>(root) {

    var tail: ModelPart = body.getChild("tail")

    override fun setupAnim(
        entity: WerewolfEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        rightArm.xRot = sin(ageInTicks * 0.1f) * 0.1f
        rightArm.zRot = cos(ageInTicks * 0.1f) * 0.05f

        leftArm.xRot = cos(ageInTicks * 0.1f) * 0.1f - Math.toRadians(12.5).toFloat()
        leftArm.zRot = sin(ageInTicks * 0.1f) * 0.05f

        leftLeg.xRot = cos(limbSwing * 0.6662f) * 0.75f * limbSwingAmount - Math.toRadians(17.5).toFloat()
        rightLeg.xRot =
            cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 0.75f * limbSwingAmount - Math.toRadians(17.5).toFloat()

        leftArm.xRot = +cos(limbSwing * 0.3333f + Math.PI.toFloat()) * limbSwingAmount - Math.toRadians(2.5).toFloat()
        rightArm.xRot = +cos(limbSwing * 0.3333f) * limbSwingAmount - Math.toRadians(2.5).toFloat()

        head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        head.xRot = headPitch * (Math.PI.toFloat() / 180f)

        head.yRot += cos(ageInTicks * 0.05f) * 0.02f
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("werewolf"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 39)
                    .addBox(-4.5f, 5.0f, -2.0f, 9.0f, 10.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 23).addBox(-5.0f, -5.0f, -3.0f, 10.0f, 10.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -2.5f, -3.0f, 0.3491f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(64, 23)
                    .addBox(-4.0f, 1.0f, 0.0f, 8.0f, 10.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0873f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(92, 23)
                    .addBox(-4.5f, -3.0f, 0.0f, 9.0f, 10.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.3927f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(56, 39)
                    .addBox(-1.5f, -1.0f, -1.5f, 3.0f, 13.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 15.0f, 2.0f, 0.1745f, 0.0f, 0.0f)
            )

            val right_leg = partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(-2.0f, 9.25f, 2.0f, -0.3054f, 0.0f, 0.0436f)
            )

            right_leg.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(0, 54)
                    .addBox(-3.0f, 2.0f, -2.0f, 4.0f, 8.0f, 5.0f, CubeDeformation(0.1f)),
                PartPose.offsetAndRotation(0.0f, -3.0f, 0.0f, -0.2618f, 0.0f, 0.0f)
            )

            right_leg.addOrReplaceChild(
                "right_leg_2",
                CubeListBuilder.create().texOffs(0, 73)
                    .addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 5.0f, 1.0f)
            )

            val left_leg = partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(2.0f, 9.25f, 2.0f, -0.3054f, 0.0f, -0.0436f)
            )

            left_leg.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(0, 54).mirror()
                    .addBox(-1.0f, 2.0f, -2.0f, 4.0f, 8.0f, 5.0f, CubeDeformation(0.1f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, -3.0f, 0.0f, -0.2618f, 0.0f, 0.0f)
            )

            left_leg.addOrReplaceChild(
                "left_leg_2",
                CubeListBuilder.create().texOffs(0, 73).mirror()
                    .addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(0.0f, 5.0f, 1.0f)
            )

            val right_arm = partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(28, 41)
                    .addBox(-3.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, CubeDeformation(0.01f))
                    .texOffs(42, 39).addBox(-3.0f, 10.0f, -2.0f, 3.0f, 3.0f, 4.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(-4.5f, -3.5f, -4.0f, -0.0436f, 0.0f, 0.1745f)
            )

            right_arm.addOrReplaceChild(
                "right_arm_2",
                CubeListBuilder.create().texOffs(18, 57)
                    .addBox(-0.5f, -3.0f, -3.0f, 5.0f, 14.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.5f, 10.0f, 0.0f, -0.2182f, 0.0f, -0.1309f)
            )

            val left_arm = partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(28, 41).mirror()
                    .addBox(0.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, CubeDeformation(0.01f)).mirror(false)
                    .texOffs(42, 39).mirror().addBox(0.0f, 10.0f, -2.0f, 3.0f, 3.0f, 4.0f, CubeDeformation(0.01f))
                    .mirror(false),
                PartPose.offsetAndRotation(4.5f, -3.5f, -4.0f, -0.0436f, 0.0f, -0.1745f)
            )

            left_arm.addOrReplaceChild(
                "left_arm_2",
                CubeListBuilder.create().texOffs(18, 57).mirror()
                    .addBox(-4.5f, -3.0f, -3.0f, 5.0f, 14.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(1.5f, 10.0f, 0.0f, -0.2182f, 0.0f, 0.1309f)
            )

            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.5f, -4.0f, -4.0f, 7.0f, 7.0f, 7.0f, CubeDeformation(0.0f))
                    .texOffs(0, 14).addBox(-3.5f, 3.0f, -4.0f, 7.0f, 2.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -7.75f, -6.0f)
            )

            head.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(82, 0)
                    .addBox(-3.5f, 0.25f, 0.9f, 7.0f, 7.0f, 4.0f, CubeDeformation(0.1f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 1.0036f, 0.0f, 0.0f)
            )

            head.addOrReplaceChild(
                "cube_r6",
                CubeListBuilder.create().texOffs(21, 0).mirror()
                    .addBox(-5.25f, -2.0f, -4.0f, 4.0f, 7.0f, 0.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.6545f, 0.0f)
            )

            head.addOrReplaceChild(
                "cube_r7",
                CubeListBuilder.create().texOffs(21, 0)
                    .addBox(1.25f, -2.0f, -4.0f, 4.0f, 7.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, -0.6545f, 0.0f)
            )

            head.addOrReplaceChild(
                "cube_r8",
                CubeListBuilder.create().texOffs(41, 0).mirror()
                    .addBox(-3.5f, -4.75f, -3.25f, 2.0f, 2.0f, 3.0f, CubeDeformation(0.2f)).mirror(false)
                    .texOffs(41, 0).addBox(1.5f, -4.75f, -3.25f, 2.0f, 2.0f, 3.0f, CubeDeformation(0.2f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.3054f, 0.0f, 0.0f)
            )

            head.addOrReplaceChild(
                "top_snout",
                CubeListBuilder.create().texOffs(28, 2)
                    .addBox(-1.5f, -2.0f, -5.0f, 3.0f, 2.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(46, 2).addBox(-1.5f, -0.5f, -5.0f, 3.0f, 2.0f, 5.0f, CubeDeformation(-0.15f)),
                PartPose.offset(0.0f, 0.0f, -4.0f)
            )

            head.addOrReplaceChild(
                "bottom_snout",
                CubeListBuilder.create().texOffs(23, 9)
                    .addBox(-2.0f, 0.0f, -5.0f, 3.0f, 2.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(41, 9).addBox(-2.0f, -1.5f, -5.0f, 3.0f, 2.0f, 5.0f, CubeDeformation(-0.1f))
                    .texOffs(28, 16).addBox(-2.0f, 2.0f, -5.0f, 3.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.5f, 0.0f, -4.0f)
            )

            val right_ear =
                head.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.offset(-3.5f, -1.0f, -1.0f))

            right_ear.addOrReplaceChild(
                "cube_r9",
                CubeListBuilder.create().texOffs(54, 4)
                    .addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.7854f, -0.6109f, 0.0f)
            )

            val left_ear =
                head.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.offset(3.5f, -1.0f, -1.0f))

            left_ear.addOrReplaceChild(
                "cube_r10",
                CubeListBuilder.create().texOffs(54, 4)
                    .addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.7854f, 0.6109f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "hat",
                CubeListBuilder.create().texOffs(4, 2)
                    .addBox(1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )


            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}