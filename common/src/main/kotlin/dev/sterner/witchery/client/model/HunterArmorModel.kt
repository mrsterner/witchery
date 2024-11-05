package dev.sterner.witchery.client.model

import dev.sterner.witchery.Witchery
import net.minecraft.client.model.HumanoidArmorModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.entity.LivingEntity


class HunterArmorModel(val root: ModelPart) : HumanoidArmorModel<LivingEntity>(root) {

    companion object {

        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(Witchery.id("witch_hunter_armor"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val Head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.55f))
                    .texOffs(50, 0).addBox(-7.0f, -6.0f, -7.0f, 14.0f, 1.0f, 14.0f, CubeDeformation(0.0f))
                    .texOffs(56, 15).addBox(-5.0f, -9.0f, -5.0f, 10.0f, 3.0f, 10.0f, CubeDeformation(0.0f))
                    .texOffs(58, 28).addBox(-4.0f, -12.0f, -4.0f, 8.0f, 3.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val Plume_r1 = Head.addOrReplaceChild(
                "Plume_r1",
                CubeListBuilder.create().texOffs(46, 39)
                    .addBox(-5.5f, -14.0f, 0.0f, 1.0f, 10.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.1745f, 0.0873f, 0.0f)
            )

            val Body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, CubeDeformation(0.3f))
                    .texOffs(0, 46).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, CubeDeformation(0.35f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val Stakes_r1 = Body.addOrReplaceChild(
                "Stakes_r1",
                CubeListBuilder.create().texOffs(40, 8)
                    .addBox(-8.5f, 2.0f, -3.5f, 3.0f, 4.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(32, 12).addBox(-13.0f, 3.0f, 2.5f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(36, 44).addBox(-10.0f, 2.0f, 2.5f, 6.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(48, 8).addBox(-11.5f, 2.0f, -3.5f, 3.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(36, 14).addBox(-3.5f, 2.0f, 2.5f, 3.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(48, 8).addBox(-1.5f, 2.0f, -3.5f, 3.0f, 3.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(24, 0).addBox(-12.5f, 2.0f, -2.5f, 15.0f, 3.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.9599f)
            )

            partdefinition.addOrReplaceChild(
                "hat",
                CubeListBuilder.create(), PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
            )

            val CoatTail = Body.addOrReplaceChild(
                "CoatTail",
                CubeListBuilder.create().texOffs(0, 32)
                    .addBox(-4.0f, 0.0f, 0.5f, 8.0f, 10.0f, 4.0f, CubeDeformation(0.4f)),
                PartPose.offset(0.0f, 12.5f, -2.5f)
            )

            val RightArm = partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(24, 16)
                    .addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.3f))
                    .texOffs(24, 32).addBox(-4.0f, -2.5f, -2.5f, 5.0f, 7.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(44, 32).addBox(-4.0f, 5.0f, -2.5f, 2.0f, 4.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, 2.0f, 0.0f)
            )

            val LeftArm = partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(24, 16).mirror()
                    .addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.3f)).mirror(false),
                PartPose.offset(5.0f, 2.0f, 0.0f)
            )

            val RightLeg = partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(40, 16)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.255f))
                    .texOffs(24, 44).addBox(-2.0f, 6.0f, -2.0f, 4.0f, 6.0f, 4.0f, CubeDeformation(0.3f)),
                PartPose.offset(-1.9f, 12.0f, 0.0f)
            )

            val LeftLeg = partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(40, 16).mirror()
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.255f)).mirror(false)
                    .texOffs(24, 44).mirror().addBox(-2.0f, 6.0f, -2.0f, 4.0f, 6.0f, 4.0f, CubeDeformation(0.3f))
                    .mirror(false),
                PartPose.offset(1.9f, 12.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 64)
        }
    }
}