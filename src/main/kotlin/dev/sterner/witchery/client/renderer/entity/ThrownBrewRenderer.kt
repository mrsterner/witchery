package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.content.entity.ThrownBrewEntity
import dev.sterner.witchery.item.QuartzSphereItem
import dev.sterner.witchery.item.brew.BrewItem
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class ThrownBrewRenderer(context: EntityRendererProvider.Context) : EntityRenderer<ThrownBrewEntity>(context) {

    private val itemRenderer: ItemRenderer = context.itemRenderer
    private val scale = 1.0f

    companion object {
        private const val MIN_CAMERA_DISTANCE_SQUARED = 12.25f
    }

    override fun render(
        entity: ThrownBrewEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        if (entity.tickCount < 2 && entityRenderDispatcher.camera.entity.distanceToSqr(entity) < MIN_CAMERA_DISTANCE_SQUARED) {
            return
        }

        val itemStack = entity.item

        if (entity.isQuartzSphere() && itemStack.item is BrewItem) {
            renderTintedQuartzSphere(entity, poseStack, buffer, packedLight, itemStack.item as BrewItem)
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
            return
        }

        poseStack.pushPose()
        poseStack.scale(scale, scale, scale)
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation())

        itemRenderer.renderStatic(
            itemStack,
            ItemDisplayContext.GROUND,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            buffer,
            entity.level(),
            entity.id
        )

        poseStack.popPose()
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
    }

    private fun renderTintedQuartzSphere(
        entity: ThrownBrewEntity,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        brew: BrewItem
    ) {
        poseStack.pushPose()
        poseStack.scale(scale, scale, scale)
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation())

        val brewColor = brew.color
        val red = (brewColor shr 16 and 0xFF) / 255.0f
        val green = (brewColor shr 8 and 0xFF) / 255.0f
        val blue = (brewColor and 0xFF) / 255.0f

        val sphereStack = ItemStack(WitcheryItems.QUARTZ_SPHERE.get())


        poseStack.pushPose()
        poseStack.scale(1.0f, 1.0f , 1.0f)

        val glowBuffer = TintedVertexConsumer(
            buffer.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(entity))),
            red,
            green,
            blue,
            0.9f
        )

        val model = itemRenderer.getModel(sphereStack, entity.level(), null, entity.id)
        itemRenderer.render(
            sphereStack,
            ItemDisplayContext.GROUND,
            false,
            poseStack,
            { _ -> glowBuffer },
            15728880,
            OverlayTexture.NO_OVERLAY,
            model
        )

        poseStack.popPose()
        poseStack.popPose()
    }


    override fun getTextureLocation(entity: ThrownBrewEntity): ResourceLocation {
        return TextureAtlas.LOCATION_BLOCKS
    }

    private class TintedVertexConsumer(
        private val delegate: VertexConsumer,
        private val r: Float,
        private val g: Float,
        private val b: Float,
        private val a: Float
    ) : VertexConsumer {

        override fun addVertex(x: Float, y: Float, z: Float): VertexConsumer {
            return delegate.addVertex(x, y, z)
        }

        override fun setColor(red: Int, green: Int, blue: Int, alpha: Int): VertexConsumer {
            val tintedR = (red * r).toInt().coerceIn(0, 255)
            val tintedG = (green * g).toInt().coerceIn(0, 255)
            val tintedB = (blue * b).toInt().coerceIn(0, 255)
            val tintedA = (alpha * a).toInt().coerceIn(0, 255)
            return delegate.setColor(tintedR, tintedG, tintedB, tintedA)
        }

        override fun setUv(u: Float, v: Float): VertexConsumer = delegate.setUv(u, v)
        override fun setUv1(u: Int, v: Int): VertexConsumer = delegate.setUv1(u, v)
        override fun setUv2(u: Int, v: Int): VertexConsumer = delegate.setUv2(u, v)
        override fun setNormal(x: Float, y: Float, z: Float): VertexConsumer = delegate.setNormal(x, y, z)
    }
}