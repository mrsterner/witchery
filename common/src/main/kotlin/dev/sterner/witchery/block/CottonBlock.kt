package dev.sterner.witchery.block

import com.mojang.serialization.MapCodec
import dev.sterner.witchery.block.GlintweedBlock.Companion
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.BushBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class CottonBlock(properties: Properties) : BushBlock(properties) {
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

        val CODEC: MapCodec<CottonBlock> = simpleCodec { arg: Properties ->
            CottonBlock(
                arg
            )
        }
        val SHAPE: VoxelShape = box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0)
    }
}