package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.DemonEntity
import dev.sterner.witchery.entity.EntEntity
import net.minecraft.client.model.ArmedModel
import net.minecraft.client.model.EntityModel
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
import net.minecraft.world.entity.HumanoidArm
import java.util.function.Function

class EntEntityModel(val root: ModelPart) :
    HierarchicalModel<EntEntity>(Function { location: ResourceLocation ->
        RenderType.entityTranslucent(
            location
        )
    }),
    ArmedModel {

    private val right_leg: ModelPart = root.getChild("right_leg")
    private val left_arm: ModelPart = root.getChild("left_arm")
    private val right_arm: ModelPart = root.getChild("right_arm")
    private val body: ModelPart = root.getChild("body")
    private val left_leg: ModelPart = root.getChild("left_leg")

    override fun setupAnim(
        entity: EntEntity?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
    }

    override fun renderToBuffer(
        poseStack: PoseStack?,
        vertexConsumer: VertexConsumer?,
        packedLight: Int,
        packedOverlay: Int,
        color: Int,
    ) {
        right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay)
        left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay)
        right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay)
        left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay)
    }

    override fun root(): ModelPart {
        return root
    }


    override fun translateToHand(side: HumanoidArm, poseStack: PoseStack) {

    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("ent"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val right_leg = partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(32, 64)
                    .addBox(-7.0f, -16.0f, -1.0f, 8.0f, 16.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.0f, 24.0f, -3.0f)
            )

            val left_arm = partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(64, 0)
                    .addBox(8.0f, -36.0f, -4.0f, 8.0f, 32.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(120, 122).addBox(16.0f, -30.0f, 0.0f, 4.0f, 6.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(64, 125).addBox(8.0f, -38.0f, -4.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val Crown_r1 = left_arm.addOrReplaceChild(
                "Crown_r1",
                CubeListBuilder.create().texOffs(64, 125)
                    .addBox(-7.0f, -2.0f, -1.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(9.0f, -36.0f, -3.0f, 0.0f, 1.5708f, 0.0f)
            )

            val Crown_r2 = left_arm.addOrReplaceChild(
                "Crown_r2",
                CubeListBuilder.create().texOffs(64, 125)
                    .addBox(-7.0f, -2.0f, -1.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(9.0f, -36.0f, 3.0f, 0.0f, 3.1416f, 0.0f)
            )

            val Crown_r3 = left_arm.addOrReplaceChild(
                "Crown_r3",
                CubeListBuilder.create().texOffs(64, 125)
                    .addBox(-7.0f, -2.0f, -1.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(15.0f, -36.0f, 3.0f, 0.0f, -1.5708f, 0.0f)
            )

            val right_arm = partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(112, 121)
                    .addBox(-20.0f, -31.0f, -1.0f, 4.0f, 7.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(64, 125).addBox(-16.0f, -38.0f, -4.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(0, 64).addBox(-16.0f, -36.0f, -4.0f, 8.0f, 32.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val Crown_r4 = right_arm.addOrReplaceChild(
                "Crown_r4",
                CubeListBuilder.create().texOffs(64, 125)
                    .addBox(-7.0f, -2.0f, -1.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-15.0f, -36.0f, 3.0f, 0.0f, 3.1416f, 0.0f)
            )

            val Crown_r5 = right_arm.addOrReplaceChild(
                "Crown_r5",
                CubeListBuilder.create().texOffs(64, 125)
                    .addBox(-7.0f, -2.0f, -1.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-15.0f, -36.0f, -3.0f, 0.0f, 1.5708f, 0.0f)
            )

            val Crown_r6 = right_arm.addOrReplaceChild(
                "Crown_r6",
                CubeListBuilder.create().texOffs(64, 125)
                    .addBox(-7.0f, -2.0f, -1.0f, 8.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-9.0f, -36.0f, 3.0f, 0.0f, -1.5708f, 0.0f)
            )

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-8.0f, -64.0f, -8.0f, 16.0f, 48.0f, 16.0f, CubeDeformation(0.0f))
                    .texOffs(88, 119).addBox(-14.0f, -55.0f, -1.0f, 6.0f, 9.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(100, 118).addBox(8.0f, -48.0f, -1.0f, 6.0f, 10.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(56, 125).addBox(-8.0f, -67.0f, -8.0f, 16.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val Crown_r7 = body.addOrReplaceChild(
                "Crown_r7",
                CubeListBuilder.create().texOffs(56, 125)
                    .addBox(-15.0f, -3.0f, -1.0f, 16.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-7.0f, -64.0f, -7.0f, 0.0f, 1.5708f, 0.0f)
            )

            val Crown_r8 = body.addOrReplaceChild(
                "Crown_r8",
                CubeListBuilder.create().texOffs(56, 125)
                    .addBox(-15.0f, -3.0f, -1.0f, 16.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-7.0f, -64.0f, 7.0f, 0.0f, 3.1416f, 0.0f)
            )

            val Crown_r9 = body.addOrReplaceChild(
                "Crown_r9",
                CubeListBuilder.create().texOffs(56, 125)
                    .addBox(-15.0f, -3.0f, -1.0f, 16.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(7.0f, -64.0f, 7.0f, 0.0f, -1.5708f, 0.0f)
            )

            val left_leg = partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(64, 40)
                    .addBox(0.0f, -16.0f, -4.0f, 8.0f, 16.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }

}