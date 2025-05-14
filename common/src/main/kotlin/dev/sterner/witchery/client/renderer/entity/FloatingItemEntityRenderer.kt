package dev.sterner.witchery.client.renderer.entity


import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.entity.FloatingItemEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemDisplayContext


@Suppress("DEPRECATION")
class FloatingItemEntityRenderer(ctx: EntityRendererProvider.Context) : EntityRenderer<FloatingItemEntity>(ctx) {

    private val itemRenderer: ItemRenderer = ctx.itemRenderer

    init {
        this.shadowRadius = 0f
        this.shadowStrength = 0f
    }

    override fun render(
        entity: FloatingItemEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {

        poseStack.pushPose()
        val itemStack = entity.getItem()
        val model: BakedModel = this.itemRenderer.getModel(itemStack, entity.level(), null, entity.id)
        val yOffset = Mth.sin((entity.age.toFloat() + partialTick) / 10.0f + entity.bobOffs) * 0.1f + 0.1f
        val scale: Float = model.transforms.getTransform(ItemDisplayContext.GROUND).scale.y()
        val rotation: Float = entity.getSpin(partialTick)
        poseStack.translate(0.0, ((yOffset + 0.25f * scale).toDouble()), 0.0)
        poseStack.mulPose(Axis.YP.rotation(rotation))
        itemRenderer.render(
            itemStack,
            ItemDisplayContext.GROUND,
            false,
            poseStack,
            bufferSource,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            model
        )
        poseStack.popPose()
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
    }

    override fun getTextureLocation(entity: FloatingItemEntity): ResourceLocation {
        return TextureAtlas.LOCATION_BLOCKS
    }
}