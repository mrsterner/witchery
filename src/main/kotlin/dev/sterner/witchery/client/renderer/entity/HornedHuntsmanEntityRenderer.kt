package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.layer.HornedHuntsmanEmissiveLayer
import dev.sterner.witchery.client.model.HornedHuntsmanModel
import dev.sterner.witchery.entity.HornedHuntsmanEntity
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer
import net.minecraft.client.renderer.entity.player.PlayerRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class HornedHuntsmanEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<HornedHuntsmanEntity, HornedHuntsmanModel>(
        context, 
        HornedHuntsmanModel(context.bakeLayer(HornedHuntsmanModel.LAYER_LOCATION)), 
        0.7f
    ) {
    
    companion object {
        private val TEXTURE = Witchery.id("textures/entity/horned_huntsman.png")
    }
    
    init {
        addLayer(ItemInHandLayer(this, context.itemInHandRenderer))
        addLayer(HornedHuntsmanEmissiveLayer(this))
    }
    
    override fun getTextureLocation(entity: HornedHuntsmanEntity): ResourceLocation {
        return TEXTURE
    }
    
    override fun scale(
        entity: HornedHuntsmanEntity,
        poseStack: PoseStack,
        partialTick: Float
    ) {
        val scale = 1.5f
        poseStack.scale(scale, scale, scale)
        super.scale(entity, poseStack, partialTick)
    }
    
    override fun setupRotations(
        entity: HornedHuntsmanEntity,
        poseStack: PoseStack,
        ageInTicks: Float,
        rotationYaw: Float,
        partialTick: Float,
        scale: Float
    ) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick, scale)
        if (!entity.isAttacking() && entity.hasSpear()) {
            val swayAmount = Mth.sin(ageInTicks * 0.05f) * 0.03f
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(swayAmount))
        }
    }
}

