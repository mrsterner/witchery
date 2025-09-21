package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.LilithEntity
import net.minecraft.client.model.ArmedModel
import net.minecraft.client.model.HierarchicalModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.HumanoidArm
import java.util.function.Function
import kotlin.math.cos
import kotlin.math.sin

class LilithEntityModel(val root: ModelPart) :
    HierarchicalModel<LilithEntity>(Function { location: ResourceLocation ->
        RenderType.entityTranslucent(
            location
        )
    }),
    ArmedModel {

    private val body: ModelPart = root.getChild("body")
    private val clothFront: ModelPart = body.getChild("clothFront")
    private val clothBack: ModelPart = body.getChild("clothBack")
    private val rightArm: ModelPart = body.getChild("rightArm")
    private val leftArm: ModelPart = body.getChild("leftArm")
    private val head: ModelPart = body.getChild("head")
    private val leftLeg: ModelPart = body.getChild("leftLeg")
    private val rightLeg: ModelPart = body.getChild("rightLeg")

    override fun setupAnim(
        entity: LilithEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        rightArm.xRot = sin(ageInTicks * 0.1f) * 0.1f
        rightArm.zRot = cos(ageInTicks * 0.1f) * 0.05f

        leftArm.xRot = cos(ageInTicks * 0.1f) * 0.1f
        leftArm.zRot = sin(ageInTicks * 0.1f) * 0.05f

        leftLeg.xRot = cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount
        rightLeg.xRot = cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount

        leftLeg.xRot -= Math.PI.toFloat() / 8
        rightLeg.xRot -= Math.PI.toFloat() / 8

        leftArm.xRot = +cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.2f * limbSwingAmount
        rightArm.xRot = +cos(limbSwing * 0.6662f) * 1.2f * limbSwingAmount

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
    }

    override fun root(): ModelPart {
        return root
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("lilith"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 18)
                    .addBox(-5.0f, 0.0f, -3.0f, 10.0f, 11.0f, 7.0f, CubeDeformation(0.0f))
                    .texOffs(50, 18).addBox(-5.0f, 0.0f, -3.0f, 10.0f, 11.0f, 7.0f, CubeDeformation(0.25f))
                    .texOffs(0, 36).addBox(-4.0f, 11.0f, -2.0f, 8.0f, 8.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(56, 36).addBox(-4.0f, 11.0f, -2.0f, 8.0f, 8.0f, 5.0f, CubeDeformation(0.25f)),
                PartPose.offsetAndRotation(0.0f, -15.5f, -1.0f, 0.0436f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "clothFront",
                CubeListBuilder.create().texOffs(84, 24)
                    .addBox(-3.0f, 0.25f, 0.0f, 6.0f, 7.0f, 1.0f, CubeDeformation(0.25f)),
                PartPose.offsetAndRotation(0.0f, 19.25f, -2.0f, -0.3927f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "clothBack",
                CubeListBuilder.create().texOffs(98, 24)
                    .addBox(-4.0f, 0.25f, -1.0f, 8.0f, 7.0f, 1.0f, CubeDeformation(0.25f)),
                PartPose.offsetAndRotation(0.0f, 16.25f, 3.0f, 0.1309f, 0.0f, 0.0f)
            )

            val rightArm = body.addOrReplaceChild(
                "rightArm",
                CubeListBuilder.create().texOffs(34, 18)
                    .addBox(-4.0f, -1.0f, -2.0f, 4.0f, 20.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(34, 42).addBox(-4.0f, 19.0f, -2.0f, 4.0f, 3.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(42, 42).addBox(-4.0f, 19.0f, 0.0f, 4.0f, 3.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(50, 36).addBox(-4.0f, 19.0f, 2.0f, 4.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-5.0f, 2.0f, 0.0f, -0.0436f, 0.0f, 0.0f)
            )

            rightArm.addOrReplaceChild(
                "right_spike_2_r1",
                CubeListBuilder.create().texOffs(84, 44)
                    .addBox(-3.0f, 13.7179f, 7.5356f, 1.0f, 2.0f, 15.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.6981f, 0.0f, 0.0f)
            )

            rightArm.addOrReplaceChild(
                "right_spike_1_r1",
                CubeListBuilder.create().texOffs(66, 45)
                    .addBox(-3.0f, 11.1874f, -4.6548f, 1.0f, 3.0f, 16.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.4363f, 0.0f, 0.0f)
            )

            val leftArm = body.addOrReplaceChild(
                "leftArm",
                CubeListBuilder.create().texOffs(34, 18).mirror()
                    .addBox(0.0f, -1.0f, -2.0f, 4.0f, 20.0f, 4.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(50, 36).mirror().addBox(0.0f, 19.0f, 2.0f, 4.0f, 3.0f, 0.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(42, 42).mirror().addBox(0.0f, 19.0f, 0.0f, 4.0f, 3.0f, 0.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(34, 42).mirror().addBox(0.0f, 19.0f, -2.0f, 4.0f, 3.0f, 0.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offsetAndRotation(5.0f, 2.0f, 0.0f, -0.0436f, 0.0f, 0.0f)
            )

            leftArm.addOrReplaceChild(
                "left_spike_2_r1",
                CubeListBuilder.create().texOffs(84, 44)
                    .addBox(2.0f, 13.7179f, 7.5356f, 1.0f, 2.0f, 15.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.6981f, 0.0f, 0.0f)
            )

            leftArm.addOrReplaceChild(
                "left_spike_1_r1",
                CubeListBuilder.create().texOffs(66, 45)
                    .addBox(2.0f, 11.1874f, -4.6548f, 1.0f, 3.0f, 16.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.4363f, 0.0f, 0.0f)
            )

            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.5f, -9.0f, -4.0f, 9.0f, 9.0f, 9.0f, CubeDeformation(0.0f))
                    .texOffs(36, 0).addBox(-4.5f, -9.0f, -4.0f, 9.0f, 9.0f, 9.0f, CubeDeformation(0.3f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.0436f, 0.0f, 0.0f)
            )

            head.addOrReplaceChild(
                "right_antlers_r1",
                CubeListBuilder.create().texOffs(72, 9).mirror()
                    .addBox(3.5f, -15.0f, -3.0f, 4.0f, 9.0f, 0.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, -0.2618f, 0.0f)
            )

            head.addOrReplaceChild(
                "left_antlers_r1",
                CubeListBuilder.create().texOffs(72, 9)
                    .addBox(-7.5f, -15.0f, -3.0f, 4.0f, 9.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.2618f, 0.0f)
            )

            body.addOrReplaceChild(
                "leftLeg",
                CubeListBuilder.create().texOffs(21, 46).mirror()
                    .addBox(0.0f, -2.0f, -3.0f, 5.0f, 13.0f, 5.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(41, 46).mirror().addBox(0.0f, 7.0f, 2.0f, 5.0f, 13.0f, 5.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offsetAndRotation(0.0f, 20.0f, 1.0f, -0.3491f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "rightLeg",
                CubeListBuilder.create().texOffs(21, 46)
                    .addBox(-5.0f, -2.0f, -3.0f, 5.0f, 13.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(41, 46).addBox(-5.0f, 7.0f, 2.0f, 5.0f, 13.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 20.0f, 1.0f, -0.3491f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 64)
        }
    }

    override fun translateToHand(side: HumanoidArm, poseStack: PoseStack) {

    }
}