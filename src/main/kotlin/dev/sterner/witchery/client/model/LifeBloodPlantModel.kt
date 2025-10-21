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


class LifeBloodPlantModel(modelPart: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    val main: ModelPart = modelPart.getChild("main")
    val cross: ModelPart = main.getChild("cross")
    val seg1: ModelPart = cross.getChild("seg1")
    val seg2: ModelPart = cross.getChild("seg2")
    val glow: ModelPart = main.getChild("glow")
    val seg3: ModelPart = glow.getChild("seg3")
    val seg4: ModelPart = glow.getChild("seg4")


    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
    }


    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("life_blood"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val main =
                partdefinition.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0f, 16.0f, 0.0f))

            val cross = main.addOrReplaceChild("cross", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))

            val seg1 = cross.addOrReplaceChild(
                "seg1",
                CubeListBuilder.create().texOffs(32, 32)
                    .addBox(0.1f, -8.0f, -8.0f, 0.0f, 16.0f, 16.0f, CubeDeformation(0.0f))
                    .texOffs(0, 32).addBox(-0.1f, -8.0f, -8.0f, 0.0f, 16.0f, 16.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.7854f, 0.0f)
            )

            val seg2 = cross.addOrReplaceChild(
                "seg2",
                CubeListBuilder.create().texOffs(32, 16)
                    .addBox(0.1f, -8.0f, -8.0f, 0.0f, 16.0f, 16.0f, CubeDeformation(0.0f))
                    .texOffs(32, 0).addBox(-0.1f, -8.0f, -8.0f, 0.0f, 16.0f, 16.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, -0.7854f, 0.0f)
            )

            val glow = main.addOrReplaceChild("glow", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))

            val seg3 = glow.addOrReplaceChild(
                "seg3",
                CubeListBuilder.create().texOffs(32, -16)
                    .addBox(0.2f, -8.0f, -8.0f, 0.0f, 16.0f, 16.0f, CubeDeformation(0.0f))
                    .texOffs(0, 16).addBox(-0.2f, -8.0f, -8.0f, 0.0f, 16.0f, 16.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, -0.7854f, 0.0f)
            )

            val seg4 = glow.addOrReplaceChild(
                "seg4",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.2f, -8.0f, -8.0f, 0.0f, 16.0f, 16.0f, CubeDeformation(0.0f))
                    .texOffs(0, -16).addBox(-0.2f, -8.0f, -8.0f, 0.0f, 16.0f, 16.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.7854f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}