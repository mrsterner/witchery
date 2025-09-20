package dev.sterner.witchery.block.phylactery

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class PhylacteryBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {
    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return PhylacteryBlockEntity(pos, state)
    }

    override fun getRenderShape(state: BlockState): RenderShape? {
        return RenderShape.INVISIBLE
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape? {
        return box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0)
    }
}