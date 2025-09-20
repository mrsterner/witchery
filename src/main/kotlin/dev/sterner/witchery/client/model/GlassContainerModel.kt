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


class GlassContainerModel(modelPart: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    private var main: ModelPart = modelPart.getChild("main")

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("glass_container"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root

            partDefinition.addOrReplaceChild(
                "main",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshDefinition, 32, 32)
        }
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        main.render(poseStack, buffer, packedLight, packedOverlay, color)
    }
}