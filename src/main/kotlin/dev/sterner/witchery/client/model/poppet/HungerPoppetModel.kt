package dev.sterner.witchery.client.model.poppet

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


class HungerPoppetModel(root: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    private val base: ModelPart = root.getChild("base")
    private val nail: ModelPart = root.getChild("nail")
    private val death: ModelPart = root.getChild("death")

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        base.render(poseStack, buffer, packedLight, packedOverlay)
        nail.render(poseStack, buffer, packedLight, packedOverlay)
        death.render(poseStack, buffer, packedLight, packedOverlay)
    }


    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("hunger_poppet"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root


            val base =
                partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(2.5f, 21.0f, 7.0f))

            val rArm = base.addOrReplaceChild(
                "rArm",
                CubeListBuilder.create().texOffs(14, 14)
                    .addBox(-0.8f, -1.2f, -1.5f, 2.0f, 4.0f, 2.0f, CubeDeformation(-0.2f)),
                PartPose.offsetAndRotation(-5.0f, -4.0f, 0.5f, -0.0436f, 0.0f, 0.0873f)
            )

            val lArm = base.addOrReplaceChild(
                "lArm",
                CubeListBuilder.create().texOffs(8, 10)
                    .addBox(-0.2f, -1.2f, -1.5f, 2.0f, 4.0f, 2.0f, CubeDeformation(-0.2f)),
                PartPose.offsetAndRotation(-1.0f, -4.0f, 0.5f, -0.0436f, 0.0f, -0.0873f)
            )

            val body = base.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 6)
                    .addBox(-2.0f, -6.0f, -1.0f, 3.0f, 4.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.0f, 1.0f, 0.0f)
            )

            val head = base.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.5f, -3.0f, -1.5f, 3.0f, 3.0f, 3.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(-2.5f, -5.0f, 0.0f, 0.1745f, 0.0f, 0.0f)
            )

            val lLeg = base.addOrReplaceChild(
                "lLeg",
                CubeListBuilder.create().texOffs(10, 4)
                    .addBox(-0.8f, -0.2f, -1.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(-0.201f)),
                PartPose.offsetAndRotation(-2.0f, -1.0f, 0.0f, 0.0f, 0.0f, -0.1309f)
            )

            val rLeg = base.addOrReplaceChild(
                "rLeg",
                CubeListBuilder.create().texOffs(0, 12)
                    .addBox(-1.2f, -0.2f, -1.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(-0.2f)),
                PartPose.offsetAndRotation(-3.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.1309f)
            )

            val nail = partdefinition.addOrReplaceChild(
                "nail",
                CubeListBuilder.create().texOffs(0, 18)
                    .addBox(0.0f, -5.0f, -3.0f, 1.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 19).addBox(-0.5f, -5.5f, -4.0f, 2.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(-0.5f, 22.5f, 7.0f)
            )

            val death = partdefinition.addOrReplaceChild(
                "death",
                CubeListBuilder.create().texOffs(0, 26)
                    .addBox(-1.5f, -1.5f, -1.5f, 3.0f, 3.0f, 3.0f, CubeDeformation(0.2f))
                    .texOffs(1, 27).addBox(-0.1f, -2.7f, -0.5f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 14.5f, 6.7f, 0.1745f, 0.0f, 0.0f)
            )

            val cube_r1 = death.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(18, -7)
                    .addBox(1.0f, -7.0f, -1.0f, 0.0f, 7.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(1.4f, 5.0f, 2.0f, 1.789f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}