package dev.sterner.witchery.client.layer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.DemonEntityModel
import dev.sterner.witchery.registry.WitcheryMobEffects
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture

class DemonHeadFeatureRenderer(renderer: RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>, var ctx: EntityRendererProvider.Context) : RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(
    renderer
) {
    
    val demonModel = DemonEntityModel(ctx.bakeLayer(DemonEntityModel.LAYER_LOCATION))
    
    override fun render(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        livingEntity: AbstractClientPlayer,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTick: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        if (livingEntity.hasEffect(WitcheryMobEffects.GROTESQUE)) {
            poseStack.pushPose()

            demonModel.leftArm.visible = false
            demonModel.rightArm.visible = false
            demonModel.rightLeg.visible = false
            demonModel.leftLeg.visible = false
            demonModel.body.visible = false
            demonModel.coreBody.visible = false
            demonModel.upperBody.visible = false
            demonModel.rWing.visible = false
            demonModel.lWing.visible = false
            demonModel.head.visible = true

            val playerModel = this.parentModel

            demonModel.head.copyFrom(playerModel.head)
            poseStack.translate(0.0, 0.25, -0.5)
            poseStack.scale(0.9f, 0.9f, 0.9f)
            demonModel.renderToBuffer(
                poseStack, 
                bufferSource.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/entity/demon.png"))),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                -1
            )

            poseStack.popPose()
        }
    }
}