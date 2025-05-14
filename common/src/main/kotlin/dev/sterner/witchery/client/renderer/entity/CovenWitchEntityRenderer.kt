package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.layer.BabaItemLayer
import dev.sterner.witchery.client.model.BabaYagaEntityModel
import dev.sterner.witchery.client.model.WitchesRobesModel
import dev.sterner.witchery.entity.BabaYagaEntity
import dev.sterner.witchery.entity.CovenWitchEntity
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class CovenWitchEntityRenderer(var context: EntityRendererProvider.Context) :
    MobRenderer<CovenWitchEntity, BabaYagaEntityModel<CovenWitchEntity>>(
        context,
        BabaYagaEntityModel(context.bakeLayer(BabaYagaEntityModel.LAYER_LOCATION)),
        0.6f
    ) {


    override fun render(
        entity: CovenWitchEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        model.leftLeg.visible = true
        model.rightLeg.visible = true
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
        poseStack.popPose()
    }

    override fun getTextureLocation(entity: CovenWitchEntity): ResourceLocation {
        return Witchery.id("textures/entity/baba_yaga.png")
    }
}