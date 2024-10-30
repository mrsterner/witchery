package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.EntEntity
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
import net.minecraft.util.Mth
import net.minecraft.world.entity.HumanoidArm
import java.util.function.Function


class EntEntityModel(val root: ModelPart) :
    HierarchicalModel<EntEntity>(Function { location: ResourceLocation ->
        RenderType.entityTranslucent(
            location
        )
    }),
    ArmedModel {

    private val rightLeg: ModelPart = root.getChild("rightLeg")
    private val leftArm: ModelPart = root.getChild("leftArm")
    private val rightArm: ModelPart = root.getChild("rightArm")
    private val body: ModelPart = root.getChild("body")
    private val leftLeg: ModelPart = root.getChild("leftLeg")

    override fun setupAnim(
        entity: EntEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.rightLeg.xRot = -1.5f * Mth.triangleWave(limbSwing, 13.0f) * limbSwingAmount
        this.leftLeg.xRot = 1.5f * Mth.triangleWave(limbSwing, 13.0f) * limbSwingAmount
        this.rightLeg.yRot = 0.0f
        this.leftLeg.yRot = 0.0f
    }

    override fun prepareMobModel(entity: EntEntity, limbSwing: Float, limbSwingAmount: Float, partialTick: Float) {
        val i: Int = entity.getAttackAnimationTick()
        if (i > 0) {
            this.rightArm.xRot = -2.0f + 1.5f * Mth.triangleWave(i.toFloat() - partialTick, 10.0f)
            this.leftArm.xRot = -2.0f + 1.5f * Mth.triangleWave(i.toFloat() - partialTick, 10.0f)
        } else {
            this.rightArm.xRot = (-0.2f + 1.5f * Mth.triangleWave(limbSwing, 13.0f)) * limbSwingAmount
            this.leftArm.xRot = (-0.2f - 1.5f * Mth.triangleWave(limbSwing, 13.0f)) * limbSwingAmount
        }
    }

    override fun renderToBuffer(
        poseStack: PoseStack?,
        vertexConsumer: VertexConsumer?,
        packedLight: Int,
        packedOverlay: Int,
        color: Int,
    ) {
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay)
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay)
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay)
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay)
    }

    override fun root(): ModelPart {
        return root
    }


    override fun translateToHand(side: HumanoidArm, poseStack: PoseStack) {

    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("ent"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val rightLeg = partdefinition.addOrReplaceChild(
                "rightLeg",
                CubeListBuilder.create().texOffs(32, 64)
                    .addBox(-4.0f, 0.0f, -4.0f, 8.0f, 16.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(-4.0f, 8.0f, 0.0f)
            )

            val leftLeg = partdefinition.addOrReplaceChild(
                "leftLeg",
                CubeListBuilder.create().texOffs(64, 40)
                    .addBox(-4.0f, 0.0f, -4.0f, 8.0f, 16.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(4.0f, 8.0f, 0.0f)
            )

            val leftArm = partdefinition.addOrReplaceChild(
                "leftArm",
                CubeListBuilder.create().texOffs(64, 0)
                    .addBox(0.0f, -3.0f, -4.0f, 8.0f, 32.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(120, 122).addBox(8.0f, 3.0f, 0.0f, 4.0f, 6.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(64, 125).addBox(0.0f, -5.0f, -4.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(8.0f, -9.0f, 0.0f)
            )

            val Crown_r1 = leftArm.addOrReplaceChild(
                "Crown_r1",
                CubeListBuilder.create().texOffs(64, 125)
                    .addBox(-7.0f, -2.0f, -1.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(1.0f, -3.0f, -3.0f, 0.0f, 1.5708f, 0.0f)
            )

            val Crown_r2 = leftArm.addOrReplaceChild(
                "Crown_r2",
                CubeListBuilder.create().texOffs(64, 125)
                    .addBox(-7.0f, -2.0f, -1.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(1.0f, -3.0f, 3.0f, 0.0f, 3.1416f, 0.0f)
            )

            val Crown_r3 = leftArm.addOrReplaceChild(
                "Crown_r3",
                CubeListBuilder.create().texOffs(64, 125)
                    .addBox(-7.0f, -2.0f, -1.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(7.0f, -3.0f, 3.0f, 0.0f, -1.5708f, 0.0f)
            )

            val rightArm = partdefinition.addOrReplaceChild(
                "rightArm",
                CubeListBuilder.create().texOffs(112, 121)
                    .addBox(-12.0f, 2.0f, -1.0f, 4.0f, 7.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(64, 125).addBox(-8.0f, -5.0f, -4.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(0, 64).addBox(-8.0f, -3.0f, -4.0f, 8.0f, 32.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(-8.0f, -9.0f, 0.0f)
            )

            val Crown_r4 = rightArm.addOrReplaceChild(
                "Crown_r4",
                CubeListBuilder.create().texOffs(64, 125)
                    .addBox(-7.0f, -2.0f, -1.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-7.0f, -3.0f, 3.0f, 0.0f, 3.1416f, 0.0f)
            )

            val Crown_r5 = rightArm.addOrReplaceChild(
                "Crown_r5",
                CubeListBuilder.create().texOffs(64, 125)
                    .addBox(-7.0f, -2.0f, -1.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-7.0f, -3.0f, -3.0f, 0.0f, 1.5708f, 0.0f)
            )

            val Crown_r6 = rightArm.addOrReplaceChild(
                "Crown_r6",
                CubeListBuilder.create().texOffs(64, 125)
                    .addBox(-7.0f, -2.0f, -1.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.0f, -3.0f, 3.0f, 0.0f, -1.5708f, 0.0f)
            )

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-8.0f, -7.5f, -7.7143f, 16.0f, 48.0f, 16.0f, CubeDeformation(0.0f))
                    .texOffs(88, 119).addBox(-14.0f, 1.5f, -0.7143f, 6.0f, 9.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(100, 118).addBox(8.0f, 8.5f, -0.7143f, 6.0f, 10.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(56, 125).addBox(-8.0f, -10.5f, -7.7143f, 16.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -32.5f, -0.2857f)
            )

            val Crown_r7 = body.addOrReplaceChild(
                "Crown_r7",
                CubeListBuilder.create().texOffs(56, 125)
                    .addBox(-15.0f, -3.0f, -1.0f, 16.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-7.0f, -7.5f, -6.7143f, 0.0f, 1.5708f, 0.0f)
            )

            val Crown_r8 = body.addOrReplaceChild(
                "Crown_r8",
                CubeListBuilder.create().texOffs(56, 125)
                    .addBox(-15.0f, -3.0f, -1.0f, 16.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-7.0f, -7.5f, 7.2857f, 0.0f, 3.1416f, 0.0f)
            )

            val Crown_r9 = body.addOrReplaceChild(
                "Crown_r9",
                CubeListBuilder.create().texOffs(56, 125)
                    .addBox(-15.0f, -3.0f, -1.0f, 16.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(7.0f, -7.5f, 7.2857f, 0.0f, -1.5708f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }

}