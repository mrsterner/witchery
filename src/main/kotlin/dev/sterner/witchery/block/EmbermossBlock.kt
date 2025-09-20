package dev.sterner.witchery.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BushBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class EmbermossBlock(properties: Properties) : BushBlock(properties.lightLevel { 7 }) {

    override fun codec(): MapCodec<out BushBlock> {
        return CODEC
    }

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        if (!entity.fireImmune()) {
            entity.remainingFireTicks += 1
            if (entity.remainingFireTicks == 0) {
                entity.igniteForSeconds(8.0f)
            }
        }

        entity.hurt(level.damageSources().inFire(), 1f)
        super.entityInside(state, level, pos, entity)
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

        val CODEC: MapCodec<EmbermossBlock> = simpleCodec { arg: Properties ->
            EmbermossBlock(
                arg
            )
        }
        val SHAPE: VoxelShape = box(4.0, 0.0, 4.0, 12.0, 4.0, 12.0)
    }
}