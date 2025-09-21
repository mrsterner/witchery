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

class AltarBlockEntityModel(modelPart: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    private val main: ModelPart = modelPart.getChild("main")

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        main.render(poseStack, buffer, packedLight, packedOverlay, color)
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("altar"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshDefinition: MeshDefinition = MeshDefinition()
            val partDefinition: PartDefinition = meshDefinition.root

            partDefinition.addOrReplaceChild(
                "main",
                CubeListBuilder.create().texOffs(0, 35)
                    .addBox(-24.0f, -3.0f, -8.0f, 48.0f, 3.0f, 32.0f, CubeDeformation(0.0f))
                    .texOffs(0, 70).addBox(-22.0f, -13.0f, -6.0f, 44.0f, 10.0f, 28.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-24.0f, -16.0f, -8.0f, 48.0f, 3.0f, 32.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshDefinition, 256, 256)
        }
    }
}