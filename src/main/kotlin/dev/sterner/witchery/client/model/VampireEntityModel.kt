package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.VampireEntity
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
import java.util.function.Function
import kotlin.math.cos
import kotlin.math.sin

class VampireEntityModel(val root: ModelPart) :
    HierarchicalModel<VampireEntity>(Function { location: ResourceLocation ->
        RenderType.entityTranslucent(
            location
        )
    }) {

    private var head: ModelPart = root.getChild("head")
    private var nose: ModelPart = head.getChild("nose")
    private var cape: ModelPart = root.getChild("cape")
    private var body: ModelPart = root.getChild("body")
    private var arms: ModelPart = root.getChild("arms")
    private var arms_rotation: ModelPart = arms.getChild("arms_rotation")
    private var arms_flipped: ModelPart = arms_rotation.getChild("arms_flipped")
    private var left_arm: ModelPart = root.getChild("left_arm")
    private var right_arm: ModelPart = root.getChild("right_arm")
    private var left_leg: ModelPart = root.getChild("left_leg")
    private var right_leg: ModelPart = root.getChild("right_leg")

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        cape.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        arms.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    override fun root(): ModelPart {
        return root
    }

    override fun setupAnim(
        entity: VampireEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        body.yRot = cos(ageInTicks * 0.1f) * 0.015f
        body.xRot = sin(ageInTicks * 0.1f) * 0.015f

        right_arm.xRot = sin(ageInTicks * 0.1f) * 0.1f
        right_arm.zRot = cos(ageInTicks * 0.1f) * 0.05f

        left_arm.xRot = cos(ageInTicks * 0.1f) * 0.1f
        left_arm.zRot = sin(ageInTicks * 0.1f) * 0.05f

        left_leg.xRot = cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount
        right_leg.xRot = cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount

        left_arm.xRot = +cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.2f * limbSwingAmount
        right_arm.xRot = +cos(limbSwing * 0.6662f) * 1.2f * limbSwingAmount

        head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        head.xRot = headPitch * (Math.PI.toFloat() / 180f)

        head.yRot += cos(ageInTicks * 0.05f) * 0.02f
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("vampire"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-6.0f, -4.0f, 0.0f, 2.0f, 2.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).mirror().addBox(4.0f, -4.0f, 0.0f, 2.0f, 2.0f, 0.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            head.addOrReplaceChild(
                "nose",
                CubeListBuilder.create().texOffs(24, 0)
                    .addBox(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -2.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "cape",
                CubeListBuilder.create().texOffs(36, 0)
                    .addBox(-5.0f, 0.0f, -0.5f, 10.0f, 16.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 3.5f, 0.1309f, 0.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(16, 20)
                    .addBox(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, CubeDeformation(0.25f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val arms =
                partdefinition.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0f, 3.5f, 0.3f))

            val arms_rotation = arms.addOrReplaceChild(
                "arms_rotation",
                CubeListBuilder.create().texOffs(44, 22)
                    .addBox(-8.0f, 0.0f, -2.05f, 4.0f, 8.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(40, 38).addBox(-4.0f, 4.0f, -2.05f, 8.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -2.0f, 0.05f, -0.7505f, 0.0f, 0.0f)
            )

            arms_rotation.addOrReplaceChild(
                "arms_flipped",
                CubeListBuilder.create().texOffs(44, 22).mirror()
                    .addBox(4.0f, -24.0f, -2.05f, 4.0f, 8.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(40, 46).mirror()
                    .addBox(-2.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(6.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(40, 46)
                    .addBox(-2.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-6.0f, 2.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(0, 22).mirror()
                    .addBox(2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(-2.0f, 12.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(0, 22)
                    .addBox(-6.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.0f, 12.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }


}