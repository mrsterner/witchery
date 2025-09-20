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


class SpinningWheelBlockEntityModel(root: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    val base: ModelPart = root.getChild("base")
    val string: ModelPart = root.getChild("string")


    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        base.render(poseStack, buffer, packedLight, packedOverlay, color)
        string.render(poseStack, buffer, packedLight, packedOverlay, color)
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("spinner"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val base = partdefinition.addOrReplaceChild(
                "base",
                CubeListBuilder.create().texOffs(-9, 12)
                    .addBox(-2.0f, -4.0f, -3.0f, 4.0f, 0.0f, 9.0f, CubeDeformation(0.0f))
                    .texOffs(0, 20).addBox(-1.1f, -14.0f, 4.0f, 0.0f, 10.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(4, 20).addBox(1.1f, -14.0f, 4.0f, 0.0f, 10.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val cube_r1 = base.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, 0.0f, -1.0f, 5.0f, 0.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.0f, -4.0f, -5.0f, 0.0f, 0.0f, 1.0472f)
            )

            val cube_r2 = base.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(10, 0)
                    .addBox(-5.0f, 0.0f, -1.0f, 5.0f, 0.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.0f, -4.0f, -5.0f, 0.0f, 0.0f, -1.0472f)
            )

            val pillar = base.addOrReplaceChild(
                "pillar",
                CubeListBuilder.create().texOffs(16, 15)
                    .addBox(-3.0f, -13.0f, -1.0f, 4.0f, 13.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.0f, 0.0f, -6.0f)
            )

            val string = partdefinition.addOrReplaceChild(
                "string",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 11.0f, -5.0f)
            )

            val cube_r3 = string.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -6.0f, 0.0f, 2.0f, 6.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.829f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }

    }

}