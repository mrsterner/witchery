package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.bear_trap.BearTrapBlockEntity
import dev.sterner.witchery.client.model.BearTrapModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.Mth

class BearTrapBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<BearTrapBlockEntity> {

    private val coreModel = BearTrapModel(ctx.bakeLayer(BearTrapModel.LAYER_LOCATION))

    override fun render(
        blockEntity: BearTrapBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {

        val smoothRot = Mth.lerp(partialTick, blockEntity.prevAngle.toFloat(), blockEntity.angle.toFloat())


        coreModel.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/block/bear_trap.png"))),
            packedLight,
            packedOverlay,
            -0x1
        )
    }

}