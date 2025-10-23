package dev.sterner.witchery.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.entity.ImpEntity
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
import kotlin.math.cos
import kotlin.math.min


class ImpEntityModel(modelPart: ModelPart) :
    HierarchicalModel<ImpEntity>(Function { location: ResourceLocation ->
        RenderType.entityTranslucent(
            location
        )
    }),
    ArmedModel {

    private val root: ModelPart = modelPart.getChild("root")
    private val head: ModelPart = root.getChild("head")
    private val body: ModelPart = root.getChild("body")
    private val right_arm: ModelPart = body.getChild("right_arm")
    private val left_arm: ModelPart = body.getChild("left_arm")
    private val right_wing: ModelPart = body.getChild("right_wing")
    private val left_wing: ModelPart = body.getChild("left_wing")
    private val tail: ModelPart = body.getChild("tail")
    private val right_horn2: ModelPart = head.getChild("right_horn2")
    private val right_horn: ModelPart = head.getChild("right_horn")
    private val bone: ModelPart = right_horn.getChild("bone")
    private val bone2: ModelPart = right_horn2.getChild("bone2")


    override fun root(): ModelPart {
        return this.root
    }

    override fun setupAnim(
        entity: ImpEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        root().allParts.forEach { obj: ModelPart -> obj.resetPose() }
        val f = ageInTicks * 20.0f * 0.017453292f + limbSwing
        val g = Mth.cos(f) * 3.1415927f * 0.15f + limbSwingAmount
        ageInTicks - entity.tickCount.toFloat()
        val i = ageInTicks * 9.0f * 0.017453292f
        val j = min((limbSwingAmount / 0.3f).toDouble(), 1.0).toFloat()
        val k = 1.0f - j
        val l = 0
        var m: Float
        var n: Float
        var o: Float


        head.xRot = headPitch * 0.017453292f
        head.yRot = netHeadYaw * 0.017453292f

        right_wing.xRot = -0.2f * (1.0f - j)
        right_wing.yRot = -0.7853982f + g
        left_wing.xRot = -0.2f * (1.0f - j)
        left_wing.yRot = 0.7853982f - g

        body.xRot = j * 0.7853982f
        m = l * Mth.lerp(j, -1.0471976f, -1.134464f)
        val var10000 = this.root
        var10000.y += cos(i.toDouble()).toFloat() * 0.25f * k
        right_arm.xRot = m
        left_arm.xRot = m
        n = k * (1.0f - l)
        o = 0.43633232f - Mth.cos(i + 4.712389f) * 3.1415927f * 0.075f * n
        left_arm.zRot = -o
        right_arm.zRot = o
        right_arm.yRot = 0.27925268f * l
        left_arm.yRot = -0.27925268f * l
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("imp"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val root =
                partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))

            val head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-2.5f, 0.0f, -2.5f, 5.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val right_horn2 = head.addOrReplaceChild(
                "right_horn2",
                CubeListBuilder.create().texOffs(16, 26)
                    .addBox(-1.0f, -1.0f, -1.0f, 2.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.0f, 5.0f, 0.0f, -0.0873f, 0.0f, -0.3054f)
            )

            val bone2 =
                right_horn2.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))

            bone2.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(10, 23)
                    .addBox(-1.0f, 0.0f, -1.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(-0.1f)),
                PartPose.offsetAndRotation(0.0f, 1.7f, 0.0f, 0.1745f, 0.0f, 0.0f)
            )

            val right_horn = head.addOrReplaceChild(
                "right_horn",
                CubeListBuilder.create().texOffs(16, 26)
                    .addBox(-1.0f, -1.0f, -1.0f, 2.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.0f, 5.0f, 0.0f, -0.0873f, 0.0f, 0.3054f)
            )

            val bone = right_horn.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))

            bone.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(10, 23)
                    .addBox(-1.0f, 0.0f, -1.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(-0.1f)),
                PartPose.offsetAndRotation(0.0f, 1.7f, 0.0f, 0.1745f, 0.0f, 0.0f)
            )

            val body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(16, 17)
                    .addBox(-1.5f, -4.0f, -1.0f, 3.0f, 4.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(16, 10).addBox(-1.5f, -5.0f, -1.0f, 3.0f, 5.0f, 2.0f, CubeDeformation(-0.2f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(20, 0)
                    .addBox(0.0f, -3.0f, -1.0f, 1.0f, 4.0f, 2.0f, CubeDeformation(-0.01f)),
                PartPose.offset(1.45f, -0.5f, 0.0f)
            )

            body.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(0, 20)
                    .addBox(-1.0f, -3.0f, -1.0f, 1.0f, 4.0f, 2.0f, CubeDeformation(-0.01f)),
                PartPose.offset(-1.5f, -0.5f, 0.0f)
            )

            body.addOrReplaceChild(
                "right_wing",
                CubeListBuilder.create().texOffs(0, 7)
                    .addBox(-0.5f, -6.0f, -0.6f, 0.0f, 5.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.5f, 0.0f, 0.6f)
            )

            body.addOrReplaceChild(
                "left_wing",
                CubeListBuilder.create().texOffs(0, 2)
                    .addBox(-0.5f, -6.0f, -0.6f, 0.0f, 5.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.5f, 0.0f, 0.6f)
            )

            body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(0, 22)
                    .addBox(1.0f, -1.0f, 0.0f, 0.0f, 4.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.0f, -3.0f, 1.0f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }

    }

    override fun translateToHand(side: HumanoidArm, poseStack: PoseStack) {
        this.root.translateAndRotate(poseStack)
        body.translateAndRotate(poseStack)
        poseStack.translate(0.0f, 0.0625f, 0.1875f)
        poseStack.mulPose(Axis.XP.rotation(right_arm.xRot))
        poseStack.scale(0.7f, 0.7f, 0.7f)
        poseStack.translate(0.0625f, 0.0f, 0.0f)
    }
}