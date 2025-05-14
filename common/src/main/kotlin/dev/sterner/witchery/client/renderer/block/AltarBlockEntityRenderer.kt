package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.client.model.AltarBlockEntityModel
import dev.sterner.witchery.client.model.AltarClothBlockEntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class AltarBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<AltarBlockEntity> {

    private val altarModel = AltarBlockEntityModel(ctx.bakeLayer(AltarBlockEntityModel.LAYER_LOCATION))
    private val altarClothModel = AltarClothBlockEntityModel(ctx.bakeLayer(AltarClothBlockEntityModel.LAYER_LOCATION))

    override fun render(
        blockEntity: AltarBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5, 1.5, 0.5)
        val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - dir))
        poseStack.scale(-1f, -1f, 1f)
        this.altarModel.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/altar.png"))),
            packedLight,
            packedOverlay
        )
        this.altarClothModel.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/altar_red_cloth.png"))),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()
    }
}