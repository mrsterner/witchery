package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
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
import net.minecraft.world.entity.Mob
import java.util.function.Function


class BabaYagaEntityModel<T : Mob>(val root: ModelPart) :
    HierarchicalModel<T>(Function { location: ResourceLocation ->
        RenderType.entityTranslucent(
            location
        )
    }) {

    var holdingItem = false
    val head: ModelPart = root.getChild("head")
    val nose: ModelPart = head.getChild("nose")
    private val body: ModelPart = root.getChild("body")
    private val arms: ModelPart = root.getChild("arms")
    val rightLeg: ModelPart = root.getChild("right_leg")
    val leftLeg: ModelPart = root.getChild("left_leg")

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.head.yRot = netHeadYaw * (Math.PI / 180.0).toFloat()
        this.head.xRot = headPitch * (Math.PI / 180.0).toFloat()
        this.head.zRot = 0.0f

        this.nose.setPos(0.0f, -2.0f, 0.0f)
        val f = 0.01f * (entity.id % 10).toFloat()
        this.nose.xRot = Mth.sin(entity.tickCount.toFloat() * f) * 4.5f * (Math.PI / 180.0).toFloat()
        this.nose.yRot = 0f
        this.nose.zRot = Mth.cos(entity.tickCount.toFloat() * f) * 2.5f * (Math.PI / 180.0).toFloat()

        if (this.holdingItem) {
            this.nose.setPos(0.0f, 1.0f, -1.5f)
            this.nose.xRot = -0.9f
        }

        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount * 0.5f
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount * 0.5f
        this.rightLeg.yRot = 0.0f
        this.leftLeg.yRot = 0.0f
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        arms.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    override fun root(): ModelPart {
        return root
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(Witchery.id("baba_yaga"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            head.addOrReplaceChild(
                "nose",
                CubeListBuilder.create().texOffs(24, 0)
                    .addBox(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -2.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(16, 20)
                    .addBox(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, CubeDeformation(0.5f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "arms",
                CubeListBuilder.create().texOffs(40, 38)
                    .addBox(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(44, 22).addBox(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(44, 22).addBox(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 2.0f, 0.0f, -0.7854f, 0.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(0, 22).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                PartPose.offset(-2.0f, 12.0f, 0.0f)
            )
            partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                PartPose.offset(2.0f, 12.0f, 0.0f)
            )


            return LayerDefinition.create(meshdefinition, 64, 128)
        }
    }
}
