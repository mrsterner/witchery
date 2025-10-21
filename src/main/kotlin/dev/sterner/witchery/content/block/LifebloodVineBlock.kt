package dev.sterner.witchery.content.block


import com.mojang.serialization.MapCodec
import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.BonemealableBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class LifebloodVineBlock(properties: Properties) : Block(properties), BonemealableBlock {

    companion object {
        val CODEC: MapCodec<LifebloodVineBlock> = simpleCodec { LifebloodVineBlock(it) }
        val BERRIES: IntegerProperty = IntegerProperty.create("berries", 0, 3)
        val HARVESTED: BooleanProperty = BooleanProperty.create("harvested")

        private val SHAPE = box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0)
    }

    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(BERRIES, 0)
                .setValue(HARVESTED, false)
        )
    }

    override fun codec(): MapCodec<LifebloodVineBlock> = CODEC

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPE

    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        if (state.getValue(BERRIES) < 3 && !state.getValue(HARVESTED) && random.nextFloat() < 0.1f) {
            level.setBlock(pos, state.setValue(BERRIES, state.getValue(BERRIES) + 1), 2)
        }

        if (random.nextFloat() < 0.15f) {
            val below = pos.below()
            if (level.isEmptyBlock(below) && level.getRawBrightness(below, 0) <= 12) {
                level.setBlockAndUpdate(below, defaultBlockState())
            }
        }

        if (state.getValue(BERRIES) == 3 && random.nextFloat() < 0.3f) {
            spawnLifebloodParticles(level, pos)
        }
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val berries = state.getValue(BERRIES)

        if (berries > 0) {
            if (!level.isClientSide) {
                level.playSound(
                    null,
                    pos,
                    SoundEvents.CAVE_VINES_PICK_BERRIES,
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f
                )

                popResource(level, pos, ItemStack(WitcheryItems.LIFEBLOOD_BERRY.get(), berries))
                level.setBlock(pos, state.setValue(BERRIES, 0), 2)
            }
            return InteractionResult.sidedSuccess(level.isClientSide)
        }

        return InteractionResult.PASS
    }

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        if (entity is LivingEntity && level.gameTime % 20 == 0L && state.getValue(BERRIES) > 0) {
            entity.heal(0.25f)
        }
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        val above = pos.above()
        val aboveState = level.getBlockState(above)
        return aboveState.`is`(this) || aboveState.isFaceSturdy(level, above, Direction.DOWN)
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if (direction == Direction.UP && !canSurvive(state, level, pos)) {
            Blocks.AIR.defaultBlockState()
        } else {
            super.updateShape(state, direction, neighborState, level, pos, neighborPos)
        }
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BERRIES, HARVESTED)
    }

    override fun isRandomlyTicking(state: BlockState): Boolean = true

    override fun isValidBonemealTarget(
        level: LevelReader,
        pos: BlockPos,
        state: BlockState
    ): Boolean = state.getValue(BERRIES) < 3 || level.isEmptyBlock(pos.below())

    override fun isBonemealSuccess(
        level: Level,
        random: RandomSource,
        pos: BlockPos,
        state: BlockState
    ): Boolean = true

    override fun performBonemeal(
        level: ServerLevel,
        random: RandomSource,
        pos: BlockPos,
        state: BlockState
    ) {
        if (state.getValue(BERRIES) < 3) {
            level.setBlock(pos, state.setValue(BERRIES, 3), 2)
        }

        val below = pos.below()
        if (level.isEmptyBlock(below)) {
            level.setBlockAndUpdate(below, defaultBlockState().setValue(BERRIES, 0))
        }
    }

    private fun spawnLifebloodParticles(level: ServerLevel, pos: BlockPos) {
        val center = pos.center
        for (i in 0..3) {
            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.SOUL,
                center.x + level.random.nextDouble() - 0.5,
                center.y + level.random.nextDouble(),
                center.z + level.random.nextDouble() - 0.5,
                1,
                0.0, 0.05, 0.0, 0.0
            )
        }
    }

    private fun spawnHarvestParticles(level: ServerLevel, pos: BlockPos) {
        val center = pos.center
        for (i in 0..15) {
            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                center.x,
                center.y,
                center.z,
                1,
                level.random.nextGaussian() * 0.2,
                level.random.nextGaussian() * 0.2,
                level.random.nextGaussian() * 0.2,
                0.1
            )
        }
    }
}