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


class SpiritPortalPortalModel(root: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    val poertal = root.getChild("poertal")

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        poertal.render(poseStack, buffer, packedLight, packedOverlay)
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("spirit_door_portal"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val poertal = partdefinition.addOrReplaceChild(
                "poertal",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-30.0f, -6.0f, -1.0f, 24.0f, 24.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(2, 0).addBox(-29.0f, -7.0f, -1.0f, 22.0f, 1.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(4, 0).addBox(-28.0f, -8.0f, -1.0f, 20.0f, 1.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(8, 0).addBox(-26.0f, -9.0f, -1.0f, 16.0f, 1.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(10, 0).addBox(-25.0f, -10.0f, -1.0f, 14.0f, 1.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(14, 0).addBox(-23.0f, -11.0f, -1.0f, 10.0f, 1.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(16, 0).addBox(-22.0f, -12.0f, -1.0f, 8.0f, 1.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(20, 0).addBox(-20.0f, -13.0f, -1.0f, 4.0f, 1.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(22, 0).addBox(-19.0f, -14.0f, -1.0f, 2.0f, 1.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(23, 0).addBox(-6.0f, -6.0f, -1.0f, 1.0f, 22.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(23, 0).addBox(-31.0f, -6.0f, -1.0f, 1.0f, 22.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(23, 0).addBox(-5.0f, -5.0f, -1.0f, 1.0f, 18.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(23, 0).addBox(-32.0f, -5.0f, -1.0f, 1.0f, 18.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(23, 0).addBox(-4.0f, -4.0f, -1.0f, 1.0f, 14.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(23, 0).addBox(-33.0f, -4.0f, -1.0f, 1.0f, 14.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(23, 0).addBox(-3.0f, -4.0f, -1.0f, 1.0f, 11.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(23, 0).addBox(-34.0f, -4.0f, -1.0f, 1.0f, 11.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(23, 0).addBox(-2.0f, -3.0f, -1.0f, 1.0f, 7.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(23, 0).addBox(-35.0f, -3.0f, -1.0f, 1.0f, 7.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(23, 0).addBox(-1.0f, -2.0f, -1.0f, 1.0f, 3.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(23, 0).addBox(-36.0f, -2.0f, -1.0f, 1.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(18.0f, 6.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}