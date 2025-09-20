package dev.sterner.witchery.client.renderer.without_level

import dev.sterner.witchery.block.werewolf_altar.WerewolfAltarBlockEntity
import dev.sterner.witchery.client.renderer.block.WerewolfAltarBlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.phys.AABB

class NeoWolfAltarRenderer(ctx: BlockEntityRendererProvider.Context) : WerewolfAltarBlockEntityRenderer(ctx) {

    override fun getRenderBoundingBox(blockEntity: WerewolfAltarBlockEntity): AABB {
        val pos = blockEntity.getBlockPos()
        return AABB(
            pos.getX() - 0.0,
            pos.getY() - 0.0,
            pos.getZ() - 0.0,
            pos.getX() + 0.0,
            pos.getY() + 2.0,
            pos.getZ() + 0.0
        )
    }

}