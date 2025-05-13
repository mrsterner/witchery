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


class MushroomLogModel(var root: ModelPart) :
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
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(Witchery.id("mushroom_log"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val bone = partdefinition.addOrReplaceChild(
                "bone",
                CubeListBuilder.create().texOffs(0, 50)
                    .addBox(-16.0f, -16.0f, -8.0f, 16.0f, 16.0f, 16.0f, CubeDeformation(0.0f))
                    .texOffs(0, 50).addBox(-16.0f, -16.0f, 8.0f, 16.0f, 16.0f, 16.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-16.5f, -16.5f, -8.5f, 17.0f, 17.0f, 33.0f, CubeDeformation(0.0f)),
                PartPose.offset(8.0f, 24.0f, -8.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}