package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.mushroom_log.MushroomLogBlockEntity
import dev.sterner.witchery.client.model.MushroomLogModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class MushroomLogBlockEntityRenderer(private val ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<MushroomLogBlockEntity> {

    var model = MushroomLogModel(ctx.bakeLayer(MushroomLogModel.LAYER_LOCATION))

    override fun render(
        blockEntity: MushroomLogBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        renderLog(blockEntity, poseStack, bufferSource, packedLight, packedOverlay)

        if (!blockEntity.currentMushroom.isEmpty) {
            renderMushrooms(blockEntity, poseStack, bufferSource, packedLight, packedOverlay)
        }
    }
    
    private fun renderLog(
        blockEntity: MushroomLogBlockEntity,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()

        val direction = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

        when (direction) {
            Direction.NORTH -> {
                poseStack.translate(0.0, 0.5, -0.5)
                poseStack.mulPose(Axis.YP.rotationDegrees(180f))
            }
            Direction.SOUTH -> {
                poseStack.translate(1.0, 0.5, 1.5)
                poseStack.mulPose(Axis.YP.rotationDegrees(0f))
            }
            Direction.EAST -> {
                poseStack.translate(1.5, 0.5, 0.0)
                poseStack.mulPose(Axis.YP.rotationDegrees(90f))
            }
            Direction.WEST -> {
                poseStack.translate(-0.5, 0.5, 1.0)
                poseStack.mulPose(Axis.YP.rotationDegrees(270f))
            }
            else -> {
                poseStack.translate(0.5, 0.5, 0.5)
            }
        }

        poseStack.scale(1.0f, -1.0f, 1.0f)
        poseStack.translate(-0.5, -1.0, -0.5)

        model.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(RenderType.entityCutoutNoCull(Witchery.id("textures/block/mushroom_log.png"))),
            packedLight,
            packedOverlay,
            -1
        )
        
        poseStack.popPose()
    }
    
    private fun renderMushrooms(
        blockEntity: MushroomLogBlockEntity,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val item = blockEntity.currentMushroom.item
        if (item !is BlockItem) return
        
        val mushroomState = item.block.defaultBlockState()
        val direction = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val mushroomData = blockEntity.getMushroomData()
        
        for (data in mushroomData) {
            poseStack.pushPose()

            val baseX = 0.5
            val baseY = 1.0
            val baseZ = 0.5

            when (direction) {
                Direction.NORTH -> {
                    val adjustedZ = 1.0 - data.zOffset - 1.5
                    poseStack.translate(baseX + data.xOffset, baseY, adjustedZ)
                }
                Direction.SOUTH -> {
                    val adjustedZ = baseZ - data.zOffset
                    poseStack.translate(baseX - data.xOffset, baseY, adjustedZ)
                }
                Direction.EAST -> {
                    val adjustedX = 1.0 - data.zOffset - 0.5
                    poseStack.translate(adjustedX, baseY, baseZ + data.xOffset)
                }
                Direction.WEST -> {
                    val adjustedX = baseX - data.zOffset - 1.0
                    poseStack.translate(adjustedX, baseY, baseZ - data.xOffset)
                }
                else -> {
                    poseStack.translate(baseX + data.xOffset, baseY, baseZ + data.zOffset)
                }
            }

            poseStack.mulPose(Axis.YP.rotationDegrees(data.rotation))

            val scale = data.scale

            poseStack.translate(-0.5 * scale, 0.0, -0.5 * scale)
            poseStack.scale(scale, scale, scale)

            ctx.blockRenderDispatcher.renderSingleBlock(
                mushroomState, 
                poseStack, 
                bufferSource, 
                packedLight, 
                packedOverlay
            )
            
            poseStack.popPose()
        }
    }
}