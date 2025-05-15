package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.layer.BabaItemLayer
import dev.sterner.witchery.client.model.BabaYagaEntityModel
import dev.sterner.witchery.client.model.HornedHuntsmanModel
import dev.sterner.witchery.client.model.WitchesRobesModel
import dev.sterner.witchery.entity.BabaYagaEntity
import dev.sterner.witchery.entity.CovenWitchEntity
import dev.sterner.witchery.entity.HornedHuntsmanEntity
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class HornedHuntsmanEntityRenderer(var context: EntityRendererProvider.Context) :
    MobRenderer<HornedHuntsmanEntity, HornedHuntsmanModel>(
        context,
        HornedHuntsmanModel(context.bakeLayer(BabaYagaEntityModel.LAYER_LOCATION)),
        0.6f
    ) {


    override fun render(
        entity: HornedHuntsmanEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
        poseStack.popPose()
    }

    override fun getTextureLocation(entity: HornedHuntsmanEntity): ResourceLocation {
        return Witchery.id("textures/entity/horned_huntsman.png")
    }
}