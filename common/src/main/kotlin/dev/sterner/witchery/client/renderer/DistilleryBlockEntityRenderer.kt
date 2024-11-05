package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.distillery.DistilleryBlockEntity
import dev.sterner.witchery.client.model.DistilleryGemModel
import dev.sterner.witchery.client.model.JarModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.properties.BlockStateProperties


class DistilleryBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<DistilleryBlockEntity> {

    private var jarModel = JarModel(ctx.bakeLayer(JarModel.LAYER_LOCATION))
    private var gemModel = DistilleryGemModel(ctx.bakeLayer(DistilleryGemModel.LAYER_LOCATION))

    override fun render(
        blockEntity: DistilleryBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {

        if (blockEntity.blockState.getValue(BlockStateProperties.LIT)) {
            poseStack.pushPose()
            poseStack.scale(-1.0f, -1.0f, 1.0f)
            poseStack.translate(0 - .5, -1.5, 0.5)
            gemModel.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.eyes(Witchery.id("textures/block/distillery_gem.png"))),
                packedLight,
                packedOverlay
            )
            poseStack.popPose()
        }


        val offsets = listOf(
            Pair(6.2f / 16f, 0f),    // +X face
            Pair(-6.2f / 16f, 0f),   // -X face
            Pair(0f, 6.2f / 16f),    // +Z face
            Pair(0f, -6.2f / 16f)    // -Z face
        )

        val jarTexture = RenderType.entityCutout(Witchery.id("textures/block/jar_block.png"))
        val buffer = bufferSource.getBuffer(jarTexture)

        for ((index, offset) in offsets.withIndex()) {
            if (blockEntity.items[DistilleryBlockEntity.SLOT_JAR].count > index) {
                poseStack.pushPose()

                poseStack.translate(0.5 + offset.first, 0.0, 0.5 + offset.second)
                poseStack.scale(-1.0f, -1.0f, 1.0f)
                poseStack.translate(0.0, -2.1, 0.0)

                jarModel.renderToBuffer(
                    poseStack,
                    buffer,
                    packedLight,
                    packedOverlay
                )

                poseStack.popPose()
            }
        }
    }
}
