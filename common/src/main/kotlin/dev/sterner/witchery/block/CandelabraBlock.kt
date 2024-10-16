package dev.sterner.witchery.block

import dev.sterner.witchery.block.cauldron.CauldronBlock.Companion.litBlockEmission
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape


class CandelabraBlock(properties: Properties) : Block(properties.noOcclusion().lightLevel(litBlockEmission(13))) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.LIT, true)
        )
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return SHAPE
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.LIT)
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
        if (state.getValue(BlockStateProperties.LIT)) {
            level.addParticle(ParticleTypes.SMALL_FLAME, pos.x + 0.5, pos.y + 1.0, pos.z + 0.5, 0.0, 0.0, 0.0)
            level.addParticle(ParticleTypes.SMALL_FLAME, pos.x + 0.2, pos.y + 0.9, pos.z + 0.5, 0.0, 0.0, 0.0)
            level.addParticle(ParticleTypes.SMALL_FLAME, pos.x + 0.8, pos.y + 0.9, pos.z + 0.5, 0.0, 0.0, 0.0)
            level.addParticle(ParticleTypes.SMALL_FLAME, pos.x + 0.5, pos.y + 0.9, pos.z + 0.8, 0.0, 0.0, 0.0)
            level.addParticle(ParticleTypes.SMALL_FLAME, pos.x + 0.5, pos.y + 0.9, pos.z + 0.2, 0.0, 0.0, 0.0)
        }
    }


    companion object {
        val SHAPE: VoxelShape = box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0)
    }
}