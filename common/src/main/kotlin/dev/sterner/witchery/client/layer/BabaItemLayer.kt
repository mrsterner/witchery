package dev.sterner.witchery.client.layer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.client.model.BabaYagaEntityModel
import dev.sterner.witchery.entity.BabaYagaEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.WitchModel
import net.minecraft.client.renderer.ItemInHandRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Items

@Environment(EnvType.CLIENT)
class BabaItemLayer(
    renderer: RenderLayerParent<BabaYagaEntity, BabaYagaEntityModel>,
    itemInHandRenderer: ItemInHandRenderer
) : CrossedArmsItemLayer<BabaYagaEntity, BabaYagaEntityModel>(renderer, itemInHandRenderer) {
    
    override fun render(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        livingEntity: BabaYagaEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        
        poseStack.pushPose()
        this.parentModel!!.head.translateAndRotate(poseStack)
        this.parentModel!!.nose.translateAndRotate(poseStack)
        poseStack.translate(0.0625f, 0.25f, 0.0f)
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f))
        poseStack.mulPose(Axis.XP.rotationDegrees(140.0f))
        poseStack.mulPose(Axis.ZP.rotationDegrees(10.0f))
        poseStack.translate(0.0f, -0.4f, 0.4f)
        
        super.render(
            poseStack,
            buffer,
            packedLight,
            livingEntity,
            limbSwing,
            limbSwingAmount,
            partialTicks,
            ageInTicks,
            netHeadYaw,
            headPitch
        )
        poseStack.popPose()
    }
}