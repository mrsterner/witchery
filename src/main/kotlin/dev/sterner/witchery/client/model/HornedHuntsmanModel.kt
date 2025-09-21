package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.HornedHuntsmanEntity
import net.minecraft.client.model.ArmedModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.util.Mth
import net.minecraft.world.entity.HumanoidArm


class HornedHuntsmanModel(val root: ModelPart) :
    HumanoidModel<HornedHuntsmanEntity>(root), ArmedModel {

    private val body: ModelPart = root.getChild("body")
    private val head: ModelPart = root.getChild("head")
    private val leftLeg: ModelPart = root.getChild("left_leg")
    private val rightLeg: ModelPart = root.getChild("right_leg")
    private val leftArm: ModelPart = root.getChild("left_arm")
    private val rightArm: ModelPart = root.getChild("right_arm")

    override fun setupAnim(
        entity: HornedHuntsmanEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)
        body.y = 6f
        rightLeg.y = 8f
        leftLeg.y = 8f
        leftArm.y = 3f
        rightArm.y = 3f

        rightLeg.zRot += 5.0f * (Mth.PI / 180f)
        leftLeg.zRot -= 5.0f * (Mth.PI / 180f)

        rightArm.zRot += 2.5f * (Mth.PI / 180f)
        leftArm.zRot -= 2.5f * (Mth.PI / 180f)

        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6f) * 0.6f * limbSwingAmount / 1 - 10.0f * (Mth.PI / 180f)
        this.leftLeg.xRot =
            Mth.cos(limbSwing * 0.6f + 3.1415927f) * 0.6f * limbSwingAmount / 1 - 10.0f * (Mth.PI / 180f)
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    override fun translateToHand(
        side: HumanoidArm,
        poseStack: PoseStack
    ) {
        val modelPart: ModelPart = if (side == HumanoidArm.RIGHT) rightArm else leftArm
        poseStack.translate(0.0, 0.15, 0.0)
        modelPart.translateAndRotate(poseStack)
        poseStack.translate(0.0, -0.15, 0.0)
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(Witchery.id("horned_huntsman"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(21, 0)
                    .addBox(-3.0f, 1.0f, -2.0f, 6.0f, 3.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 4.0f, 1.0f)
            )

            body.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 14)
                    .addBox(-4.0f, -7.0f, -1.0f, 8.0f, 7.0f, 5.0f, CubeDeformation(0.2f))
                    .texOffs(21, 21).addBox(-4.0f, -7.0f, -1.0f, 8.0f, 7.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 2.0f, -1.0f, 0.3054f, 0.0f, 0.0f)
            )

            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(26, 8)
                    .addBox(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(25, 45).addBox(-3.0f, -3.0f, -3.0f, 6.0f, 7.0f, 6.0f, CubeDeformation(0.1f))
                    .texOffs(40, 54).addBox(-3.0f, 1.0f, -3.0f, 6.0f, 4.0f, 6.0f, CubeDeformation(0.1f))
                    .texOffs(0, 0).addBox(-3.5f, -3.5f, -3.5f, 7.0f, 7.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.0f, -4.0f, 0.1745f, 0.0f, 0.0f)
            )

            head.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(0, 44)
                    .addBox(-6.0f, -10.5f, 0.0f, 12.0f, 11.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -2.5f, 1.0f, -0.48f, 0.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "hat",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, 0f, 0.0f, 0f, 0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0f, 0f, 0f, 0.0f, 0.0f)
            )

            head.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(0, 33)
                    .addBox(-6.0f, -10.5f, 0.0f, 12.0f, 11.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -2.5f, -1.0f, 0.0873f, 0.0f, 0.0f)
            )

            val leftArm = partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(0, 26)
                    .addBox(-2.0f, -2.0f, -1.0f, 3.0f, 3.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(41, 0).addBox(-2.0f, 1.0f, -0.5f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.0f, 1.0f, -1.0f, -0.0436f, 0.0f, -0.0873f)
            )

            leftArm.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(12, 26)
                    .addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.0f, 6.0f, 0.5f, -0.5672f, 0.0f, 0.0f)
            )

            val leftLeg = partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(36, 33)
                    .addBox(-1.5f, 0.0f, -1.0f, 3.0f, 8.0f, 3.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(1.5f, 7.0f, 0.5f, -0.2182f, 0.0f, -0.0436f)
            )

            leftLeg.addOrReplaceChild(
                "bone",
                CubeListBuilder.create().texOffs(24, 33)
                    .addBox(-1.5f, 0.0f, 0.5f, 3.0f, 9.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 8.0f, -1.5f, 0.2182f, 0.0f, 0.0f)
            )

            val rightLeg = partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(36, 33).mirror()
                    .addBox(-1.5f, 0.0f, -1.0f, 3.0f, 8.0f, 3.0f, CubeDeformation(0.01f)).mirror(false),
                PartPose.offsetAndRotation(-1.5f, 7.0f, 0.5f, -0.2182f, 0.0f, 0.0436f)
            )

            rightLeg.addOrReplaceChild(
                "bone2",
                CubeListBuilder.create().texOffs(24, 33).mirror()
                    .addBox(-1.5f, 0.0f, 0.5f, 3.0f, 9.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 8.0f, -1.5f, 0.2182f, 0.0f, 0.0f)
            )

            val rightArm = partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(0, 26).mirror()
                    .addBox(-1.0f, -2.0f, -1.0f, 3.0f, 3.0f, 3.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(41, 0).mirror().addBox(0.0f, 1.0f, -0.5f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offsetAndRotation(-6.0f, 1.0f, -1.0f, -0.0436f, 0.0f, 0.0873f)
            )

            rightArm.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(12, 26).mirror()
                    .addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(1.0f, 6.0f, 0.5f, -0.5672f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}