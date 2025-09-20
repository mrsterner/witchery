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


class WerewolfAltarModel(root: ModelPart) :
    Model(Function { location: ResourceLocation ->
        RenderType.entitySolid(
            location
        )
    }) {
    private val body: ModelPart = root.getChild("body")
    private val bb_main: ModelPart = root.getChild("bb_main")

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("werewolf_altar"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 39)
                    .addBox(-4.5f, 5.0f, -2.0f, 9.0f, 10.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 23).addBox(-5.0f, -5.0f, -3.0f, 10.0f, 10.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.5f, 0.0f, 0.6109f, 0.0f, 0.0f)
            )

            val cube_r1 = body.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(64, 23)
                    .addBox(-4.0f, 1.0f, 0.0f, 8.0f, 10.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0873f, 0.0f, 0.0f)
            )

            val cube_r2 = body.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(92, 23)
                    .addBox(-4.5f, -3.0f, 0.0f, 9.0f, 10.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.3927f, 0.0f, 0.0f)
            )

            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(56, 39)
                    .addBox(-1.5f, -1.0f, -1.5f, 3.0f, 13.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 15.0f, 2.0f, -0.3491f, 0.0f, 0.0f)
            )

            val headjoint = body.addOrReplaceChild(
                "headjoint",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, -5.25f, -2.0f, -0.2618f, 0.0f, 0.0f)
            )

            val head = headjoint.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.5f, -4.0f, -4.0f, 7.0f, 7.0f, 7.0f, CubeDeformation(0.0f))
                    .texOffs(0, 14).addBox(-3.5f, 3.0f, -4.0f, 7.0f, 2.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val cube_r3 = head.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(82, 0)
                    .addBox(-3.5f, 0.25f, 0.9f, 7.0f, 7.0f, 4.0f, CubeDeformation(0.1f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 1.0036f, 0.0f, 0.0f)
            )

            val cube_r4 = head.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(21, 0).mirror()
                    .addBox(-5.25f, -2.0f, -4.0f, 4.0f, 7.0f, 0.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.6545f, 0.0f)
            )

            val cube_r5 = head.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(21, 0)
                    .addBox(1.25f, -2.0f, -4.0f, 4.0f, 7.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, -0.6545f, 0.0f)
            )

            val cube_r6 = head.addOrReplaceChild(
                "cube_r6",
                CubeListBuilder.create().texOffs(41, 0).mirror()
                    .addBox(-3.5f, -4.75f, -3.25f, 2.0f, 2.0f, 3.0f, CubeDeformation(0.2f)).mirror(false)
                    .texOffs(41, 0).addBox(1.5f, -4.75f, -3.25f, 2.0f, 2.0f, 3.0f, CubeDeformation(0.2f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.3054f, 0.0f, 0.0f)
            )

            val top_snout = head.addOrReplaceChild(
                "top_snout",
                CubeListBuilder.create().texOffs(28, 2)
                    .addBox(-1.5f, -2.0f, -5.0f, 3.0f, 2.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(46, 2).addBox(-1.5f, -0.5f, -5.0f, 3.0f, 2.0f, 5.0f, CubeDeformation(-0.15f)),
                PartPose.offset(0.0f, 0.0f, -4.0f)
            )

            val bottom_snout = head.addOrReplaceChild(
                "bottom_snout",
                CubeListBuilder.create().texOffs(23, 9)
                    .addBox(-2.0f, 0.0f, -5.0f, 3.0f, 2.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(41, 9).addBox(-2.0f, -1.5f, -5.0f, 3.0f, 2.0f, 5.0f, CubeDeformation(-0.1f))
                    .texOffs(28, 16).addBox(-2.0f, 2.0f, -5.0f, 3.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.5f, 0.0f, -4.0f)
            )

            val right_ear =
                head.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.offset(-3.5f, -1.0f, -1.0f))

            val cube_r7 = right_ear.addOrReplaceChild(
                "cube_r7",
                CubeListBuilder.create().texOffs(54, 4)
                    .addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.7854f, -0.6109f, 0.0f)
            )

            val left_ear =
                head.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.offset(3.5f, -1.0f, -1.0f))

            val cube_r8 = left_ear.addOrReplaceChild(
                "cube_r8",
                CubeListBuilder.create().texOffs(54, 4)
                    .addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.7854f, 0.6109f, 0.0f)
            )

            val right_arm = body.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(28, 41)
                    .addBox(-3.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, CubeDeformation(0.01f))
                    .texOffs(42, 39).addBox(-3.0f, 10.0f, -2.0f, 3.0f, 3.0f, 4.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(-4.5f, -1.0f, 0.0f, -1.5708f, 0.3054f, -0.4363f)
            )

            val right_arm_2 = right_arm.addOrReplaceChild(
                "right_arm_2",
                CubeListBuilder.create().texOffs(18, 57)
                    .addBox(-0.5f, -3.0f, -3.0f, 5.0f, 14.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.5f, 10.0f, 0.0f, -0.0436f, -0.0873f, -0.5672f)
            )

            val left_arm = body.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(28, 41).mirror()
                    .addBox(0.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, CubeDeformation(0.01f)).mirror(false)
                    .texOffs(42, 39).mirror().addBox(0.0f, 10.0f, -2.0f, 3.0f, 3.0f, 4.0f, CubeDeformation(0.01f))
                    .mirror(false),
                PartPose.offsetAndRotation(4.5f, -1.0f, 0.0f, -0.5672f, -0.1745f, 0.0f)
            )

            val left_arm_2 = left_arm.addOrReplaceChild(
                "left_arm_2",
                CubeListBuilder.create().texOffs(18, 57).mirror()
                    .addBox(-4.5f, -3.0f, -3.0f, 5.0f, 14.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(1.5f, 10.0f, 0.0f, 0.0f, 0.0f, 0.48f)
            )

            val right_leg = body.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(-2.0f, 11.75f, 1.0f, -1.2654f, 0.1745f, 0.0f)
            )

            val cube_r9 = right_leg.addOrReplaceChild(
                "cube_r9",
                CubeListBuilder.create().texOffs(0, 54)
                    .addBox(-3.0f, 2.0f, -2.0f, 4.0f, 8.0f, 5.0f, CubeDeformation(0.1f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.2618f, 0.0f, 0.0f)
            )

            val right_leg_2 = right_leg.addOrReplaceChild(
                "right_leg_2",
                CubeListBuilder.create().texOffs(0, 71)
                    .addBox(-3.0f, -2.0f, -2.0f, 4.0f, 14.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 6.0f, 3.0f, -0.4363f, 0.0f, 0.0f)
            )

            val left_leg = body.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(2.0f, 11.75f, 1.0f, -1.1781f, -0.2182f, 0.0f)
            )

            val cube_r10 = left_leg.addOrReplaceChild(
                "cube_r10",
                CubeListBuilder.create().texOffs(0, 54).mirror()
                    .addBox(-1.0f, 2.0f, -2.0f, 4.0f, 8.0f, 5.0f, CubeDeformation(0.1f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.2618f, 0.0f, 0.0f)
            )

            val left_leg_2 = left_leg.addOrReplaceChild(
                "left_leg_2",
                CubeListBuilder.create().texOffs(0, 71).mirror()
                    .addBox(-1.0f, -4.0f, -2.0f, 4.0f, 14.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 8.0f, 1.0f, -0.48f, 0.0f, 0.0f)
            )

            val bb_main = partdefinition.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create().texOffs(0, 90)
                    .addBox(-8.0f, -6.0f, -8.0f, 16.0f, 6.0f, 16.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}