package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.SpectreEntity
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


class SpectreEntityModel(val root: ModelPart) :
    HierarchicalModel<SpectreEntity>(Function { location: ResourceLocation ->
        RenderType.entityTranslucent(
            location
        )
    }) {

    val body: ModelPart = root.getChild("body")
    val head: ModelPart = root.getChild("head")
    val lArm: ModelPart = root.getChild("lArm")
    val rArm: ModelPart = root.getChild("rArm")

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        lArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        rArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    override fun root(): ModelPart {
        return root
    }

    override fun setupAnim(
        entity: SpectreEntity,
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

        lArm.xRot = -1.0f + Mth.cos(ageInTicks * 0.1f) * 0.1f
        lArm.zRot = 0.6f + Mth.sin(ageInTicks * 0.1f) * 0.05f
        rArm.xRot = -1.0f + Mth.cos(ageInTicks * 0.1f + Math.PI.toFloat()) * 0.1f
        rArm.zRot = -0.6f - Mth.sin(ageInTicks * 0.1f) * 0.05f

        body.xRot = Mth.sin(ageInTicks * 0.05f) * 0.02f
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("spectre"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 1.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "rArm",
                CubeListBuilder.create().texOffs(0, 32)
                    .addBox(-3.0f, -3.0f, -10.0f, 4.0f, 4.0f, 12.0f, CubeDeformation(0.0f))
                    .texOffs(0, 40).addBox(1.0f, 1.0f, -10.0f, 0.0f, 4.0f, 12.0f, CubeDeformation(0.0f))
                    .texOffs(0, 36).addBox(-3.0f, 1.0f, -10.0f, 0.0f, 4.0f, 12.0f, CubeDeformation(0.0f))
                    .texOffs(0, 56).addBox(-3.0f, 1.0f, -8.0f, 4.0f, 4.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, 4.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "lArm",
                CubeListBuilder.create().texOffs(32, 32)
                    .addBox(-1.0f, -3.0f, -10.0f, 4.0f, 4.0f, 12.0f, CubeDeformation(0.0f))
                    .texOffs(32, 40).addBox(3.0f, 1.0f, -10.0f, 0.0f, 4.0f, 12.0f, CubeDeformation(0.0f))
                    .texOffs(32, 36).addBox(-1.0f, 1.0f, -10.0f, 0.0f, 4.0f, 12.0f, CubeDeformation(0.0f))
                    .texOffs(32, 56).addBox(-1.0f, 1.0f, -8.0f, 4.0f, 4.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(5.0f, 4.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(32, 0)
                    .addBox(-4.0f, -14.0f, -2.0f, 8.0f, 12.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(40, 20).addBox(4.0f, -2.0f, -2.0f, 0.0f, 8.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(32, 20).addBox(-4.0f, -2.0f, -2.0f, 0.0f, 8.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(36, 16).addBox(-4.0f, -2.0f, -2.0f, 8.0f, 8.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(0, 16).addBox(-4.0f, -2.0f, 2.0f, 8.0f, 8.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 15.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}