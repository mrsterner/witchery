package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlockEntity
import dev.sterner.witchery.client.model.SpiritPortalBlockEntityModel
import dev.sterner.witchery.client.model.SpiritPortalPortalModel
import dev.sterner.witchery.registry.WitcheryRenderTypes
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class SpiritPortalBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<SpiritPortalBlockEntity> {

    private val model = SpiritPortalBlockEntityModel(ctx.bakeLayer(SpiritPortalBlockEntityModel.LAYER_LOCATION))
    private val modelShaderModel = SpiritPortalPortalModel(ctx.bakeLayer(SpiritPortalPortalModel.LAYER_LOCATION))

    override fun render(
        blockEntity: SpiritPortalBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()

        poseStack.translate(1.0, 1.5, 0.0)
        val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val yDir = dir.toYRot()
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - yDir))
        poseStack.scale(-1f, -1f, 1f)
        poseStack.mulPose(Axis.YP.rotationDegrees(180f))
        poseStack.translate(0.0, 0.0, -0.05)
        if (dir == Direction.WEST) {
            poseStack.translate(0.0, 0.0, 1.0)
        }
        if (dir == Direction.EAST) {
            poseStack.translate(1.0, 0.0, 0.0)
        }
        if (dir == Direction.NORTH) {
            poseStack.translate(0.0, 0.0, 0.0)
        }
        if (dir == Direction.SOUTH) {
            poseStack.translate(1.0, 0.0, 1.0)
        }
        modelShaderModel.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(WitcheryRenderTypes.SPIRIT_PORTAL.apply(Witchery.id("textures/block/spirit_door_portal.png"))),
            packedLight,
            packedOverlay
        )

        poseStack.mulPose(Axis.YP.rotationDegrees(-180f))

        val progress = blockEntity.getRenderProgress(partialTick)
        val doorAngle = progress * 90.0f * (-1)

        poseStack.pushPose()
        model.frame.render(
            poseStack,
            bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/spirit_door_square.png"))),
            packedLight,
            packedOverlay
        )
        poseStack.translate(-1.0, 0.0, 0.0)

        poseStack.mulPose(Axis.YP.rotationDegrees(doorAngle))
        poseStack.translate(1.0, 0.0, 0.0)
        model.lDoor.render(
            poseStack,
            bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/spirit_door_square.png"))),
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
            bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/spirit_door_square.png"))),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()

        poseStack.popPose()
    }
}