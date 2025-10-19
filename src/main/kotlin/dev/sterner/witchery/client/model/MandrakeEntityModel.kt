package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.entity.MandrakeEntity
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.util.Mth


class MandrakeEntityModel(root: ModelPart) : EntityModel<MandrakeEntity>() {
    private val head: ModelPart = root.getChild("head")
    private val arms: ModelPart = root.getChild("arms")
    private val legs: ModelPart = root.getChild("legs")
    private val rightLeg: ModelPart = legs.getChild("rightLeg")
    private val leftLeg: ModelPart = legs.getChild("leftLeg")
    private val bb_main: ModelPart = root.getChild("bb_main")

    override fun setupAnim(
        entity: MandrakeEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        head.xRot = headPitch * (Math.PI / 180.0).toFloat()
        head.zRot = netHeadYaw * (Math.PI / 180.0).toFloat() + Mth.sin(ageInTicks * 0.1f) * 0.1f

        rightLeg.xRot = Mth.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount
        leftLeg.xRot = Mth.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount
        arms.zRot = Mth.sin(ageInTicks * 0.1f) * 0.05f
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        head.render(poseStack, buffer, packedLight, packedOverlay, color)
        arms.render(poseStack, buffer, packedLight, packedOverlay, color)
        legs.render(poseStack, buffer, packedLight, packedOverlay, color)
        bb_main.render(poseStack, buffer, packedLight, packedOverlay, color)
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("mandrake"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(16, 12)
                    .addBox(-2.0f, -1.0f, -2.0f, 4.0f, 2.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 15.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "arms",
                CubeListBuilder.create().texOffs(16, 22)
                    .addBox(-5.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(8, 20).addBox(3.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 18.0f, 0.0f)
            )

            val legs =
                partdefinition.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))

            legs.addOrReplaceChild(
                "rightLeg",
                CubeListBuilder.create().texOffs(16, 18)
                    .addBox(-1.0f, 0.0f, -1.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.0f, -2.0f, 0.0f)
            )

            legs.addOrReplaceChild(
                "leftLeg",
                CubeListBuilder.create().texOffs(0, 20)
                    .addBox(-1.0f, 0.0f, -1.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.0f, -2.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, -8.0f, -3.0f, 6.0f, 6.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(0, 12).addBox(-4.0f, -18.0f, 0.0f, 8.0f, 8.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }


}