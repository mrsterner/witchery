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


class CoffinModel(root: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    val bone: ModelPart = root.getChild("bone")
    val top: ModelPart = bone.getChild("top")
    val base: ModelPart = bone.getChild("base")

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("coffin"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val bone =
                partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(1.0f, 14.0f, -22.0f))

            val top = bone.addOrReplaceChild(
                "top",
                CubeListBuilder.create().texOffs(0, 35)
                    .addBox(0.0f, -1.0f, -15.0f, 14.0f, 1.0f, 30.0f, CubeDeformation(0.0f))
                    .texOffs(64, 0).addBox(1.0f, -2.0f, -14.0f, 12.0f, 1.0f, 28.0f, CubeDeformation(0.0f))
                    .texOffs(58, 35).addBox(1.0f, -0.5f, -14.0f, 12.0f, 1.0f, 28.0f, CubeDeformation(0.0f)),
                PartPose.offset(-8.0f, -1.0f, 14.0f)
            )

            val base = bone.addOrReplaceChild(
                "base",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-16.0f, 5.0f, 0.0f, 16.0f, 3.0f, 32.0f, CubeDeformation(0.0f))
                    .texOffs(0, 66).addBox(-2.0f, -2.0f, 1.5f, 1.0f, 7.0f, 29.0f, CubeDeformation(0.0f))
                    .texOffs(0, 66).addBox(-15.0f, -2.0f, 1.5f, 1.0f, 7.0f, 29.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-14.5f, -2.0f, 1.0f, 13.0f, 7.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-14.5f, -2.0f, 30.0f, 13.0f, 7.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(61, 64).addBox(-13.5f, 4.0f, 2.5f, 11.0f, 1.0f, 27.0f, CubeDeformation(0.0f))
                    .texOffs(31, 92).addBox(-2.5f, -2.5f, 1.5f, 1.0f, 7.0f, 29.0f, CubeDeformation(0.0f))
                    .texOffs(31, 92).addBox(-14.5f, -2.5f, 1.5f, 1.0f, 7.0f, 29.0f, CubeDeformation(0.0f))
                    .texOffs(0, 35).addBox(-13.5f, -2.5f, 1.5f, 11.0f, 7.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 35).addBox(-13.5f, -2.5f, 29.5f, 11.0f, 7.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(64, 29).addBox(-16.0f, -3.0f, 0.0f, 16.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(64, 29).addBox(-16.0f, -3.0f, 30.0f, 16.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 66).addBox(-2.0f, -3.0f, 2.0f, 2.0f, 1.0f, 28.0f, CubeDeformation(0.0f))
                    .texOffs(0, 66).addBox(-16.0f, -3.0f, 2.0f, 2.0f, 1.0f, 28.0f, CubeDeformation(0.0f)),
                PartPose.offset(7.0f, 2.0f, -2.0f)
            )

            val detailsNorth = base.addOrReplaceChild(
                "detailsNorth",
                CubeListBuilder.create().texOffs(0, 15).mirror()
                    .addBox(-14.5f, -3.0f, 6.5f, 13.0f, 2.0f, 1.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(5, 22).addBox(-5.5f, -5.0f, 6.5f, 2.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(5, 22).mirror().addBox(-12.5f, -5.0f, 6.5f, 2.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(24, 22).addBox(-1.5f, -7.0f, 6.5f, 1.0f, 7.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(27, 17).addBox(-8.5f, -1.0f, 6.5f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(12, 26).addBox(-8.5f, -6.0f, 6.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(20, 22).addBox(-15.5f, -7.0f, 6.5f, 1.0f, 7.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 20).addBox(-14.5f, -7.0f, 6.5f, 13.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 5.0f, -6.0f)
            )

            val detailsSouth = base.addOrReplaceChild(
                "detailsSouth",
                CubeListBuilder.create().texOffs(0, 15)
                    .addBox(-14.5f, -3.0f, 6.5f, 13.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(5, 22).addBox(-5.5f, -5.0f, 6.5f, 2.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(5, 22).addBox(-12.5f, -5.0f, 6.5f, 2.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(20, 22).addBox(-1.5f, -7.0f, 6.5f, 1.0f, 7.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(27, 13).addBox(-8.5f, -1.0f, 6.5f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(8, 26).addBox(-8.5f, -6.0f, 6.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(16, 22).addBox(-15.5f, -7.0f, 6.5f, 1.0f, 7.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 20).addBox(-14.5f, -7.0f, 6.5f, 13.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 5.0f, 24.0f)
            )

            val detailWest = base.addOrReplaceChild(
                "detailWest",
                CubeListBuilder.create().texOffs(16, 36)
                    .addBox(-1.5f, -7.0f, 7.5f, 1.0f, 1.0f, 29.0f, CubeDeformation(0.0f))
                    .texOffs(3, 3).addBox(-1.5f, -3.0f, 7.5f, 1.0f, 2.0f, 29.0f, CubeDeformation(0.0f))
                    .texOffs(0, 22).addBox(-1.5f, -6.0f, 20.5f, 1.0f, 3.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(18, 26).addBox(-1.5f, -1.0f, 20.5f, 1.0f, 1.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(24, 8).addBox(-1.5f, -6.0f, 29.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(27, 11).addBox(-1.5f, -1.0f, 29.5f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(24, 8).addBox(-1.5f, -6.0f, 13.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(27, 7).addBox(-1.5f, -1.0f, 13.5f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 5.0f, -6.0f)
            )

            val detailEast = base.addOrReplaceChild(
                "detailEast",
                CubeListBuilder.create().texOffs(16, 36)
                    .addBox(-1.5f, -7.0f, 7.5f, 1.0f, 1.0f, 29.0f, CubeDeformation(0.0f))
                    .texOffs(3, 3).mirror().addBox(-1.5f, -3.0f, 7.5f, 1.0f, 2.0f, 29.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(0, 22).addBox(-1.5f, -6.0f, 20.5f, 1.0f, 3.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(15, 24).addBox(-1.5f, -1.0f, 20.5f, 1.0f, 1.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(24, 8).mirror().addBox(-1.5f, -6.0f, 29.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(24, 12).addBox(-1.5f, -1.0f, 29.5f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(24, 8).mirror().addBox(-1.5f, -6.0f, 13.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(24, 12).addBox(-1.5f, -1.0f, 13.5f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(-14.0f, 5.0f, -6.0f)
            )

            return LayerDefinition.create(meshdefinition, 256, 256)
        }

    }


    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        bone.render(poseStack, buffer, packedLight, packedOverlay)
    }
}