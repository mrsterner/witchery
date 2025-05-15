package dev.sterner.witchery.fabric.client

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.BroomEntityModel
import dev.sterner.witchery.client.model.HuntsmanSpearModel
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class HuntsmanSpearDynamicRenderer : DynamicItemRenderer {

    private var model: HuntsmanSpearModel? = HuntsmanSpearModel(HuntsmanSpearModel.createBodyLayer().bakeRoot())

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
            bufferSource.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/entity/huntsman_spear.png"))),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()
    }
}