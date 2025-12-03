package dev.sterner.witchery.client.layer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.content.entity.InsanityEntity
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.EyesLayer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class InsanityEndermanEyesLayer(p_116964_: RenderLayerParent<InsanityEntity, EntityModel<InsanityEntity>>) :
    EyesLayer<InsanityEntity, EntityModel<InsanityEntity>>(p_116964_) {

    override fun renderType(): RenderType {
        return ENDERMAN_EYES
    }

    override fun render(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        livingEntity: InsanityEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        if (livingEntity.entityData.get(InsanityEntity.DATA_MIMIC) == "enderman") {
            val vertexconsumer = buffer.getBuffer(this.renderType())
            this.parentModel.renderToBuffer(poseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY)
        }
    }

    companion object {
        private val ENDERMAN_EYES: RenderType =
            RenderType.eyes(ResourceLocation.withDefaultNamespace("textures/entity/enderman/enderman_eyes.png"))
    }
}