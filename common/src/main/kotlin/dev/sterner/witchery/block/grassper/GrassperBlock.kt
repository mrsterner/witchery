package dev.sterner.witchery.block.grassper

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class GrassperBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.GRASSPER.get().create(pos, state)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.box(6.0 / 16, 0.0, 6.0 / 16, 10.0 / 16, 8.0 / 16, 10.0 / 16)
    }
}