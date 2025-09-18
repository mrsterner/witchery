package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.block.phylactery.PhylacteryBlockEntity
import dev.sterner.witchery.client.model.AltarBlockEntityModel
import dev.sterner.witchery.client.model.AltarClothBlockEntityModel
import dev.sterner.witchery.client.model.PhylacteryEtherCoreModel
import dev.sterner.witchery.client.model.PhylacteryEtherModel
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryRenderTypes
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class PhylacteryBlockEntityRenderer(var ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<PhylacteryBlockEntity> {

    private val model = PhylacteryEtherModel(ctx.bakeLayer(PhylacteryEtherModel.LAYER_LOCATION))
    private val core = PhylacteryEtherCoreModel(ctx.bakeLayer(PhylacteryEtherCoreModel.LAYER_LOCATION))

    override fun render(
        blockEntity: PhylacteryBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {

        poseStack.pushPose()
        poseStack.translate(0.5, 2.0, 0.5)
        poseStack.scale(-1f, -1f, 1f)

        // Render the core model
        poseStack.pushPose()
        poseStack.translate(0.0, -0.5, 0.0)
        poseStack.scale(0.75f, 1.5f, 0.75f)
        this.core.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(WitcheryRenderTypes.ETHER.apply(Witchery.id("textures/block/phylactery_ether_core.png"))),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()

        // Render the top altar block
        poseStack.pushPose()
        poseStack.translate(-0.5, 1.0, -0.5)
        ctx.blockRenderDispatcher.renderSingleBlock(
            WitcheryBlocks.DEEPLSTAE_ALTAR_BLOCK.get().defaultBlockState(),
            poseStack,
            bufferSource,
            packedLight,
            packedOverlay
        )
        poseStack.popPose()

        // Render the bottom altar block
        poseStack.pushPose()
        poseStack.translate(-0.5, -0.2, -0.5)
        ctx.blockRenderDispatcher.renderSingleBlock(
            WitcheryBlocks.DEEPLSTAE_ALTAR_BLOCK.get().defaultBlockState(),
            poseStack,
            bufferSource,
            packedLight,
            packedOverlay
        )
        poseStack.popPose()

        poseStack.popPose()
    }
}