package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.block.SuspiciousGraveyardDirtBlockEntity
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class SuspiciousGraveyardDirtBlockEntityRenderer(context: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<SuspiciousGraveyardDirtBlockEntity> {
    private val itemRenderer: ItemRenderer = context.itemRenderer

    override fun render(
        blockEntity: SuspiciousGraveyardDirtBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        if (blockEntity.level != null) {
            val i = blockEntity.blockState.getValue(BlockStateProperties.DUSTED) as Int
            if (i > 0) {
                val direction = blockEntity.hitDirection
                if (direction != null) {
                    val itemStack = blockEntity.storedItem
                    if (!itemStack.isEmpty) {
                        poseStack.pushPose()
                        poseStack.translate(0.0f, 0.5f, 0.0f)
                        val fs = this.translations(direction, i)
                        poseStack.translate(fs[0], fs[1], fs[2])
                        poseStack.mulPose(Axis.YP.rotationDegrees(75.0f))
                        val bl = direction == Direction.EAST || direction == Direction.WEST
                        poseStack.mulPose(Axis.YP.rotationDegrees(((if (bl) 90 else 0) + 11).toFloat()))
                        poseStack.scale(0.5f, 0.5f, 0.5f)
                        val j = LevelRenderer.getLightColor(
                            blockEntity.level!!,
                            blockEntity.blockState,
                            blockEntity.blockPos.relative(direction)
                        )
                        itemRenderer.renderStatic(
                            itemStack,
                            ItemDisplayContext.FIXED,
                            j,
                            OverlayTexture.NO_OVERLAY,
                            poseStack,
                            bufferSource,
                            blockEntity.level,
                            0
                        )
                        poseStack.popPose()
                    }
                }
            }
        }
    }

    private fun translations(direction: Direction, dustedLevel: Int): FloatArray {
        val fs = floatArrayOf(0.5f, 0.0f, 0.5f)
        val f = dustedLevel.toFloat() / 10.0f * 0.75f
        when (direction) {
            Direction.EAST -> fs[0] = 0.73f + f
            Direction.WEST -> fs[0] = 0.25f - f
            Direction.UP -> fs[1] = 0.25f + f
            Direction.DOWN -> fs[1] = -0.23f - f
            Direction.NORTH -> fs[2] = 0.25f - f
            Direction.SOUTH -> fs[2] = 0.73f + f
        }
        return fs
    }
}