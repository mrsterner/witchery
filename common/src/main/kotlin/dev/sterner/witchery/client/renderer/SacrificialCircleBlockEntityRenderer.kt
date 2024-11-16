package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.block.sacrificial_circle.SacrificialBlockEntity
import net.fabricmc.loader.impl.lib.sat4j.core.Vec
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.SkullBlockEntity
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin


class SacrificialCircleBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<SacrificialBlockEntity> {

    override fun render(
        blockEntity: SacrificialBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val candleCount = 8 //blockEntity.candleCount.coerceIn(0, 8)
        val radius = 1.4
        val center = blockEntity.blockPos.center.subtract(0.5,0.5,0.5)
        poseStack.pushPose()
        // Render skeleton skull in the center
        val skullState = Blocks.SKELETON_SKULL.defaultBlockState()
        val skullBlockEntity = SkullBlockEntity(BlockPos.ZERO, skullState)

        poseStack.pushPose()
        poseStack.translate(center.x - blockEntity.blockPos.x,
            center.y - blockEntity.blockPos.y,
            center.z - blockEntity.blockPos.z)
        poseStack.scale(1.0f, 1.0f, 1.0f)
        Minecraft.getInstance().blockEntityRenderDispatcher.renderItem(
            skullBlockEntity, poseStack, bufferSource, packedLight, packedOverlay
        )
        poseStack.popPose()
        poseStack.popPose()
        for (i in 0 until candleCount) {
            val angle = Math.toRadians(360.0 / 8 * i)
            val xOffset = radius * cos(angle)
            val zOffset = radius * sin(angle)

            poseStack.pushPose()
            poseStack.translate(center.x + xOffset - blockEntity.blockPos.x,
                center.y - blockEntity.blockPos.y,
                center.z + zOffset - blockEntity.blockPos.z)

            Minecraft.getInstance().blockRenderer.renderSingleBlock(
                Blocks.CANDLE.defaultBlockState(),
                poseStack, bufferSource, packedLight, packedOverlay
            )
            poseStack.popPose()
            if (blockEntity.level!!.gameTime % 20 == 0L) {
                blockEntity.level?.addParticle(
                    ParticleTypes.SMALL_FLAME,
                    center.x + xOffset + 0.5, center.y + 0.5, center.z + zOffset + 0.5,
                    0.0, 0.0, 0.0
                )
            }

        }
    }
}