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

class JarModel(modelPart: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    private val group: ModelPart = modelPart.getChild("group")

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        group.render(poseStack, buffer, packedLight, packedOverlay, color)
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(Witchery.id("jar"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root

            val group = partDefinition.addOrReplaceChild(
                "group",
                CubeListBuilder.create().texOffs(0, 2)
                    .addBox(-2.0f, 2.0f, -2.0f, 4.0f, 6.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(8, 12).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(8, 12).mirror().addBox(-1.0f, 0.0f, -1.0f, 2.0f, 1.0f, 2.0f, CubeDeformation(0.1f))
                    .mirror(false),
                PartPose.offset(0.0f, 16.0f, 0.0f)
            )

            return LayerDefinition.create(meshDefinition, 16, 16)
        }
    }
}