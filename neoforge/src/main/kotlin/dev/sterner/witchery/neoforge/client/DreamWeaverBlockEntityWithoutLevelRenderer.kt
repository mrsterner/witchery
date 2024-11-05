package dev.sterner.witchery.neoforge.client

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.DreamWeaverBlockEntityModel
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack


class DreamWeaverBlockEntityWithoutLevelRenderer : BlockEntityWithoutLevelRenderer(
    Minecraft.getInstance().blockEntityRenderDispatcher,
    Minecraft.getInstance().entityModels
) {

    var model: DreamWeaverBlockEntityModel? =
        DreamWeaverBlockEntityModel(DreamWeaverBlockEntityModel.createBodyLayer().bakeRoot())
    private var texture: ResourceLocation? = null

    override fun renderByItem(
        stack: ItemStack,
        displayContext: ItemDisplayContext,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {

        if (texture == null) {
            val block = stack.descriptionId
            val filename = block.replaceFirst("block.witchery.", "")
            texture = Witchery.id("textures/block/${filename}.png")
        }

        poseStack.pushPose()
        poseStack.scale(-1.0f, -1.0f, 1.0f)
        model?.renderToBuffer(
            poseStack,
            buffer.getBuffer(RenderType.entityTranslucent(texture)),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()
    }
}