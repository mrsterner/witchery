package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.client.model.AncientTabletModel
import dev.sterner.witchery.content.block.ancient_tablet.AncientTabletBlockEntity
import dev.sterner.witchery.content.block.mirror.MirrorBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.phys.AABB

class MirrorBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<MirrorBlockEntity> {

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

    }
}