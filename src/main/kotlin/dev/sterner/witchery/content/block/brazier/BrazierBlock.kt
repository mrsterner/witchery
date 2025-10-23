package dev.sterner.witchery.content.block.brazier


import dev.sterner.witchery.content.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.content.block.cauldron.WitcheryCauldronBlock.Companion.litBlockEmission
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.RandomSource
import net.minecraft.world.Containers
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class BrazierBlock(properties: Properties) : WitcheryBaseEntityBlock(
    properties.lightLevel(
        litBlockEmission(14)
    )
) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.LIT, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.LIT)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.BRAZIER.get().create(pos, state)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.box(3.0 / 16.0, 0.0 / 16.0, 3.0 / 16.0, 13.0 / 16.0, 12.0 / 16.0, 13.0 / 16.0)
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        if (level.getBlockEntity(pos) is BrazierBlockEntity) {
            val be = level.getBlockEntity(pos) as BrazierBlockEntity
            if (!be.active) {
                Containers.dropContents(level, pos, be)
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
        if (state.getValue(BlockStateProperties.LIT)) {
            val baseX = pos.x.toDouble() + 0.5
            val baseY = pos.y.toDouble() + 0.8
            val baseZ = pos.z.toDouble() + 0.5

            val offsetX = (random.nextDouble() - 0.5) * 0.4
            val offsetY = (random.nextDouble() - 0.5) * 0.4
            val offsetZ = (random.nextDouble() - 0.5) * 0.4

            level.addParticle(ParticleTypes.SMOKE, baseX + offsetX, baseY + offsetY, baseZ + offsetZ, 0.0, 0.0, 0.0)
            level.addParticle(ParticleTypes.FLAME, baseX + offsetX, baseY + offsetY, baseZ + offsetZ, 0.0, 0.0, 0.0)

            level.addParticle(
                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                baseX + offsetX,
                baseY + offsetY,
                baseZ + offsetZ,
                0.0,
                0.0,
                0.0
            )
        }
    }
}