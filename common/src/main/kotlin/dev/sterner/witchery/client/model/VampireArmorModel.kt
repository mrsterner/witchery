package dev.sterner.witchery.client.model

import dev.sterner.witchery.Witchery
import net.minecraft.client.model.HumanoidArmorModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.world.entity.LivingEntity


class VampireArmorModel(val root: ModelPart) : HumanoidArmorModel<LivingEntity>(root) {

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(Witchery.id("vampplayer"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partDefinition = meshdefinition.root

            partDefinition.addOrReplaceChild(
                "hat",
                CubeListBuilder.create(), PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
            )


            val Head: PartDefinition = partDefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(56, 15)
                    .addBox(-5.0f, -9.0f, -5.0f, 10.0f, 3.0f, 10.0f, CubeDeformation(0.0f))
                    .texOffs(58, 28).addBox(-4.0f, -17.0f, -4.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val Body: PartDefinition = partDefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, CubeDeformation(0.3f))
                    .texOffs(0, 46).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, CubeDeformation(0.7f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val CoatTail = Body.addOrReplaceChild(
                "CoatTail",
                CubeListBuilder.create().texOffs(101, 40)
                    .addBox(-4.0f, 0.0f, 0.5f, 8.0f, 10.0f, 4.0f, CubeDeformation(0.4f)),
                PartPose.offset(0.0f, 12.5f, -2.5f)
            )

            val RightArm: PartDefinition = partDefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(24, 16)
                    .addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.3f)),
                PartPose.offset(-5.0f, 2.0f, 0.0f)
            )

            val LeftArm: PartDefinition = partDefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(24, 16).mirror()
                    .addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.3f)).mirror(false),
                PartPose.offset(5.0f, 2.0f, 0.0f)
            )

            val RightLeg: PartDefinition = partDefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(40, 16)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.255f))
                    .texOffs(24, 47).addBox(-2.0f, 9.0f, -2.0f, 4.0f, 3.0f, 4.0f, CubeDeformation(0.3f)),
                PartPose.offset(-1.9f, 12.0f, 0.0f)
            )

            val LeftLeg: PartDefinition = partDefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(40, 16).mirror()
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.255f)).mirror(false)
                    .texOffs(24, 47).mirror().addBox(-2.0f, 9.0f, -2.0f, 4.0f, 3.0f, 4.0f, CubeDeformation(0.3f))
                    .mirror(false),
                PartPose.offset(1.9f, 12.0f, 0.0f)
            )


            return LayerDefinition.create(meshdefinition, 128, 64)
        }
    }
}