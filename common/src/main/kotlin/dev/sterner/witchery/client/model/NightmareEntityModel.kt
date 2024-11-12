package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.NightmareEntity
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


class NightmareEntityModel(root: ModelPart) : EntityModel<NightmareEntity>() {

    private val head: ModelPart = root.getChild("head")
    private val body: ModelPart = root.getChild("body")
    private val rightArm: ModelPart = root.getChild("rightArm")
    private val leftArm: ModelPart = root.getChild("leftArm")

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    override fun setupAnim(
        entity: NightmareEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        body.yRot = cos(ageInTicks * 0.1f) * 0.025f
        body.xRot = sin(ageInTicks * 0.1f) * 0.025f

        rightArm.xRot = -sin(ageInTicks * 0.1f) * 0.1f
        rightArm.zRot = cos(ageInTicks * 0.1f) * 0.05f
        rightArm.zRot += 3.14f / 24.0f

        leftArm.xRot = cos(ageInTicks * 0.1f) * 0.1f
        leftArm.zRot = sin(ageInTicks * 0.1f) * 0.05f
        leftArm.zRot -= 3.14f / 24.0f

        head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        head.xRot = headPitch * (Math.PI.toFloat() / 180f)

        head.yRot += cos(ageInTicks * 0.05f) * 0.02f
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("nightmare"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(16, 33).addBox(-6.0f, -11.0f, -1.0f, 2.0f, 7.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(32, 0).addBox(4.0f, -11.0f, -1.0f, 2.0f, 7.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 4.0f, 0.0f)
            )

            val rightArm = partdefinition.addOrReplaceChild(
                "rightArm",
                CubeListBuilder.create().texOffs(28, 16)
                    .addBox(-2.0f, -1.0f, -1.0f, 2.0f, 15.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-4.0f, 5.0f, 0.0f)
            )

            val leftArm = partdefinition.addOrReplaceChild(
                "leftArm",
                CubeListBuilder.create().texOffs(20, 16)
                    .addBox(0.0f, -1.0f, -1.0f, 2.0f, 15.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(4.0f, 5.0f, 0.0f)
            )

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 30)
                    .addBox(-4.0f, 12.0f, 0.0f, 8.0f, 7.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(0, 16).addBox(-4.0f, 0.0f, -1.0f, 8.0f, 12.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 4.0f, 0.0f)
            )


            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}