package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.BabaYagaEntityModel
import dev.sterner.witchery.entity.BabaYagaEntity
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class BabaYagaEntityRenderer(var context: EntityRendererProvider.Context) :
    MobRenderer<BabaYagaEntity, BabaYagaEntityModel>(
        context,
        BabaYagaEntityModel(context.bakeLayer(BabaYagaEntityModel.LAYER_LOCATION)),
        0.6f
    ) {
    
    override fun render(
        entity: BabaYagaEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.0, -0.25, 0.0)
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
        poseStack.popPose()
        poseStack.pushPose()

        val f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot)

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - f))
        poseStack.translate(-0.5, 0.0, -0.5)
        Minecraft.getInstance().blockRenderer.renderSingleBlock(
            WitcheryBlocks.CAULDRON_DUMMY.get().defaultBlockState(),
            poseStack,
            buffer,
            packedLight,
            OverlayTexture.NO_OVERLAY
        )

        poseStack.popPose()
    }

    override fun getTextureLocation(entity: BabaYagaEntity): ResourceLocation {
        return Witchery.id("textures/entity/baba_yaga.png")
    }
}