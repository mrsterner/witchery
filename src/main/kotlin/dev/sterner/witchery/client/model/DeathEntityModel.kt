package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.entity.DeathEntity
import net.minecraft.client.model.ArmedModel
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
import net.minecraft.world.entity.HumanoidArm
import java.util.function.Function


class DeathEntityModel(val root: ModelPart) :
    HierarchicalModel<DeathEntity>(Function { location: ResourceLocation ->
        RenderType.entityTranslucent(
            location
        )
    }), ArmedModel {

    private val head: ModelPart = root.getChild("head")
    private val hood: ModelPart = head.getChild("hood")
    private val body: ModelPart = root.getChild("body")
    private val robe: ModelPart = body.getChild("robe")
    private val leftArm: ModelPart = body.getChild("lArm")
    private val rightArm: ModelPart = body.getChild("rArm")

    override fun setupAnim(
        entity: DeathEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        head.xRot = headPitch * 0.017453292f
        head.yRot = netHeadYaw * 0.017453292f

        val idleAmplitude = 0.05f
        this.leftArm.xRot = Mth.sin(ageInTicks * 0.067f) * idleAmplitude

        this.rightArm.xRot = -0.4f + Mth.sin(ageInTicks * 0.067f) * (idleAmplitude * 0.5f)

        this.rightArm.zRot = 0.1f
        this.leftArm.zRot = -0.1f
        this.rightArm.yRot = 0.0f
        this.leftArm.yRot = 0.0f

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
    }

    override fun root(): ModelPart? {
        return root
    }

    override fun translateToHand(
        side: HumanoidArm,
        poseStack: PoseStack
    ) {
        val modelPart: ModelPart = rightArm
        poseStack.translate(-0.0, 0.8, -0.0)
        modelPart.translateAndRotate(poseStack)
        poseStack.translate(-0.0, -0.2, -0.0)
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("death"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 18)
                    .addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 5.0f, 0.0f)
            )

            head.addOrReplaceChild(
                "hood",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.5f, -4.5f, -4.5f, 9.0f, 9.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -4.0f, 0.0f)
            )

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(32, 14)
                    .addBox(-3.5f, -6.2348f, -3.1736f, 7.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 11.2348f, 1.1736f)
            )

            body.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(3, 2)
                    .addBox(-0.5f, 0.0f, 0.0f, 1.0f, 4.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.7652f, 0.8264f, 0.1745f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "robe",
                CubeListBuilder.create().texOffs(27, 29)
                    .addBox(-4.5f, -1.0f, -5.0f, 9.0f, 13.0f, 5.0f, CubeDeformation(-0.01f)),
                PartPose.offsetAndRotation(0.0f, -5.7348f, 1.3264f, 0.0873f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "lArm",
                CubeListBuilder.create().texOffs(56, 0)
                    .addBox(0.0f, -0.5f, -1.0f, 2.0f, 9.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(1, 47).addBox(-0.5f, -1.0f, -1.5f, 3.0f, 8.0f, 3.0f, CubeDeformation(-0.15f)),
                PartPose.offset(4.0f, -5.2348f, -1.1736f)
            )

            body.addOrReplaceChild(
                "rArm",
                CubeListBuilder.create().texOffs(56, 0).mirror()
                    .addBox(-2.0f, -0.5f, -1.0f, 2.0f, 9.0f, 2.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(1, 47).mirror().addBox(-2.5f, -1.0f, -1.5f, 3.0f, 8.0f, 3.0f, CubeDeformation(-0.05f))
                    .mirror(false),
                PartPose.offset(-4.0f, -5.2348f, -1.1736f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}