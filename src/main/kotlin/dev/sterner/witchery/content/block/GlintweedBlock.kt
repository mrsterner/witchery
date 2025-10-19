package dev.sterner.witchery.content.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.BushBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class GlintweedBlock(properties: Properties) : BushBlock(properties.lightLevel { 14 }) {

    override fun codec(): MapCodec<out BushBlock> {
        return CODEC
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        val vec3 = state.getOffset(level, pos)
        return SHAPE.move(vec3.x, vec3.y, vec3.z)
    }

    companion object {

        val CODEC: MapCodec<GlintweedBlock> = simpleCodec { arg: Properties ->
            GlintweedBlock(
                arg
            )
        }
        val SHAPE: VoxelShape = box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0)
    }
}