package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.OwlEntity
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
import java.util.function.Function
import kotlin.math.sin


class OwlEntityModel(val root: ModelPart) :
    HierarchicalModel<OwlEntity>(Function { location: ResourceLocation ->
        RenderType.entityTranslucent(
            location
        )
    }) {

    private val head: ModelPart = root.getChild("head")
    private val body: ModelPart = root.getChild("body")
    private val tail: ModelPart = body.getChild("tail")
    private val leftWing: ModelPart = body.getChild("leftWing")
    private val rightWing: ModelPart = body.getChild("rightWing")
    private val leftWing1: ModelPart = leftWing.getChild("leftWing1")
    private val rightWing1: ModelPart = rightWing.getChild("rightWing1")
    private val leftLeg: ModelPart = body.getChild("leftLeg")
    private val rightLeg: ModelPart = body.getChild("rightLeg")
    private val folded = body.getChild("folded")

    override fun setupAnim(
        entity: OwlEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        head.xRot = headPitch * 0.017453292f
        head.yRot = netHeadYaw * 0.017453292f

        if (entity.isFlying) {
            folded.visible = false
            leftWing.visible = true
            rightWing.visible = true

            val wingFlapSpeed = 0.7f // Speed of wing flapping
            val wingFlapAmplitude = 0.4f // Intensity of wing flapping

            leftWing.zRot = (-0.05f + sin((ageInTicks * wingFlapSpeed).toDouble()) * wingFlapAmplitude).toFloat()
            rightWing.zRot = (0.05f - sin((ageInTicks * wingFlapSpeed).toDouble()) * wingFlapAmplitude).toFloat()

            leftWing1.yRot = (-0.03f + sin((ageInTicks * wingFlapSpeed).toDouble()) * wingFlapAmplitude).toFloat()
            rightWing1.yRot = (0.03f - sin((ageInTicks * wingFlapSpeed).toDouble()) * wingFlapAmplitude).toFloat()
        } else {
            folded.visible = true
            leftWing.visible = false
            rightWing.visible = false
        }
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {

        super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, color)
    }

    override fun root(): ModelPart {
        return this.root
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("little_owl"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, -4.0f, -3.0f, 6.0f, 4.0f, 5.0f, CubeDeformation(-0.01f)),
                PartPose.offset(0.0f, 17.0f, -2.0f)
            )

            head.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 9)
                    .addBox(-0.5f, 0.0f, 0.5f, 1.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.0f, -3.0f, -0.6545f, 0.0f, 0.0f)
            )

            val ears = head.addOrReplaceChild(
                "ears",
                CubeListBuilder.create().texOffs(22, 4)
                    .addBox(2.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(22, 4).mirror().addBox(-6.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offset(1.0f, -4.0f, -3.0f)
            )

            ears.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(0, 19).mirror()
                    .addBox(-2.0f, -3.0f, 0.0f, 2.0f, 3.0f, 2.0f, CubeDeformation(-0.02f)).mirror(false)
                    .texOffs(0, 19).addBox(2.0f, -3.0f, 0.0f, 2.0f, 3.0f, 2.0f, CubeDeformation(-0.02f)),
                PartPose.offsetAndRotation(-2.0f, 0.0f, 0.0f, -0.6981f, 0.0f, 0.0f)
            )

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 9)
                    .addBox(-2.0f, -4.0f, -6.0f, 4.0f, 4.0f, 6.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(0.0f, 22.0f, -1.0f, -0.8727f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "folded",
                CubeListBuilder.create().texOffs(14, 15).mirror()
                    .addBox(-3.0f, -1.0f, -1.0f, 1.0f, 3.0f, 6.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(14, 15).addBox(2.0f, -1.0f, -1.0f, 1.0f, 3.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.0f, -3.0f, 0.1309f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(14, 9)
                    .addBox(-3.0f, 0.0f, -1.0f, 4.0f, 1.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(1.0f, -4.0f, 0.0f, 0.8727f, 0.0f, 0.0f)
            )

            val leftWing = body.addOrReplaceChild(
                "leftWing",
                CubeListBuilder.create().texOffs(0, 26)
                    .addBox(0.0f, -1.0f, -1.0f, 5.0f, 5.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.0f, -3.0f, -4.0f, 1.5708f, 0.0f, 0.0f)
            )

            leftWing.addOrReplaceChild(
                "leftWing1",
                CubeListBuilder.create().texOffs(20, 26)
                    .addBox(0.0f, -1.0f, -1.0f, 5.0f, 5.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(5.0f, 0.0f, 0.0f, 0.0f, -0.0436f, 0.0f)
            )

            val rightWing = body.addOrReplaceChild(
                "rightWing",
                CubeListBuilder.create().texOffs(0, 26).mirror()
                    .addBox(-5.0f, -1.0f, -1.0f, 5.0f, 5.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-2.0f, -3.0f, -4.0f, 1.5708f, 0.0f, 0.0f)
            )

            rightWing.addOrReplaceChild(
                "rightWing1",
                CubeListBuilder.create().texOffs(20, 26).mirror()
                    .addBox(-5.0f, -1.0f, -1.0f, 5.0f, 5.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(-5.0f, 0.0f, 0.0f)
            )

            val leftLeg = body.addOrReplaceChild(
                "leftLeg",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(28, 0).addBox(-1.0f, -2.0f, 1.0f, 1.0f, 4.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.0f, 0.0f, 0.0f, 0.8727f, 0.0f, 0.0f)
            )

            leftLeg.addOrReplaceChild(
                "leftFoot",
                CubeListBuilder.create().texOffs(17, 0)
                    .addBox(0.5f, -0.8f, -1.81f, 2.0f, 1.0f, 3.0f, CubeDeformation(-0.2f)),
                PartPose.offset(-2.0f, 2.0f, 0.0f)
            )

            val rightLeg = body.addOrReplaceChild(
                "rightLeg",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, -1.0f, 0.0f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(28, 0).addBox(0.0f, -2.0f, 1.0f, 1.0f, 4.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.0f, 0.0f, 0.0f, 0.8727f, 0.0f, 0.0f)
            )

            rightLeg.addOrReplaceChild(
                "rightFoot",
                CubeListBuilder.create().texOffs(17, 0)
                    .addBox(0.5f, -0.8f, -1.81f, 2.0f, 1.0f, 3.0f, CubeDeformation(-0.2f)),
                PartPose.offset(-1.0f, 2.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}