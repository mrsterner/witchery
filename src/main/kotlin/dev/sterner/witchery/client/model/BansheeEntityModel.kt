package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.entity.BansheeEntity
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
import java.util.function.Function


class BansheeEntityModel(val root: ModelPart) :
    HierarchicalModel<BansheeEntity>(Function { location: ResourceLocation ->
        RenderType.entityTranslucent(
            location
        )
    }) {

    val body: ModelPart = root.getChild("body")
    val head: ModelPart = body.getChild("head")
    val headMain: ModelPart = head.getChild("headMain")
    val jaw: ModelPart = headMain.getChild("jaw")
    val lArm: ModelPart = body.getChild("lArm")
    val rArm: ModelPart = body.getChild("rArm")

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

    override fun setupAnim(
        entity: BansheeEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {

        head.xRot = headPitch * 0.017453292f
        head.yRot = netHeadYaw * 0.017453292f

        head.zRot = Mth.sin(ageInTicks * 0.05f) * 0.05f
        head.xRot += Mth.sin(ageInTicks * 0.1f) * 0.03f

        jaw.xRot = 0.6f + Mth.sin(ageInTicks * 0.15f) * 0.1f

        lArm.xRot = -1.0f + Mth.cos(ageInTicks * 0.1f) * 0.1f
        lArm.zRot = 0.6f + Mth.sin(ageInTicks * 0.1f) * 0.05f
        rArm.xRot = -1.0f + Mth.cos(ageInTicks * 0.1f + Math.PI.toFloat()) * 0.1f
        rArm.zRot = -0.6f - Mth.sin(ageInTicks * 0.1f) * 0.05f

        body.xRot = Mth.sin(ageInTicks * 0.05f) * 0.02f
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("banshee"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 43)
                    .addBox(-5.0f, 5.0f, 0.0f, 10.0f, 13.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(0, 30).addBox(-4.0f, -2.0f, 0.0f, 8.0f, 7.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.0f, -4.0f, 0.1745f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(70, 43)
                    .addBox(-5.0f, 0.0f, 0.0f, 10.0f, 13.0f, 8.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(0.0f, 18.0f, 0.0f, 0.6109f, 0.0f, 0.0f)
            )

            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 23)
                    .addBox(-3.0f, -2.0f, -3.0f, 6.0f, 4.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val headMain = head.addOrReplaceChild(
                "headMain",
                CubeListBuilder.create().texOffs(73, 0)
                    .addBox(-4.0f, -5.5f, -8.0f, 8.0f, 11.0f, 8.0f, CubeDeformation(0.5f))
                    .texOffs(0, 0).addBox(-4.0f, -5.0f, -8.0f, 8.0f, 6.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(32, 0).addBox(-4.0f, 0.9f, -8.0f, 8.0f, 2.0f, 8.0f, CubeDeformation(-0.01f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, -3.0f, -0.2618f, 0.0f, 0.0f)
            )

            headMain.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create().texOffs(24, 10)
                    .addBox(-4.0f, 0.0f, -8.0f, 8.0f, 2.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(48, 12).addBox(-4.0f, -1.9f, -8.0f, 8.0f, 2.0f, 8.0f, CubeDeformation(-0.02f)),
                PartPose.offsetAndRotation(0.0f, 1.0f, 0.0f, 1.1781f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "rArm",
                CubeListBuilder.create().texOffs(64, 30)
                    .addBox(-3.0f, -2.0f, -2.0f, 3.0f, 17.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-4.0f, 5.0f, 3.0f, -0.6981f, 0.8727f, 0.0f)
            )

            body.addOrReplaceChild(
                "lArm",
                CubeListBuilder.create().texOffs(64, 30)
                    .addBox(0.0f, -2.0f, -2.0f, 3.0f, 17.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.0f, 6.0f, 3.0f, -0.6981f, -0.8727f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 64)
        }
    }
}