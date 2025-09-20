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


class HuntsmanSpearModel(root: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {
    private val bone: ModelPart = root.getChild("bone")


    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id( "spear"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val bone = partdefinition.addOrReplaceChild(
                "bone",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(1.0f, 0.0f, -1.0f, 2.0f, 28.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(6, 0).addBox(1.0f, -8.0f, -1.0f, 2.0f, 8.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(10, 9).addBox(3.0f, -5.0f, -1.0f, 1.0f, 5.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(12, 4).addBox(4.0f, -2.0f, -1.0f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(12, 0).addBox(-1.0f, -2.0f, -1.0f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(6, 9).addBox(0.0f, -5.0f, -1.0f, 1.0f, 5.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.0f, -4.0f, 0.5f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}