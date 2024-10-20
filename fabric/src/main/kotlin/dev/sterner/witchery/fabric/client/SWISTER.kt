package dev.sterner.witchery.fabric.client

import com.mojang.authlib.minecraft.client.MinecraftClient
import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.SpinningWheelBlockEntityModel
import dev.sterner.witchery.client.model.SpinningWheelWheelBlockEntityModel
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.model.geom.EntityModelSet
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class SWISTER : DynamicItemRenderer {

    var model: SpinningWheelBlockEntityModel? = SpinningWheelBlockEntityModel(SpinningWheelBlockEntityModel.createBodyLayer().bakeRoot())
    var wheelModel: SpinningWheelWheelBlockEntityModel? = SpinningWheelWheelBlockEntityModel(SpinningWheelWheelBlockEntityModel.createBodyLayer().bakeRoot())

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
        model?.base?.render(
            poseStack,
            bufferSource.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/block/spinner.png"))),
            packedLight,
            packedOverlay
        )
        poseStack.translate(0.0,-0.8,0.3)
        wheelModel?.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/block/spinning_wheel_wheel.png"))),
            packedLight,
            packedOverlay
        )

        poseStack.popPose()
    }

}