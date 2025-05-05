package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import java.util.function.Function


class ChainModel(root: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    val chain: ModelPart = root.getChild("chain")
    val overlay: ModelPart = root.getChild("overlay")

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        chain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        overlay.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("chain"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root

            partDefinition.addOrReplaceChild(
                "chain",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-7.0f, -3.0f, -1.0f, 10.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 8).addBox(-7.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(8, 8).addBox(1.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 4).addBox(-7.0f, 1.0f, -1.0f, 10.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.0f, 21.0f, 0.0f)
            )

            partDefinition.addOrReplaceChild(
                "overlay",
                CubeListBuilder.create().texOffs(0, 24)
                    .addBox(-9.0f, -6.0f, -1.0f, 10.0f, 6.0f, 2.0f, CubeDeformation(0.2f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )


            return LayerDefinition.create(meshDefinition, 32, 32)
        }
    }
}