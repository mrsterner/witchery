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


class BroomEntityModel(root: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    private val bone8: ModelPart = root.getChild("bone8")
    private val bone7: ModelPart = bone8.getChild("bone7")
    private val shaft: ModelPart = bone7.getChild("shaft")
    private val bone: ModelPart = shaft.getChild("bone")
    private val bone2: ModelPart = bone.getChild("bone2")
    private val bone3: ModelPart = bone2.getChild("bone3")
    private val bone4: ModelPart = bone3.getChild("bone4")
    private val bone5: ModelPart = bone4.getChild("bone5")
    private val bone6: ModelPart = bone5.getChild("bone6")


    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("broom"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val bone8 = partdefinition.addOrReplaceChild(
                "bone8",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -2.0f, -4.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.01f)),
                PartPose.offset(0.0f, 20.0f, -13.0f)
            )

            val bone7 = bone8.addOrReplaceChild(
                "bone7",
                CubeListBuilder.create().texOffs(32, 15)
                    .addBox(-1.0f, 0.0f, 0.0f, 2.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -2.0f, -2.0f, -0.4363f, 0.0f, 0.0f)
            )

            val shaft = bone7.addOrReplaceChild(
                "shaft",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -2.0f, 0.0f, 2.0f, 2.0f, 9.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(0.0f, 2.0f, 6.0f, 0.6109f, 0.0f, 0.0f)
            )

            val bone = shaft.addOrReplaceChild(
                "bone",
                CubeListBuilder.create().texOffs(13, 0)
                    .addBox(-1.0f, 0.0f, 0.0f, 2.0f, 2.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -2.0f, 9.0f, -0.3491f, 0.0f, 0.0f)
            )

            val bone2 = bone.addOrReplaceChild(
                "bone2",
                CubeListBuilder.create().texOffs(0, 4)
                    .addBox(-1.0f, -2.0f, 0.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(0.0f, 2.0f, 7.0f, 0.1745f, 0.0f, 0.0f)
            )

            val bone3 = bone2.addOrReplaceChild(
                "bone3",
                CubeListBuilder.create().texOffs(24, 0)
                    .addBox(-2.5f, -2.5f, -1.0f, 3.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.0f, 0.0f, 3.0f)
            )

            val bone4 = bone3.addOrReplaceChild(
                "bone4",
                CubeListBuilder.create().texOffs(22, 23)
                    .addBox(-3.0f, -3.0f, 1.0f, 4.0f, 4.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val bone5 = bone4.addOrReplaceChild(
                "bone5",
                CubeListBuilder.create().texOffs(0, 25)
                    .addBox(-2.5f, -2.5f, -1.0f, 3.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 4.0f)
            )

            val bone6 = bone5.addOrReplaceChild(
                "bone6",
                CubeListBuilder.create().texOffs(0, 11)
                    .addBox(-3.0f, -3.0f, 0.0f, 4.0f, 4.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val cube_r1 = bone6.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(12, 15)
                    .addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-3.0f, -1.0f, 2.0f, 0.0f, -0.1745f, 0.0f)
            )

            val cube_r2 = bone6.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(12, 15)
                    .addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-3.0f, -1.0f, 0.0f, 0.0f, -0.3054f, 0.0f)
            )

            val cube_r3 = bone6.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(16, 9)
                    .addBox(-3.0f, 0.0f, 0.0f, 4.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.0f, 3.0f, 0.1745f, 0.0f, 0.0f)
            )

            val cube_r4 = bone6.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(16, 9)
                    .addBox(-3.0f, 0.0f, 0.0f, 4.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.0f, 0.0f, 0.3054f, 0.0f, 0.0f)
            )

            val cube_r5 = bone6.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(48, 9)
                    .addBox(0.0f, -4.0f, -4.0f, 0.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.0f, -1.0f, 5.0f, 0.0f, 0.0f, 0.7854f)
            )

            val cube_r6 = bone6.addOrReplaceChild(
                "cube_r6",
                CubeListBuilder.create().texOffs(48, 9)
                    .addBox(0.0f, -4.0f, -4.0f, 0.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.0f, -1.0f, 5.0f, 0.0f, 0.0f, -0.7854f)
            )

            val cube_r7 = bone6.addOrReplaceChild(
                "cube_r7",
                CubeListBuilder.create().texOffs(0, 15)
                    .addBox(0.0f, -3.0f, 0.0f, 0.0f, 4.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(1.0f, 0.0f, 2.0f, 0.0f, 0.1745f, 0.0f)
            )

            val cube_r8 = bone6.addOrReplaceChild(
                "cube_r8",
                CubeListBuilder.create().texOffs(0, 15)
                    .addBox(0.0f, -3.0f, 0.0f, 0.0f, 4.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(1.0f, 0.0f, 0.0f, 0.0f, 0.3054f, 0.0f)
            )

            val cube_r9 = bone6.addOrReplaceChild(
                "cube_r9",
                CubeListBuilder.create().texOffs(8, 11)
                    .addBox(-3.0f, 0.0f, 0.0f, 4.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 1.0f, 2.0f, -0.1745f, 0.0f, 0.0f)
            )

            val cube_r10 = bone6.addOrReplaceChild(
                "cube_r10",
                CubeListBuilder.create().texOffs(8, 11)
                    .addBox(-3.0f, 0.0f, 0.0f, 4.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 1.0f, 0.0f, -0.3054f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        bone8.render(poseStack, buffer, packedLight, packedOverlay, color)
    }
}