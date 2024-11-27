package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.block.grassper.GrassperBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.item.ItemDisplayContext


class GrassperBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<GrassperBlockEntity> {

    override fun render(
        blockEntity: GrassperBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {

        poseStack.pushPose()
        poseStack.translate(0.5, 0.6, 0.5)
        poseStack.scale(0.5f, 0.5f, 0.5f)

        Minecraft.getInstance().itemRenderer
                .renderStatic(blockEntity.item[0],
                    ItemDisplayContext.FIXED,
                    packedLight,
                    packedOverlay,
                    poseStack,
                    bufferSource,
                    blockEntity.level,
                    123321
                )

        poseStack.popPose()
    }
}
