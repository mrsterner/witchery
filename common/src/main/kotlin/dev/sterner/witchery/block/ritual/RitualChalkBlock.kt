package dev.sterner.witchery.block.ritual

import dev.architectury.event.EventResult
import dev.sterner.witchery.handler.VampireHandler
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape


class RitualChalkBlock(val type: ParticleType<*>?, val color: Int, properties: Properties) :
    Block(properties.noOcclusion().noCollission().replaceable()) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(VARIANT, 0)
        )
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if (!state.canSurvive(level, pos)) Blocks.AIR.defaultBlockState() else super.updateShape(
            state,
            direction,
            neighborState,
            level,
            pos,
            neighborPos
        )
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        return !level.isEmptyBlock(pos.below())
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(VARIANT)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return super.getStateForPlacement(context)
            ?.setValue(VARIANT, context.level.random.nextIntBetweenInclusive(0, VARIANTS))
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0 / 16, 1.0)
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
        if (type != null) {
            level.addParticle(
                type as ParticleOptions,
                pos.x + 0.5 + Mth.nextDouble(random, -0.5, 0.5),
                pos.y + (1 / 16.0),
                pos.z + 0.5 + Mth.nextDouble(random, -0.5, 0.5),
                0.0,
                0.0,
                0.0
            )
        }

        super.animateTick(state, level, pos, random)
    }

    companion object {
        const val VARIANTS = 15
        val VARIANT: IntegerProperty = IntegerProperty.create("variant", 0, VARIANTS)

        fun placeInfernal(level: Level, blockPos: BlockPos, blockState: BlockState, entity: Entity?): EventResult? {
            if (entity !is Player) {
                return EventResult.pass()
            }

            if (blockState.`is`(WitcheryBlocks.INFERNAL_CHALK_BLOCK.get())) {

                val surroundingOffsets = listOf(
                    BlockPos(1, 0, 0),  // East
                    BlockPos(1, 0, 1),  // Southeast
                    BlockPos(0, 0, 1),  // South
                    BlockPos(-1, 0, 1), // Southwest
                    BlockPos(-1, 0, 0), // West
                    BlockPos(-1, 0, -1),// Northwest
                    BlockPos(0, 0, -1), // North
                    BlockPos(1, 0, -1)  // Northeast
                )

                for (offset in surroundingOffsets) {
                    val testPos = blockPos.offset(offset)
                    if (level.getBlockState(testPos).`is`(Blocks.SKELETON_SKULL)) {

                        return tryMakeSacrificialCircle(level, testPos.immutable(), entity)
                    }
                }
            }

            return EventResult.pass()
        }

        private fun tryMakeSacrificialCircle(level: Level, skullPos: BlockPos, entity: Player): EventResult?  {

            val allInfernalChalk = level.getBlockStates(AABB.ofSize(skullPos.center, 2.0, 2.0, 2.0))
                .filter { it.`is`(WitcheryBlocks.INFERNAL_CHALK_BLOCK.get()) }.count()

            if (allInfernalChalk >= 7) {
                VampireHandler.makeSacrificialCircle(entity, skullPos)
                return EventResult.interruptFalse()
            }

            return EventResult.pass()
        }

    }
}