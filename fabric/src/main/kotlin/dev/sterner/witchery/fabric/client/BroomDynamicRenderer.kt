package dev.sterner.witchery.fabric.client

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.BroomEntityModel
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class BroomDynamicRenderer : DynamicItemRenderer {

    var model: BroomEntityModel? = BroomEntityModel(BroomEntityModel.createBodyLayer().bakeRoot())

    override fun render(
        stack: ItemStack?,
        mode: ItemDisplayContext?,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.scale(-1.0f, -1.0f, 1.0f)
        model?.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/entity/broom.png"))),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()
    }

}