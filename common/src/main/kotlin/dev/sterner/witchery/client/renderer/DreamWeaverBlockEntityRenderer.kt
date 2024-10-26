package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.block.dream_weaver.DreamWeaverBlockEntity
import dev.sterner.witchery.client.model.AltarBlockEntityModel
import dev.sterner.witchery.client.model.AltarClothBlockEntityModel
import dev.sterner.witchery.client.model.DreamWeaverBlockEntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class DreamWeaverBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<DreamWeaverBlockEntity> {

    private val model = DreamWeaverBlockEntityModel(ctx.bakeLayer(DreamWeaverBlockEntityModel.LAYER_LOCATION))

    override fun render(
        blockEntity: DreamWeaverBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {

        val block = blockEntity.blockState.block.descriptionId
        val filename = block.replaceFirst("block.witchery.", "")
        val texture = Witchery.id("textures/block/${filename}.png")

        poseStack.pushPose()
        poseStack.translate(0.5, 1.5, 0.5)
        val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val dirYRot = dir.toYRot()
        poseStack.mulPose(Axis.YP.rotationDegrees(-dirYRot))
        poseStack.scale(-1f, -1f, 1f)
        this.model.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(RenderType.entityCutout(texture)),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()
    }
}