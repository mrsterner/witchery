package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.HuntsmanSpearEntity
import net.minecraft.client.model.TridentModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import java.awt.Color

class HuntsmanSpearRenderer(context: EntityRendererProvider.Context) : 
    EntityRenderer<HuntsmanSpearEntity>(context) {

    private val model = TridentModel(context.bakeLayer(ModelLayers.TRIDENT))
    
    companion object {
        private val TEXTURE = Witchery.id("textures/entity/huntsman_spear.png")
    }
    
    override fun render(
        entity: HuntsmanSpearEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()

        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.yRot) - 90.0f))
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.xRot) + 90.0f))

        val vertexConsumer = ItemRenderer.getFoilBufferDirect(
            buffer, 
            model.renderType(this.getTextureLocation(entity)), 
            false, 
            false
        )

        model.renderToBuffer(
            poseStack, 
            vertexConsumer, 
            packedLight, 
            OverlayTexture.NO_OVERLAY, 
           -1
        )

        if (entity.entityData.get(HuntsmanSpearEntity.Companion.ID_THROWN_BY_HUNTSMAN)) {
            val renderType = net.minecraft.client.renderer.RenderType.energySwirl(
                TEXTURE, 
                entity.tickCount * 0.01f % 1.0f, 
                entity.tickCount * 0.01f % 1.0f
            )
            
            val energyVertexConsumer = buffer.getBuffer(renderType)

            poseStack.scale(1.1f, 1.1f, 1.1f)

            model.renderToBuffer(
                poseStack,
                energyVertexConsumer,
                15728640,
                OverlayTexture.NO_OVERLAY,
                Color(255,255,60,100).rgb
            )
        }
        
        poseStack.popPose()
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
    }
    
    override fun getTextureLocation(entity: HuntsmanSpearEntity): ResourceLocation {
        return TEXTURE
    }
}