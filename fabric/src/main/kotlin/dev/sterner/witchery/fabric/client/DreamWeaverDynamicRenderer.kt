package dev.sterner.witchery.fabric.client

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.DreamWeaverBlockEntityModel
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class DreamWeaverDynamicRenderer : DynamicItemRenderer {

    private var model: DreamWeaverBlockEntityModel? =
        DreamWeaverBlockEntityModel(DreamWeaverBlockEntityModel.createBodyLayer().bakeRoot())
    private var texture: ResourceLocation? = null

    override fun render(
        stack: ItemStack,
        mode: ItemDisplayContext?,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {

        if (texture == null) {
            val block = stack.descriptionId
            val filename = block.replaceFirst("block.witchery.", "")
            texture = Witchery.id("textures/block/${filename}.png")
        } else {
            poseStack.pushPose()
            poseStack.scale(-1.0f, -1.0f, 1.0f)
            model?.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(texture!!)),
                packedLight,
                packedOverlay
            )
            poseStack.popPose()
        }
    }

}