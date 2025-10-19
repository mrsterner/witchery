package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.entity.ParasiticLouseEntity
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
import kotlin.math.abs

class ParasiticLouseEntityModel(val root: ModelPart) :
    HierarchicalModel<ParasiticLouseEntity>(Function { location: ResourceLocation ->
        RenderType.entityTranslucent(
            location
        )
    }) {

    private val bone: ModelPart = root.getChild("bone")
    private val bone3: ModelPart = bone.getChild("bone3")
    private val bone4: ModelPart = bone3.getChild("bone4")
    private val bone2: ModelPart = bone.getChild("bone2")
    private val bodyParts: Array<ModelPart?> = arrayOfNulls(3)

    init {
        this.bodyParts[0] = bone3
        this.bodyParts[1] = bone4
        this.bodyParts[2] = bone2
    }

    override fun setupAnim(
        entity: ParasiticLouseEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        for (i in this.bodyParts.indices) {
            this.bodyParts[i]!!.yRot =
                Mth.cos(ageInTicks * 0.9f + i.toFloat() * 0.15f * 3.1415927f) * 3.1415927f * 0.01f * ((1 + abs((i - 2).toDouble())).toFloat())
            this.bodyParts[i]!!.x =
                Mth.sin(ageInTicks * 0.9f + i.toFloat() * 0.15f * 3.1415927f) * 3.1415927f * 0.1f * (abs((i - 2).toDouble()).toFloat())
        }
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    override fun root(): ModelPart {
        return root
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("leech"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val bone = partdefinition.addOrReplaceChild(
                "bone",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, -4.0f, -3.0f, 6.0f, 4.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, -2.0f)
            )

            val bone3 = bone.addOrReplaceChild(
                "bone3",
                CubeListBuilder.create().texOffs(0, 21)
                    .addBox(-2.0f, -1.0f, 0.0f, 4.0f, 3.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -2.0f, 2.0f)
            )

            bone3.addOrReplaceChild(
                "bone4",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-1.0f, -1.0f, 1.0f, 2.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 1.0f, 4.0f)
            )

            bone.addOrReplaceChild(
                "bone2",
                CubeListBuilder.create().texOffs(15, 14)
                    .addBox(-1.5f, -1.0f, -2.0f, 3.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -2.0f, -3.0f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}