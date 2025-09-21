package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import net.minecraft.client.model.Model
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


class DreamWeaverBlockEntityModel(modelPart: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    private val weaver: ModelPart = modelPart.getChild("weaver")

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        weaver.render(poseStack, buffer, packedLight, packedOverlay, color)
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("dreamcatcher"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val weaver = partdefinition.addOrReplaceChild(
                "weaver",
                CubeListBuilder.create().texOffs(20, 24)
                    .addBox(-1.0f, -4.0f, 0.0f, 1.0f, 4.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(5.0f, 16.0f, -8.0f)
            )

            weaver.addOrReplaceChild(
                "dreads",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-8.8f, -6.0f, 0.5f, 8.0f, 8.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(0, 8).addBox(-9.8f, 0.0f, 0.5f, 10.0f, 9.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val group3 = weaver.addOrReplaceChild(
                "group3",
                CubeListBuilder.create().texOffs(16, 16)
                    .addBox(-1.0f, -4.0f, 0.0f, 1.0f, 4.0f, 1.0f, CubeDeformation(-0.01f)),
                PartPose.offsetAndRotation(0.0f, -4.0f, 0.0f, 0.0f, 0.0f, -0.7854f)
            )

            val group = group3.addOrReplaceChild(
                "group",
                CubeListBuilder.create().texOffs(23, 24)
                    .addBox(-1.0f, -4.0f, 0.0f, 1.0f, 4.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -4.0f, 0.0f, 0.0f, 0.0f, -0.7854f)
            )

            val group4 = group.addOrReplaceChild(
                "group4",
                CubeListBuilder.create().texOffs(27, 24)
                    .addBox(-1.0f, -4.0f, 0.0f, 1.0f, 4.0f, 1.0f, CubeDeformation(-0.01f)),
                PartPose.offsetAndRotation(0.0f, -4.0f, 0.0f, 0.0f, 0.0f, -0.7854f)
            )

            val group5 = group4.addOrReplaceChild(
                "group5",
                CubeListBuilder.create().texOffs(18, 24)
                    .addBox(-1.0f, -4.0f, 0.0f, 1.0f, 4.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -4.0f, 0.0f, 0.0f, 0.0f, -0.7854f)
            )

            val group6 = group5.addOrReplaceChild(
                "group6",
                CubeListBuilder.create().texOffs(23, 18)
                    .addBox(-1.0f, -4.0f, 0.0f, 1.0f, 4.0f, 1.0f, CubeDeformation(-0.01f)),
                PartPose.offsetAndRotation(0.0f, -4.0f, 0.0f, 0.0f, 0.0f, -0.7854f)
            )

            val group7 = group6.addOrReplaceChild(
                "group7",
                CubeListBuilder.create().texOffs(20, 16)
                    .addBox(-1.0f, -4.0f, 0.0f, 1.0f, 4.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -4.0f, 0.0f, 0.0f, 0.0f, -0.7854f)
            )

            group7.addOrReplaceChild(
                "group8",
                CubeListBuilder.create().texOffs(23, 16)
                    .addBox(-1.0f, -4.0f, 0.0f, 1.0f, 4.0f, 1.0f, CubeDeformation(-0.01f)),
                PartPose.offsetAndRotation(0.0f, -4.0f, 0.0f, 0.0f, 0.0f, -0.7854f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}