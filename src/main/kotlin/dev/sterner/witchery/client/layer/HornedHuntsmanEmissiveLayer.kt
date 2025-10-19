package dev.sterner.witchery.client.layer

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.HornedHuntsmanModel
import dev.sterner.witchery.client.renderer.entity.HornedHuntsmanEntityRenderer
import dev.sterner.witchery.content.entity.HornedHuntsmanEntity
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture

class HornedHuntsmanEmissiveLayer(renderer: HornedHuntsmanEntityRenderer) :
    net.minecraft.client.renderer.entity.layers.RenderLayer<HornedHuntsmanEntity, HornedHuntsmanModel>(renderer) {

    private val EMISSIVE_TEXTURE = Witchery.id("textures/entity/horned_huntsman_eyes.png")

    override fun render(
        poseStack: com.mojang.blaze3d.vertex.PoseStack,
        buffer: net.minecraft.client.renderer.MultiBufferSource,
        packedLight: Int,
        entity: HornedHuntsmanEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTick: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        if (entity.isAttacking()) {
            val renderType = RenderType.eyes(EMISSIVE_TEXTURE)
            parentModel.renderToBuffer(
                poseStack,
                buffer.getBuffer(renderType),
                15728640,
                OverlayTexture.NO_OVERLAY,
                -1
            )
        }
    }
}