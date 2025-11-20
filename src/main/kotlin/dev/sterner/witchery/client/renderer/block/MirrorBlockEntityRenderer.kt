package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.AncientTabletModel
import dev.sterner.witchery.client.model.MirrorModel
import dev.sterner.witchery.content.block.ancient_tablet.AncientTabletBlockEntity
import dev.sterner.witchery.content.block.mirror.MirrorBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB

class MirrorBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<MirrorBlockEntity> {

    val model = MirrorModel(ctx.bakeLayer(MirrorModel.LAYER_LOCATION))
    val texture = Witchery.id("textures/block/mirror.png")

    override fun getRenderBoundingBox(blockEntity: MirrorBlockEntity): AABB {
        val pos = blockEntity.blockPos
        return AABB(
            pos.x - 0.0,
            pos.y - 0.0,
            pos.z - 0.0,
            pos.x + 0.0,
            pos.y + 1.0,
            pos.z + 0.0
        )
    }

    override fun render(
        blockEntity: MirrorBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5, 1.5, 0.5)
        val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val dirYRot = dir.toYRot()
        poseStack.mulPose(Axis.YP.rotationDegrees(-dirYRot))
        poseStack.scale(-1f, -1f, 1f)

        if (blockEntity.isSmallMirror) {
            model.single.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                packedOverlay
            )
        } else {
            model.full.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                packedOverlay
            )
        }

        poseStack.popPose()
    }
}