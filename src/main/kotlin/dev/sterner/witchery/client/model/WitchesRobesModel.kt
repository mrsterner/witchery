package dev.sterner.witchery.client.model

import dev.sterner.witchery.Witchery
import net.minecraft.client.model.HumanoidArmorModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.entity.LivingEntity


class WitchesRobesModel(val root: ModelPart) : HumanoidArmorModel<LivingEntity>(root) {

    var baba1 = head.getChild("baba_hat_tip_2_r1")
    var baba2 = head.getChild("baba_hat_tip_r1")

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("witch_robes"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root


            val headHat = partDefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-7.5f, -6.0f, -7.5f, 15.0f, 2.0f, 15.0f, CubeDeformation(0.0f))
                    .texOffs(51, 0).addBox(-4.5f, -11.0f, -4.5f, 9.0f, 6.0f, 9.0f, CubeDeformation(0.05f))
                    .texOffs(87, 0).addBox(-2.5f, -14.0f, -1.0f, 5.0f, 3.0f, 9.0f, CubeDeformation(0.05f))
                    .texOffs(60, 44).addBox(-8.5f, -5.0f, -8.5f, 17.0f, 3.0f, 17.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            headHat.addOrReplaceChild(
                "baba_hat_tip_2_r1",
                CubeListBuilder.create().texOffs(120, 44)
                    .addBox(-3.0f, -17.0f, -1.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.05f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.1745f)
            )

            headHat.addOrReplaceChild(
                "baba_hat_tip_r1",
                CubeListBuilder.create().texOffs(108, 35)
                    .addBox(-3.5f, -14.0f, -2.5f, 5.0f, 4.0f, 5.0f, CubeDeformation(0.05f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0873f)
            )

            partDefinition.addOrReplaceChild(
                "hat",
                CubeListBuilder.create(), PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
            )

            val bodyRobe = partDefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 19)
                    .addBox(-4.5f, -0.5f, -2.5f, 9.0f, 13.0f, 5.0f, CubeDeformation(0.05f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            bodyRobe.addOrReplaceChild(
                "skull_2_r1",
                CubeListBuilder.create().texOffs(59, 21)
                    .addBox(-1.0f, 3.0f, -4.0f, 2.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(58, 15).addBox(-1.5f, 0.0f, -4.0f, 3.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.1745f, 0.0f, 0.0f)
            )

            bodyRobe.addOrReplaceChild(
                "robeFlap",
                CubeListBuilder.create().texOffs(0, 37)
                    .addBox(-4.5f, 0.1f, 0.05f, 9.0f, 9.0f, 5.0f, CubeDeformation(0.04f)),
                PartPose.offsetAndRotation(0.0f, 12.5f, -2.5f, 0.2182f, 0.0f, 0.0f)
            )

            val rightArmRobe = partDefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(28, 20)
                    .addBox(-3.5f, -2.5f, -2.5f, 5.0f, 12.0f, 5.0f, CubeDeformation(0.01f)),
                PartPose.offset(-5.0f, 2.0f, 0.0f)
            )

            rightArmRobe.addOrReplaceChild(
                "right_cuff_r1",
                CubeListBuilder.create().texOffs(48, 28)
                    .addBox(-2.5f, -4.65f, -0.35f, 5.0f, 5.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.0f, 9.0f, 2.5f, 0.7418f, 0.0f, 0.0f)
            )

            val leftArmRobe = partDefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(28, 20).mirror()
                    .addBox(-1.5f, -2.5f, -2.5f, 5.0f, 12.0f, 5.0f, CubeDeformation(0.01f)).mirror(false),
                PartPose.offset(5.0f, 2.0f, 0.0f)
            )

            leftArmRobe.addOrReplaceChild(
                "left_cuff_r1",
                CubeListBuilder.create().texOffs(48, 28).mirror()
                    .addBox(-2.5f, -4.65f, -0.35f, 5.0f, 5.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(1.0f, 9.0f, 2.5f, 0.7418f, 0.0f, 0.0f)
            )

            partDefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(28, 37)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.4f)),
                PartPose.offset(-1.9f, 12.0f, 0.0f)
            )

            partDefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(28, 37).mirror()
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.4f)).mirror(false),
                PartPose.offset(1.9f, 12.0f, 0.0f)
            )

            return LayerDefinition.create(meshDefinition, 128, 64)
        }
    }
}