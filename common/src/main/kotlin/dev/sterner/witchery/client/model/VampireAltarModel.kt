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

class VampireAltarModel(var root: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    var bone = root.getChild("bone")

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay,color)
    }

    companion object {
       val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("vampire_altar"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val bone = partdefinition.addOrReplaceChild(
                "bone",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-11.0f, -3.0f, -1.0f, 12.0f, 3.0f, 12.0f, CubeDeformation(0.0f))
                    .texOffs(0, 27).addBox(-9.0f, -7.0f, 1.0f, 8.0f, 4.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(0, 15).addBox(-10.0f, -9.0f, 0.0f, 10.0f, 2.0f, 10.0f, CubeDeformation(0.0f))
                    .texOffs(36, 0).addBox(-9.0f, -14.0f, -1.0f, 8.0f, 5.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-1.0f, -14.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(5.0f, 24.0f, -5.0f)
            )

            val cube_r1 = bone.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -2.5f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-10.0f, -11.5f, 0.0f, 0.0f, 1.5708f, 0.0f)
            )

            val cube_r2 = bone.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -5.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-10.0f, -9.0f, 10.0f, 0.0f, 3.1416f, 0.0f)
            )

            val cube_r3 = bone.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -2.5f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -11.5f, 10.0f, 0.0f, -1.5708f, 0.0f)
            )

            val cube_r4 = bone.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(36, 0)
                    .addBox(-7.0f, -5.0f, -1.0f, 8.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-10.0f, -9.0f, 2.0f, 0.0f, 1.5708f, 0.0f)
            )

            val cube_r5 = bone.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(36, 0)
                    .addBox(-4.0f, -2.5f, -1.0f, 8.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -11.5f, 5.0f, 0.0f, -1.5708f, 0.0f)
            )

            val cube_r6 = bone.addOrReplaceChild(
                "cube_r6",
                CubeListBuilder.create().texOffs(36, 0)
                    .addBox(-4.0f, -2.5f, -1.0f, 8.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-5.0f, -11.5f, 10.0f, 0.0f, 3.1416f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}