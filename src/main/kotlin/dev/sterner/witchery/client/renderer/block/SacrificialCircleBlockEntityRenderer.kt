package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.block.sacrificial_circle.SacrificialBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.SkullBlockEntity
import net.minecraft.world.phys.AABB
import kotlin.math.cos
import kotlin.math.sin


class SacrificialCircleBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<SacrificialBlockEntity> {

    override fun getRenderBoundingBox(blockEntity: SacrificialBlockEntity): AABB {
        val pos = blockEntity.blockPos
        return AABB(
            pos.x - 1.0,
            pos.y - 0.0,
            pos.z - 1.0,
            pos.x + 1.0,
            pos.y + 0.0,
            pos.z + 1.0
        )
    }

    override fun render(
        blockEntity: SacrificialBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val radius = 1.4
        val center = blockEntity.blockPos.center.subtract(0.5, 0.5, 0.5)

        val skullState = Blocks.SKELETON_SKULL.defaultBlockState()
        val skullBlockEntity = SkullBlockEntity(BlockPos.ZERO, skullState)

        poseStack.pushPose()
        poseStack.translate(
            center.x - blockEntity.blockPos.x,
            center.y - blockEntity.blockPos.y,
            center.z - blockEntity.blockPos.z
        )
        if (blockEntity.hasSkull) {
            Minecraft.getInstance().blockEntityRenderDispatcher.renderItem(
                skullBlockEntity, poseStack, bufferSource, packedLight, packedOverlay
            )
        }

        poseStack.popPose()

        for (i in blockEntity.candles.indices) {
            val candleItem = blockEntity.candles[i].item as? BlockItem ?: continue
            val candleBlock = candleItem.block
            val angle = Math.toRadians(360.0 / blockEntity.candles.size * i)
            val xOffset = radius * cos(angle)
            val zOffset = radius * sin(angle)

            poseStack.pushPose()
            poseStack.translate(
                center.x + xOffset - blockEntity.blockPos.x,
                center.y - blockEntity.blockPos.y,
                center.z + zOffset - blockEntity.blockPos.z
            )

            Minecraft.getInstance().blockRenderer.renderSingleBlock(
                candleBlock.defaultBlockState(),
                poseStack, bufferSource, packedLight, packedOverlay
            )
            poseStack.popPose()

            val randomOffset = blockEntity.level!!.random.nextInt(5)
            if ((blockEntity.level!!.gameTime + randomOffset) % 20 == 0L) {
                blockEntity.level?.addParticle(
                    ParticleTypes.SMALL_FLAME,
                    center.x + xOffset + 0.5, center.y + 0.5, center.z + zOffset + 0.5,
                    0.0, 0.0, 0.0
                )
            }
        }
    }
}