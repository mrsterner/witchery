package dev.sterner.witchery.content.block

import com.mojang.serialization.MapCodec
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.monster.Ravager
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.*
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import kotlin.math.abs
import kotlin.math.min

class WormwoodCropBlock(properties: Properties) : DoublePlantBlock(properties.noCollission()), BonemealableBlock {

    override fun codec(): MapCodec<WormwoodCropBlock> {
        return CODEC
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return this.defaultBlockState()
    }

    public override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return if (state.getValue(HALF) == DoubleBlockHalf.UPPER
        ) UPPER_SHAPE_BY_AGE[min(
            abs((4 - (state.getValue(BlockStateProperties.AGE_4) as Int + 1)).toDouble()),
            (UPPER_SHAPE_BY_AGE.size - 1).toDouble()
        )
            .toInt()]
        else LOWER_SHAPE_BY_AGE[state.getValue(BlockStateProperties.AGE_4)]
    }

    public override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if (isDouble(state.getValue(BlockStateProperties.AGE_4) as Int)) {
            super.updateShape(state, direction, neighborState, level, pos, neighborPos)
        } else {
            if (state.canSurvive(level, pos)) state else Blocks.AIR.defaultBlockState()
        }
    }

    public override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        return if (isLower(state) && !sufficientLight(level, pos)) false else super.canSurvive(state, level, pos)
    }

    override fun mayPlaceOn(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean {
        return state.`is`(Blocks.FARMLAND)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(BlockStateProperties.AGE_4)
        super.createBlockStateDefinition(builder)
    }

    public override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        if (entity is Ravager && level.gameRules.getBoolean(GameRules.RULE_MOBGRIEFING)) {
            level.destroyBlock(pos, true, entity)
        }

        super.entityInside(state, level, pos, entity)
    }

    public override fun canBeReplaced(state: BlockState, useContext: BlockPlaceContext): Boolean {
        return false
    }

    override fun setPlacedBy(
        level: Level,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        stack: ItemStack
    ) {
    }

    public override fun isRandomlyTicking(state: BlockState): Boolean {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER && !this.isMaxAge(state)
    }

    // Yes I stole this, AccessWidener didnt wanna work on NeoForge
    private fun getGrowthSpeed(block: Block, level: BlockGetter, pos: BlockPos): Float {
        var f = 1.0f
        val blockPos = pos.below()

        for (i in -1..1) {
            for (j in -1..1) {
                var g = 0.0f
                val blockState = level.getBlockState(blockPos.offset(i, 0, j))
                if (blockState.`is`(Blocks.FARMLAND)) {
                    g = 1.0f
                    if (blockState.getValue(FarmBlock.MOISTURE) as Int > 0) {
                        g = 3.0f
                    }
                }

                if (i != 0 || j != 0) {
                    g /= 4.0f
                }

                f += g
            }
        }

        val blockPos2 = pos.north()
        val blockPos3 = pos.south()
        val blockPos4 = pos.west()
        val blockPos5 = pos.east()
        val bl = level.getBlockState(blockPos4).`is`(block) || level.getBlockState(blockPos5).`is`(block)
        val bl2 = level.getBlockState(blockPos2).`is`(block) || level.getBlockState(blockPos3).`is`(block)
        if (bl && bl2) {
            f /= 2.0f
        } else {
            val bl3 = level.getBlockState(blockPos4.north()).`is`(block) || level.getBlockState(blockPos5.north())
                .`is`(block) || level.getBlockState(blockPos5.south())
                .`is`(block) || level.getBlockState(blockPos4.south()).`is`(block)
            if (bl3) {
                f /= 2.0f
            }
        }

        return f
    }

    public override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        val f = getGrowthSpeed(this, level, pos)
        val bl = random.nextInt((25.0f / f).toInt() + 1) == 0
        if (bl) {
            this.grow(level, state, pos)
        }
    }

    private fun grow(level: ServerLevel, state: BlockState, pos: BlockPos) {
        val i = min((state.getValue(BlockStateProperties.AGE_4) as Int + 1).toDouble(), 4.0).toInt()
        if (this.canGrow(level, pos, state, i)) {
            val blockState = state.setValue(BlockStateProperties.AGE_4, i)
            level.setBlock(pos, blockState, 2)
            if (isDouble(i)) {
                level.setBlock(pos.above(), blockState.setValue(HALF, DoubleBlockHalf.UPPER), 3)
            }
        }
    }

    private fun canGrowInto(level: LevelReader, pos: BlockPos): Boolean {
        val blockState = level.getBlockState(pos)
        return blockState.isAir || blockState.`is`(WitcheryBlocks.WORMWOOD_CROP.get())
    }

    private fun sufficientLight(level: LevelReader, pos: BlockPos): Boolean {
        return level.getRawBrightness(pos, 0) >= 8
    }

    private fun isLower(state: BlockState): Boolean {
        return state.`is`(WitcheryBlocks.WORMWOOD_CROP.get()) && state.getValue(HALF) == DoubleBlockHalf.LOWER
    }

    private fun isDouble(age: Int): Boolean {
        return age >= 3
    }

    private fun canGrow(reader: LevelReader, pos: BlockPos, state: BlockState, age: Int): Boolean {
        return !this.isMaxAge(state) && sufficientLight(reader, pos) && (!isDouble(age) || canGrowInto(
            reader,
            pos.above()
        ))
    }

    private fun isMaxAge(state: BlockState): Boolean {
        return state.getValue(BlockStateProperties.AGE_4) >= 4
    }

    private fun getLowerHalf(level: LevelReader, pos: BlockPos, state: BlockState): PosAndState? {
        if (isLower(state)) {
            return PosAndState(pos, state)
        } else {
            val blockPos = pos.below()
            val blockState = level.getBlockState(blockPos)
            return if (isLower(blockState)) PosAndState(blockPos, blockState) else null
        }
    }

    override fun isValidBonemealTarget(level: LevelReader, pos: BlockPos, state: BlockState): Boolean {
        val posAndState = this.getLowerHalf(level, pos, state)
        return if (posAndState == null) false else this.canGrow(
            level, posAndState.pos, posAndState.state, posAndState.state.getValue(
                BlockStateProperties.AGE_4
            ) + 1
        )
    }

    override fun isBonemealSuccess(level: Level, random: RandomSource, pos: BlockPos, state: BlockState): Boolean {
        return true
    }

    override fun performBonemeal(level: ServerLevel, random: RandomSource, pos: BlockPos, state: BlockState) {
        val posAndState = this.getLowerHalf(level, pos, state)
        if (posAndState != null) {
            this.grow(level, posAndState.state, posAndState.pos)
        }
    }

    @JvmRecord
    data class PosAndState(val pos: BlockPos, val state: BlockState)

    companion object {
        val CODEC: MapCodec<WormwoodCropBlock> = simpleCodec { properties: Properties ->
            WormwoodCropBlock(
                properties
            )
        }
        private val FULL_UPPER_SHAPE: VoxelShape = box(3.0, 0.0, 3.0, 13.0, 15.0, 13.0)
        private val FULL_LOWER_SHAPE: VoxelShape = box(3.0, -1.0, 3.0, 13.0, 16.0, 13.0)
        private val COLLISION_SHAPE_BULB: VoxelShape = box(5.0, -1.0, 5.0, 11.0, 3.0, 11.0)
        val UPPER_SHAPE_BY_AGE: Array<VoxelShape> = arrayOf(box(3.0, 0.0, 3.0, 13.0, 11.0, 13.0), FULL_UPPER_SHAPE)
        val LOWER_SHAPE_BY_AGE: Array<VoxelShape> = arrayOf(
            COLLISION_SHAPE_BULB,
            box(3.0, -1.0, 3.0, 13.0, 8.0, 13.0),
            box(3.0, -1.0, 3.0, 13.0, 13.0, 13.0),
            box(3.0, -1.0, 3.0, 13.0, 16.0, 13.0),
            FULL_LOWER_SHAPE
        )
    }
}