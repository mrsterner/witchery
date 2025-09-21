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


class SpinningWheelWheelBlockEntityModel(root: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    private val wheel: ModelPart = root.getChild("wheel")


    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        wheel.render(poseStack, buffer, packedLight, packedOverlay, color)
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("wheel"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val wheel = partdefinition.addOrReplaceChild(
                "wheel",
                CubeListBuilder.create().texOffs(20, 8)
                    .addBox(-1.0f, 5.3f, -3.0f, 2.0f, 2.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(25, 25).addBox(-0.5f, 0.5f, -0.5f, 1.0f, 5.0f, 1.0f, CubeDeformation(-0.01f))
                    .texOffs(20, 18).addBox(-1.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val bone2 = wheel.addOrReplaceChild(
                "bone2",
                CubeListBuilder.create().texOffs(20, 0)
                    .addBox(-1.0f, -2.0f, 0.0f, 2.0f, 2.0f, 6.0f, CubeDeformation(-0.01f))
                    .texOffs(0, 16).addBox(-0.5f, -7.0f, 2.5f, 1.0f, 5.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 7.3f, 3.0f, 0.7854f, 0.0f, 0.0f)
            )

            val bone3 = bone2.addOrReplaceChild(
                "bone3",
                CubeListBuilder.create().texOffs(10, 18)
                    .addBox(-1.0f, -2.0f, 0.0f, 2.0f, 2.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(4, 24).addBox(-0.5f, -7.0f, 2.5f, 1.0f, 5.0f, 1.0f, CubeDeformation(-0.01f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.7854f, 0.0f, 0.0f)
            )

            val bone4 = bone3.addOrReplaceChild(
                "bone4",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-1.0f, -2.0f, 0.0f, 2.0f, 2.0f, 6.0f, CubeDeformation(-0.01f))
                    .texOffs(10, 0).addBox(-0.5f, -7.0f, 2.5f, 1.0f, 5.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.7854f, 0.0f, 0.0f)
            )

            val bone5 = bone4.addOrReplaceChild(
                "bone5",
                CubeListBuilder.create().texOffs(10, 10)
                    .addBox(-1.0f, -2.0f, 0.0f, 2.0f, 2.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(0, 24).addBox(-0.5f, -7.0f, 2.5f, 1.0f, 5.0f, 1.0f, CubeDeformation(-0.01f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.7854f, 0.0f, 0.0f)
            )

            val bone6 = bone5.addOrReplaceChild(
                "bone6",
                CubeListBuilder.create().texOffs(10, 2)
                    .addBox(-1.0f, -2.0f, 0.0f, 2.0f, 2.0f, 6.0f, CubeDeformation(-0.01f))
                    .texOffs(0, 0).addBox(-0.5f, -7.0f, 2.5f, 1.0f, 5.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.7854f, 0.0f, 0.0f)
            )

            val bone7 = bone6.addOrReplaceChild(
                "bone7",
                CubeListBuilder.create().texOffs(0, 8)
                    .addBox(-1.0f, -2.0f, 0.0f, 2.0f, 2.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(20, 0).addBox(-0.5f, -7.0f, 2.5f, 1.0f, 5.0f, 1.0f, CubeDeformation(-0.01f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.7854f, 0.0f, 0.0f)
            )

            bone7.addOrReplaceChild(
                "bone8",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -2.0f, 0.0f, 2.0f, 2.0f, 6.0f, CubeDeformation(-0.01f))
                    .texOffs(0, 8).addBox(-0.5f, -7.0f, 2.5f, 1.0f, 5.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.7854f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }

    }

}