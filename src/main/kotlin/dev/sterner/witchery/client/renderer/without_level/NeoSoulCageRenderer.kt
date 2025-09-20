package dev.sterner.witchery.client.renderer.without_level

import dev.sterner.witchery.block.soul_cage.SoulCageBlockEntity
import dev.sterner.witchery.client.renderer.block.SoulCageBlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.phys.AABB

class NeoSoulCageRenderer(ctx: BlockEntityRendererProvider.Context) : SoulCageBlockEntityRenderer(ctx) {

    override fun getRenderBoundingBox(blockEntity: SoulCageBlockEntity): AABB {
        val pos = blockEntity.getBlockPos()
        return AABB(
            pos.getX() - 5.0,
            pos.getY() - 5.0,
            pos.getZ() - 5.0,
            pos.getX() + 5.0,
            pos.getY() + 5.0,
            pos.getZ() + 5.0
        )
    }

}