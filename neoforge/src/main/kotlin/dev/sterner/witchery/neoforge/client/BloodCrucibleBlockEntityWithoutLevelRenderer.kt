package dev.sterner.witchery.neoforge.client

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.BloodCrucibleModel
import dev.sterner.witchery.client.model.WerewolfAltarModel
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack


class BloodCrucibleBlockEntityWithoutLevelRenderer : BlockEntityWithoutLevelRenderer(
    Minecraft.getInstance().blockEntityRenderDispatcher,
    Minecraft.getInstance().entityModels
) {

    private var model: BloodCrucibleModel? = BloodCrucibleModel(BloodCrucibleModel.createBodyLayer().bakeRoot())

    override fun renderByItem(
        stack: ItemStack,
        displayContext: ItemDisplayContext,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.scale(-1.0f, -1.0f, 1.0f)
        model?.renderToBuffer(
            poseStack,
            buffer.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/block/vampire_altar.png"))),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()
    }
}