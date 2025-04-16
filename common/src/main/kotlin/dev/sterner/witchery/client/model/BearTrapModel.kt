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


class BearTrapModel(root: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    private val bone: ModelPart = root.getChild("bone")
    private val leftClaw: ModelPart = bone.getChild("leftClaw")
    private val rightClaw: ModelPart = bone.getChild("rightClaw")
    private val crank: ModelPart = bone.getChild("crank")
    private val plate: ModelPart = bone.getChild("plate")

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        bone.render(poseStack, buffer, packedLight, packedOverlay, color)
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(Witchery.id("bear_trap"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val bone = partdefinition.addOrReplaceChild(
                "bone",
                CubeListBuilder.create().texOffs(0, 16).addBox(-13.0f, 1.0f, 7.0f, 14.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(8, 25).addBox(-1.0f, -1.0f, 7.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 25).addBox(-12.5f, -1.0f, 7.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(6.0f, 22.0f, -8.0f)
            )

            val leftClaw = bone.addOrReplaceChild(
                "leftClaw",
                CubeListBuilder.create().texOffs(28, 55)
                    .addBox(-5.5f, -1.001f, -6.0f, 11.0f, 3.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-6.0f, -1.0f, 8.0f, -1.3963f, 0.0f, 0.0f)
            )

            val rightClaw = bone.addOrReplaceChild(
                "rightClaw",
                CubeListBuilder.create().texOffs(0, 29)
                    .addBox(-5.5f, -1.001f, 0.0f, 11.0f, 3.0f, 6.0f, CubeDeformation(0.05f)),
                PartPose.offsetAndRotation(-6.0f, -1.0f, 8.0f, 1.3963f, 0.0f, 0.0f)
            )

            val crank = bone.addOrReplaceChild("crank", CubeListBuilder.create(), PartPose.offset(-12.0f, 1.5f, 8.0f))

            val cube_r1 = crank.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(20, 19)
                    .addBox(-1.1f, -5.0f, -1.0f, 2.0f, 6.0f, 2.0f, CubeDeformation(-0.1f)),
                PartPose.offsetAndRotation(0.0f, -0.5f, 0.0f, 0.0f, 0.0f, -0.2182f)
            )

            val plate = bone.addOrReplaceChild(
                "plate",
                CubeListBuilder.create().texOffs(0, 19).addBox(-8.5f, -0.5f, 5.5f, 5.0f, 1.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-6.5f, 0.0f, 7.5f, 1.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}