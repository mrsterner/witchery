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


class SpiritPortalBlockEntityModel(root: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {

    val lDoor = root.getChild("lDoor")
    val rDoor = root.getChild("rDoor")

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        lDoor.render(poseStack, buffer, packedLight, packedOverlay)
        rDoor.render(poseStack, buffer, packedLight, packedOverlay)
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("spirit_door"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val lDoor = partdefinition.addOrReplaceChild(
                "lDoor",
                CubeListBuilder.create().texOffs(0, 34)
                    .addBox(0.0f, -32.0f, -1.0f, 16.0f, 32.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-16.0f, 24.0f, 0.0f)
            )

            val rDoor = partdefinition.addOrReplaceChild(
                "rDoor",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-16.0f, -32.0f, -1.0f, 16.0f, 32.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(16.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}