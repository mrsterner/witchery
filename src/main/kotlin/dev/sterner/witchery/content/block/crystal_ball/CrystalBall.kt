package dev.sterner.witchery.content.block.crystal_ball

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class CrystalBall(properties: Properties) : Block(properties) {

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return SHAPE
    }

    companion object {
        val base = Shapes.create(4.0 / 16, 0.0, 4.0 / 16, 12.0 / 16, 3.0 / 16, 12.0 / 16)
        val glass = Shapes.create(3.5 / 16, 4.0 / 16, 3.5 / 16, 12.5 / 16, 13.0 / 16, 12.5 / 16)

        val SHAPE = Shapes.join(base, glass, BooleanOp.OR)
    }
}