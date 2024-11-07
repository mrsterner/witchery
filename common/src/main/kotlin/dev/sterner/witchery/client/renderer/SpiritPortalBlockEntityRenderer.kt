package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlockEntity
import dev.sterner.witchery.client.model.SpiritPortalBlockEntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class SpiritPortalBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<SpiritPortalBlockEntity> {

    private val model = SpiritPortalBlockEntityModel(ctx.bakeLayer(SpiritPortalBlockEntityModel.LAYER_LOCATION))

    override fun render(
        blockEntity: SpiritPortalBlockEntity,
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

        val progress = blockEntity.getRenderProgress(partialTick)
        val doorAngle = progress * 90.0f * (-1)

        poseStack.pushPose()
        poseStack.translate(-1.0, 0.0, 0.0)
        poseStack.mulPose(Axis.YP.rotationDegrees(doorAngle))
        poseStack.translate(1.0, 0.0, 0.0)
        model.lDoor.render(
            poseStack,
            bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/spirit_door.png"))),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()

        poseStack.pushPose()
        poseStack.translate(1.0, 0.0, 0.0)
        poseStack.mulPose(Axis.YP.rotationDegrees(-doorAngle))
        poseStack.translate(-1.0, 0.0, 0.0)
        model.rDoor.render(
            poseStack,
            bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/spirit_door.png"))),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()

        poseStack.popPose()
    }
}