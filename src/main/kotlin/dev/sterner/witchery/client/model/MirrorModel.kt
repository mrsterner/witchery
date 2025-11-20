package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import net.minecraft.client.model.EntityModel
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


class MirrorModel(root: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    val full: ModelPart = root.getChild("full")
    val frame: ModelPart = full.getChild("frame")
    val mirror: ModelPart = full.getChild("mirror")
    val single: ModelPart = root.getChild("single")
    val frame2: ModelPart = single.getChild("frame2")
    val mirror2: ModelPart = single.getChild("mirror2")

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {

    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("mirror_full"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val full =
                partdefinition.addOrReplaceChild("full", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))

            val frame = full.addOrReplaceChild(
                "frame",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-9.0f, -33.0f, -7.5f, 18.0f, 34.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val mirror = full.addOrReplaceChild(
                "mirror",
                CubeListBuilder.create().texOffs(0, 35)
                    .addBox(-7.0f, -31.0f, -8.0f, 14.0f, 30.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val single = partdefinition.addOrReplaceChild(
                "single",
                CubeListBuilder.create(),
                PartPose.offset(6.0f, 23.0f, -7.0f)
            )

            val frame2 = single.addOrReplaceChild(
                "frame2",
                CubeListBuilder.create().texOffs(35, 58)
                    .addBox(-15.0f, -16.0f, 0.0f, 18.0f, 18.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val mirror2 = single.addOrReplaceChild(
                "mirror2",
                CubeListBuilder.create().texOffs(35, 37)
                    .addBox(-13.0f, -14.0f, -1.0f, 14.0f, 14.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}