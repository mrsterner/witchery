package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.entity.ScytheThrownEntity
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class ScytheThrownEntityRenderer(
    context: EntityRendererProvider.Context
) : EntityRenderer<ScytheThrownEntity>(context) {

    private val itemRenderer: ItemRenderer = context.itemRenderer
    private val scytheStack = ItemStack(WitcheryItems.DEATH_SICKLE.get())

    override fun render(
        entity: ScytheThrownEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()

        poseStack.translate(0.0, 0.15, 0.0)

        val rotationYaw = entity.getRotationYaw() + partialTick * 30f

        poseStack.mulPose(Axis.YP.rotationDegrees(entity.yRot - 90f))
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.xRot))

        poseStack.mulPose(Axis.ZP.rotationDegrees(rotationYaw))

        poseStack.scale(1.5f, 1.5f, 1.5f)

        itemRenderer.renderStatic(
            scytheStack,
            ItemDisplayContext.GROUND,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            buffer,
            entity.level(),
            entity.id
        )

        poseStack.popPose()
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight)
    }

    override fun getTextureLocation(entity: ScytheThrownEntity): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(Witchery.MODID, "textures/item/death_scythe.png")
    }
}
