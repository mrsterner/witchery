package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.block.brazier.BrazierBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemDisplayContext
import org.joml.Vector4f
import org.joml.Vector4fc


class BrazierBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<BrazierBlockEntity> {


    override fun render(
        blockEntity: BrazierBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {

        poseStack.translate(0.5, 0.5, 0.5)
        for ((index, item) in blockEntity.items.withIndex()) {
            val transform = POS[index]
            poseStack.pushPose()
            poseStack.translate(transform.x(), transform.y(), transform.z())
            poseStack.scale(1 / 2.5f, 1 / 2.5f, 1 / 2.5f)
            poseStack.mulPose(Axis.XP.rotationDegrees(90f))
            poseStack.mulPose(Axis.ZP.rotationDegrees(transform.w()))
            Minecraft.getInstance().itemRenderer
                .renderStatic(
                    item,
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

    companion object {
        val POS: Array<Vector4fc> = arrayOf(
            Vector4f(-0.1f, 0.03f, -0.1f, 25 * Mth.DEG_TO_RAD),
            Vector4f(0.1f, 0.06f, -0.1f, -45 * Mth.DEG_TO_RAD),
            Vector4f(0.1f, 0.01f, 0.1f, 15 * Mth.DEG_TO_RAD),
            Vector4f(-0.1f, 0.07f, 0.1f, 65 * Mth.DEG_TO_RAD),
            Vector4f(0.05f, 0.05f, 0.05f, -35 * Mth.DEG_TO_RAD),
            Vector4f(-0.05f, 0.08f, 0.05f, -35 * Mth.DEG_TO_RAD),
            Vector4f(0.05f, 0.09f, -0.075f, 35 * Mth.DEG_TO_RAD),
            Vector4f(-0.075f, 0.1f, -0.05f, -35 * Mth.DEG_TO_RAD),
            Vector4f(0.075f, 0.11f, 0.075f, 35 * Mth.DEG_TO_RAD)
        )
    }
}
