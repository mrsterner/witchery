package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.MirrorModel
import dev.sterner.witchery.content.block.mirror.MirrorBlockEntity
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import java.util.function.Supplier


class MirrorBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<MirrorBlockEntity> {

    private val model = MirrorModel(ctx.bakeLayer(MirrorModel.LAYER_LOCATION))
    private val texture = Witchery.id("textures/block/mirror.png")

    override fun getRenderBoundingBox(blockEntity: MirrorBlockEntity): AABB {
        val pos = blockEntity.blockPos
        return AABB(
            pos.x - 1.0, pos.y - 1.0, pos.z - 1.0,
            pos.x + 2.0, pos.y + 3.0, pos.z + 2.0
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
        val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val dirYRot = dir.toYRot()

        renderNormalMirror(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, dirYRot)
    }

    private fun renderNormalMirror(
        blockEntity: MirrorBlockEntity,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int,
        dirYRot: Float
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5, 1.5, 0.5)
        poseStack.mulPose(Axis.YP.rotationDegrees(-dirYRot))
        poseStack.scale(-1f, -1f, 1f)

        if (blockEntity.isSmallMirror) {
            model.single.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                packedOverlay,
                -1
            )
        } else {
            model.full.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                packedOverlay,
                -1
            )
        }

        poseStack.popPose()
    }
}