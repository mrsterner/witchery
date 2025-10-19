package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.layer.BabaItemLayer
import dev.sterner.witchery.client.model.BabaYagaEntityModel
import dev.sterner.witchery.client.model.WitchesRobesModel
import dev.sterner.witchery.content.entity.BabaYagaEntity
import dev.sterner.witchery.core.registry.WitcheryBlocks
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class BabaYagaEntityRenderer(var context: EntityRendererProvider.Context) :
    MobRenderer<BabaYagaEntity, BabaYagaEntityModel<BabaYagaEntity>>(
        context,
        BabaYagaEntityModel(context.bakeLayer(BabaYagaEntityModel.LAYER_LOCATION)),
        0.6f
    ) {

    var hatModel = WitchesRobesModel(context.bakeLayer(WitchesRobesModel.LAYER_LOCATION))

    init {
        this.addLayer(BabaItemLayer(this, context.itemInHandRenderer))
    }

    override fun render(
        entity: BabaYagaEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        model.leftLeg.visible = false
        model.rightLeg.visible = false
        this.model.holdingItem = !entity.mainHandItem.isEmpty
        poseStack.pushPose()
        poseStack.translate(0.0, -0.25, 0.0)

        val f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot)
        val k = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot)
        val g = Mth.rotLerp(partialTicks, entity.xRotO, entity.xRot)

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)

        poseStack.scale(-1.0f, -1.0f, 1.0f)
        poseStack.translate(0.0, -2.6, 0.0)
        poseStack.mulPose(Axis.YP.rotationDegrees(k))
        poseStack.translate(0.0, 1.1, 0.0)
        poseStack.mulPose(Axis.XP.rotationDegrees(-g))
        poseStack.translate(0.0, -1.1, 0.0)

        hatModel.head.visible = true
        hatModel.body.visible = false
        hatModel.leftArm.visible = false
        hatModel.rightArm.visible = false
        hatModel.leftLeg.visible = false
        hatModel.rightLeg.visible = false

        hatModel.renderToBuffer(
            poseStack,
            buffer.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/models/armor/baba_yagas_hat.png"))),
            packedLight,
            OverlayTexture.NO_OVERLAY,
            -1
        )
        poseStack.popPose()
        poseStack.pushPose()

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