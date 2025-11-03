package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.entity.SpiritEntity
import net.minecraft.client.model.HierarchicalModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import java.util.function.Function


class SpiritEntityModel(val root: ModelPart) :
    HierarchicalModel<SpiritEntity>(Function { location: ResourceLocation ->
        RenderType.entityTranslucent(
            location
        )
    }) {

    val head: ModelPart = root.getChild("head")

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    override fun root(): ModelPart {
        return root
    }

    override fun setupAnim(
        entity: SpiritEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {

        head.xRot = headPitch * 0.017453292f
        head.yRot = netHeadYaw * 0.017453292f

        head.zRot = Mth.sin(ageInTicks * 0.05f) * 0.05f
        head.xRot += Mth.sin(ageInTicks * 0.1f) * 0.03f
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("spirit"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, 8.0f, -4.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}