package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.DemonEntity
import dev.sterner.witchery.entity.ImpEntity
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


class DemonEntityModel(root: ModelPart) :
    HierarchicalModel<DemonEntity>(Function { location: ResourceLocation ->
        RenderType.entityTranslucent(
            location
        )
    }),
    ArmedModel {

    private val demon: ModelPart = root.getChild("demon")
    private val head: ModelPart = demon.getChild("head")
    private val leftArm: ModelPart = demon.getChild("leftArm")
    private val rightArm: ModelPart = demon.getChild("rightArm")
    private val rightLeg: ModelPart = demon.getChild("rightLeg")
    private val leftLeg: ModelPart = demon.getChild("leftLeg")
    private val body: ModelPart = demon.getChild("body")
    private val rWing: ModelPart = body.getChild("rWing")
    private val lWing: ModelPart = body.getChild("lWing")

    override fun setupAnim(
        entity: DemonEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        head.xRot = headPitch * 0.017453292f
        head.yRot = netHeadYaw * 0.017453292f
    }

    override fun root(): ModelPart {
        return demon
    }

    override fun translateToHand(side: HumanoidArm, poseStack: PoseStack) {

    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("demon"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val demon =
                partdefinition.addOrReplaceChild("demon", CubeListBuilder.create(), PartPose.offset(0.0f, -4.0f, 8.0f))

            val head = demon.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(24, 89)
                    .addBox(-5.0f, -9.0f, -4.0f, 10.0f, 9.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -14.0f, -10.0f, 0.1309f, 0.0f, 0.0f)
            )

            val rHorn = head.addOrReplaceChild(
                "rHorn",
                CubeListBuilder.create().texOffs(53, 89).mirror()
                    .addBox(-3.0f, -1.0f, -1.0f, 4.0f, 3.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-2.0f, -9.0f, -4.0f, -0.1745f, 0.0f, 0.0f)
            )

            val bone2 = rHorn.addOrReplaceChild(
                "bone2",
                CubeListBuilder.create().texOffs(52, 107).mirror()
                    .addBox(-3.0f, -2.0f, -0.4f, 3.0f, 3.0f, 3.0f, CubeDeformation(-0.01f)).mirror(false),
                PartPose.offsetAndRotation(-2.7f, 1.0f, -0.6f, 0.0f, 0.1745f, 0.2618f)
            )

            val bone3 = bone2.addOrReplaceChild(
                "bone3",
                CubeListBuilder.create().texOffs(14, 23).mirror()
                    .addBox(-3.0f, -2.0f, -0.4f, 3.0f, 3.0f, 3.0f, CubeDeformation(-0.1f)).mirror(false),
                PartPose.offsetAndRotation(-2.7f, 0.0f, 0.0f, 0.0f, 0.2182f, 0.2618f)
            )

            val bone14 = bone3.addOrReplaceChild(
                "bone14",
                CubeListBuilder.create().texOffs(53, 23).mirror()
                    .addBox(-4.0f, -1.0f, -0.4f, 5.0f, 2.0f, 2.0f, CubeDeformation(-0.02f)).mirror(false),
                PartPose.offsetAndRotation(-3.5f, -0.7f, 0.5f, 0.0f, 0.2182f, 0.2618f)
            )

            val bone21 = bone14.addOrReplaceChild(
                "bone21",
                CubeListBuilder.create().texOffs(0, 6).mirror()
                    .addBox(-4.0f, 0.0f, -0.4f, 4.0f, 1.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-2.9f, -0.3f, 0.3f, 0.0f, 0.2182f, 0.2618f)
            )

            val lHorn = head.addOrReplaceChild(
                "lHorn",
                CubeListBuilder.create().texOffs(53, 89)
                    .addBox(-1.0f, -1.0f, -1.0f, 4.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.0f, -9.0f, -4.0f, -0.1745f, 0.0f, 0.0f)
            )

            val bone = lHorn.addOrReplaceChild(
                "bone",
                CubeListBuilder.create().texOffs(52, 107)
                    .addBox(0.0f, -2.0f, -0.4f, 3.0f, 3.0f, 3.0f, CubeDeformation(-0.01f)),
                PartPose.offsetAndRotation(2.7f, 1.0f, -0.6f, 0.0f, -0.1745f, -0.2618f)
            )

            val bone24 = bone.addOrReplaceChild(
                "bone24",
                CubeListBuilder.create().texOffs(14, 23)
                    .addBox(0.0f, -2.0f, -0.4f, 3.0f, 3.0f, 3.0f, CubeDeformation(-0.1f)),
                PartPose.offsetAndRotation(2.7f, 0.0f, 0.0f, 0.0f, -0.2182f, -0.2618f)
            )

            val bone25 = bone24.addOrReplaceChild(
                "bone25",
                CubeListBuilder.create().texOffs(53, 23)
                    .addBox(-1.0f, -1.0f, -0.4f, 5.0f, 2.0f, 2.0f, CubeDeformation(-0.02f)),
                PartPose.offsetAndRotation(3.5f, -0.7f, 0.5f, 0.0f, -0.2182f, -0.2618f)
            )

            val bone26 = bone25.addOrReplaceChild(
                "bone26",
                CubeListBuilder.create().texOffs(0, 6)
                    .addBox(0.0f, 0.0f, -0.4f, 4.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.9f, -0.3f, 0.3f, 0.0f, -0.2182f, -0.2618f)
            )

            val leftArm = demon.addOrReplaceChild(
                "leftArm",
                CubeListBuilder.create().texOffs(59, 108)
                    .addBox(0.0f, -3.5f, -3.5f, 5.0f, 7.0f, 7.0f, CubeDeformation(0.0f))
                    .texOffs(107, 38).addBox(0.0f, -3.5f, -3.5f, 5.0f, 7.0f, 7.0f, CubeDeformation(0.5f)),
                PartPose.offsetAndRotation(5.0f, -12.0f, -6.5f, 0.3054f, 0.0f, -0.3054f)
            )

            val middleLeftArm = leftArm.addOrReplaceChild(
                "middleLeftArm",
                CubeListBuilder.create().texOffs(36, 121)
                    .addBox(-2.0f, -1.0f, -2.5f, 4.0f, 9.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 89).addBox(-2.0f, -1.0f, -2.5f, 4.0f, 9.0f, 5.0f, CubeDeformation(0.5f)),
                PartPose.offsetAndRotation(2.0f, 3.5f, 0.0f, 0.0436f, 0.0f, 0.0f)
            )

            val lowerLeftArm = middleLeftArm.addOrReplaceChild(
                "lowerLeftArm",
                CubeListBuilder.create().texOffs(79, 121)
                    .addBox(-2.0f, -1.0f, -2.0f, 4.0f, 9.0f, 4.0f, CubeDeformation(-0.005f)),
                PartPose.offsetAndRotation(0.0f, 8.0f, 0.0f, -0.9163f, 0.0f, 0.0f)
            )

            val leftHand = lowerLeftArm.addOrReplaceChild(
                "leftHand",
                CubeListBuilder.create().texOffs(40, 23).mirror()
                    .addBox(-2.0f, -1.0f, -2.5f, 4.0f, 5.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 8.0f, 0.0f, 0.1309f, 0.0436f, -0.1745f)
            )

            val rightArm = demon.addOrReplaceChild(
                "rightArm",
                CubeListBuilder.create().texOffs(107, 60)
                    .addBox(-5.0f, -3.5f, -3.5f, 5.0f, 7.0f, 7.0f, CubeDeformation(0.0f))
                    .texOffs(35, 107).addBox(-5.0f, -3.5f, -3.5f, 5.0f, 7.0f, 7.0f, CubeDeformation(0.5f)),
                PartPose.offsetAndRotation(-5.0f, -12.0f, -6.5f, 0.3054f, 0.0f, 0.3054f)
            )

            val middleLeftArm2 = rightArm.addOrReplaceChild(
                "middleLeftArm2",
                CubeListBuilder.create().texOffs(119, 15)
                    .addBox(-2.0f, -1.0f, -2.5f, 4.0f, 9.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(66, 67).addBox(-2.0f, -1.0f, -2.5f, 4.0f, 9.0f, 5.0f, CubeDeformation(0.5f)),
                PartPose.offsetAndRotation(-2.0f, 3.5f, 0.0f, 0.0436f, 0.0f, 0.0f)
            )

            val lowerLeftArm2 = middleLeftArm2.addOrReplaceChild(
                "lowerLeftArm2",
                CubeListBuilder.create().texOffs(95, 121)
                    .addBox(-2.0f, -1.0f, -2.0f, 4.0f, 9.0f, 4.0f, CubeDeformation(-0.005f)),
                PartPose.offsetAndRotation(0.0f, 8.0f, 0.0f, -0.9163f, 0.0f, 0.0f)
            )

            val rightHand = lowerLeftArm2.addOrReplaceChild(
                "rightHand",
                CubeListBuilder.create().texOffs(40, 23)
                    .addBox(-2.0f, -1.0f, -2.5f, 4.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 8.0f, 0.0f, 0.1309f, -0.0436f, 0.1745f)
            )

            val rightLeg = demon.addOrReplaceChild(
                "rightLeg",
                CubeListBuilder.create().texOffs(96, 89)
                    .addBox(-3.0f, -0.5f, -3.5f, 6.0f, 13.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-4.0f, 5.5f, -0.5f, -0.48f, 0.3491f, 0.0f)
            )

            val middleLeftLeg2 = rightLeg.addOrReplaceChild(
                "middleLeftLeg2",
                CubeListBuilder.create().texOffs(66, 45)
                    .addBox(-2.0f, 0.0f, -0.5f, 4.0f, 10.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.5f, 12.5f, -3.0f, 1.3963f, 0.0f, 0.0f)
            )

            val upperLeftFoot2 = middleLeftLeg2.addOrReplaceChild(
                "upperLeftFoot2",
                CubeListBuilder.create().texOffs(90, 67)
                    .addBox(-1.0f, -1.0f, -10.0f, 3.0f, 3.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.5f, 10.0f, 2.5f, -0.1745f, 0.0f, 0.0f)
            )

            val lowerLeftFoot2 = upperLeftFoot2.addOrReplaceChild(
                "lowerLeftFoot2",
                CubeListBuilder.create().texOffs(20, 23)
                    .addBox(-2.0f, -2.0f, -2.0f, 4.0f, 3.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.5f, 1.0f, -10.0f, -0.7418f, 0.0f, 0.0f)
            )

            val rightToes = lowerLeftFoot2.addOrReplaceChild(
                "rightToes",
                CubeListBuilder.create(),
                PartPose.offset(-1.5f, -0.5f, -5.5f)
            )

            val leftLeg = demon.addOrReplaceChild(
                "leftLeg",
                CubeListBuilder.create().texOffs(93, 16)
                    .addBox(-3.0f, -0.5f, -3.5f, 6.0f, 13.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.0f, 5.5f, -0.5f, -0.48f, -0.3491f, 0.0f)
            )

            val middleLeftLeg3 = leftLeg.addOrReplaceChild(
                "middleLeftLeg3",
                CubeListBuilder.create().texOffs(66, 23)
                    .addBox(-2.0f, 0.0f, -0.5f, 4.0f, 10.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.5f, 12.5f, -3.0f, 1.3963f, 0.0f, 0.0f)
            )

            val upperLeftFoot3 = middleLeftLeg3.addOrReplaceChild(
                "upperLeftFoot3",
                CubeListBuilder.create().texOffs(90, 45)
                    .addBox(-2.0f, -1.0f, -10.0f, 3.0f, 3.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.5f, 10.0f, 2.5f, -0.1745f, 0.0f, 0.0f)
            )

            val lowerLeftFoot3 = upperLeftFoot3.addOrReplaceChild(
                "lowerLeftFoot3",
                CubeListBuilder.create().texOffs(0, 23)
                    .addBox(-2.0f, -2.0f, -2.0f, 4.0f, 3.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.5f, 1.0f, -10.0f, -0.7418f, 0.0f, 0.0f)
            )

            val leftToes = lowerLeftFoot3.addOrReplaceChild(
                "leftToes",
                CubeListBuilder.create(),
                PartPose.offset(1.5f, -0.5f, -5.5f)
            )

            val body = demon.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0f, -5.0f, -2.0f))

            val lWing = body.addOrReplaceChild(
                "lWing",
                CubeListBuilder.create().texOffs(110, 74)
                    .addBox(-2.0f, -2.0f, 0.0f, 3.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.0f, -8.0f, 2.0f, 0.48f, 0.2182f, 0.0f)
            )

            val bone31 = lWing.addOrReplaceChild(
                "bone31",
                CubeListBuilder.create().texOffs(105, 109)
                    .addBox(-2.0f, -4.0f, 0.0f, 3.0f, 4.0f, 8.0f, CubeDeformation(-0.01f)),
                PartPose.offsetAndRotation(0.0f, 2.0f, 8.0f, 0.5236f, 0.0f, 0.0f)
            )

            val bone32 = bone31.addOrReplaceChild(
                "bone32",
                CubeListBuilder.create().texOffs(83, 109)
                    .addBox(-2.0f, -4.0f, 0.0f, 3.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 8.0f, 0.3491f, 0.0f, 0.0f)
            )

            val bone33 = bone32.addOrReplaceChild(
                "bone33",
                CubeListBuilder.create().texOffs(66, 45)
                    .addBox(-2.0f, -1.0f, 0.0f, 3.0f, 4.0f, 18.0f, CubeDeformation(-0.01f))
                    .texOffs(0, 0).addBox(0.0f, 3.0f, -2.0f, 0.0f, 28.0f, 33.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.0f, 8.0f, -1.7453f, 0.0f, 0.0f)
            )

            val bone34 = bone33.addOrReplaceChild(
                "bone34",
                CubeListBuilder.create().texOffs(66, 23)
                    .addBox(-2.0f, 0.0f, 0.0f, 3.0f, 4.0f, 18.0f, CubeDeformation(-0.02f)),
                PartPose.offsetAndRotation(0.0f, -1.0f, 18.0f, -0.829f, 0.0f, 0.0f)
            )

            val rWing = body.addOrReplaceChild(
                "rWing",
                CubeListBuilder.create().texOffs(14, 115)
                    .addBox(-1.0f, -2.0f, 0.0f, 3.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-4.0f, -8.0f, 2.0f, 0.48f, -0.2182f, 0.0f)
            )

            val bone27 = rWing.addOrReplaceChild(
                "bone27",
                CubeListBuilder.create().texOffs(111, 3)
                    .addBox(-2.0f, -4.0f, 0.0f, 3.0f, 4.0f, 8.0f, CubeDeformation(-0.01f)),
                PartPose.offsetAndRotation(1.0f, 2.0f, 8.0f, 0.5236f, 0.0f, 0.0f)
            )

            val bone28 = bone27.addOrReplaceChild(
                "bone28",
                CubeListBuilder.create().texOffs(0, 111)
                    .addBox(-2.0f, -4.0f, 0.0f, 3.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 8.0f, 0.3491f, 0.0f, 0.0f)
            )

            val bone29 = bone28.addOrReplaceChild(
                "bone29",
                CubeListBuilder.create().texOffs(0, 89)
                    .addBox(-2.0f, -1.0f, 0.0f, 3.0f, 4.0f, 18.0f, CubeDeformation(-0.01f))
                    .texOffs(0, 28).addBox(0.0f, 3.0f, -2.0f, 0.0f, 28.0f, 33.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.0f, 8.0f, -1.7453f, 0.0f, 0.0f)
            )

            val bone30 = bone29.addOrReplaceChild(
                "bone30",
                CubeListBuilder.create().texOffs(66, 67)
                    .addBox(-2.0f, 0.0f, 0.0f, 3.0f, 4.0f, 18.0f, CubeDeformation(-0.02f)),
                PartPose.offsetAndRotation(0.0f, -1.0f, 18.0f, -0.829f, 0.0f, 0.0f)
            )

            val coreBody =
                body.addOrReplaceChild("coreBody", CubeListBuilder.create(), PartPose.offset(0.0f, 11.5f, 2.0f))

            val core = coreBody.addOrReplaceChild(
                "core",
                CubeListBuilder.create().texOffs(62, 89)
                    .addBox(-5.0f, -5.0f, -2.5f, 10.0f, 12.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -9.5f, -2.5f)
            )

            val becken = core.addOrReplaceChild(
                "becken",
                CubeListBuilder.create().texOffs(89, 0)
                    .addBox(-4.5f, 0.0f, -3.0f, 9.0f, 5.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 7.0f, 1.5f)
            )

            val upperBody = coreBody.addOrReplaceChild(
                "upperBody",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, -7.5f, -2.0f, 0.1309f, 0.0f, 0.0f)
            )

            val chestLower = upperBody.addOrReplaceChild(
                "chestLower",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, -2.0f, -1.0f, 0.2182f, 0.0f, 0.0f)
            )

            val chestUpper = chestLower.addOrReplaceChild(
                "chestUpper",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, -5.0f, 1.0f, 0.0873f, 0.0f, 0.0f)
            )

            val chest =
                chestUpper.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, -5.0f))

            val chestBone = chest.addOrReplaceChild(
                "chestBone",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-7.0f, -8.0f, -2.5f, 14.0f, 12.0f, 11.0f, CubeDeformation(0.5f)),
                PartPose.offset(0.0f, 0.0f, 2.5f)
            )

            return LayerDefinition.create(meshdefinition, 256, 256)
        }
    }
}