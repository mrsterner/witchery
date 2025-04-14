package dev.sterner.witchery.block.cauldron

import dev.architectury.fluid.FluidStack
import dev.architectury.platform.Platform
import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.api.multiblock.MultiBlockHorizontalDirectionStructure
import dev.sterner.witchery.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome.Precipitation
import net.minecraft.world.level.block.AbstractFurnaceBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.Supplier
import java.util.function.ToIntFunction

open class CauldronBlock(properties: Properties) :
    WitcheryBaseEntityBlock(properties.noOcclusion().lightLevel(litBlockEmission(8))) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.LIT, false)
        )
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            blockPlaceContext.horizontalDirection.opposite
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.LIT)
    }

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.CAULDRON.get().create(blockPos, blockState)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val be = level.getBlockEntity(pos)
        if (be is CauldronBlockEntity) {
            if (be.fluidTank.isEmpty()) {
                return Shapes.block()
            }
        }
        return SHAPE
    }

    private fun shouldHandlePrecipitation(level: Level, precipitation: Precipitation): Boolean {
        return when (precipitation) {
            Precipitation.RAIN -> level.getRandom().nextFloat() < 0.05f
            Precipitation.SNOW -> level.getRandom().nextFloat() < 0.1f
            else -> false
        }
    }

    private fun giveFluid(fluid: Fluid, amountWithoutConversion: Long, cauldron: CauldronBlockEntity) {
        val amount = amountWithoutConversion * if (Platform.isFabric()) 81 else 1
        cauldron.fluidTank.fill(FluidStack.create(fluid, amount), false)
    }

    override fun handlePrecipitation(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        precipitation: Precipitation
    ) {
        if (shouldHandlePrecipitation(level, precipitation) && !level.isClientSide) {
            level.getBlockEntity(pos, WitcheryBlockEntityTypes.CAULDRON.get()).ifPresent { cauldron ->
                giveFluid(Fluids.WATER, 10, cauldron)
            }
        }
    }

    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        super.randomTick(state, level, pos, random)
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
        if (state.getValue(BlockStateProperties.LIT) as Boolean) {
            if (random.nextInt(20) == 0) {
                level.playLocalSound(
                    pos.x.toDouble() + 0.5,
                    pos.y.toDouble() + 0.5,
                    pos.z.toDouble() + 0.5,
                    SoundEvents.CAMPFIRE_CRACKLE,
                    SoundSource.BLOCKS,
                    0.5f + random.nextFloat(),
                    random.nextFloat() * 0.7f + 0.6f,
                    false
                )
            }
            for (i in 0 until random.nextInt(1) + 1) {
                val d = pos.x.toDouble() + 0.5
                val e = pos.y.toDouble()
                val f = pos.z.toDouble() + 0.5
                if (random.nextDouble() < 0.1) {
                    level.playLocalSound(
                        d,
                        e,
                        f,
                        SoundEvents.FURNACE_FIRE_CRACKLE,
                        SoundSource.BLOCKS,
                        1.0f,
                        1.0f,
                        false
                    )
                }

                for (direction in Direction.entries) {
                    val axis = direction.axis
                    val h = random.nextDouble() * 0.6 - 0.3
                    val i = if (axis === Direction.Axis.X) direction.stepX.toDouble() * 0.42 else h
                    val j = random.nextDouble() * 6.0 / 16.0
                    val k = if (axis === Direction.Axis.Z) direction.stepZ.toDouble() * 0.42 else h
                    level.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0, 0.0, 0.0)
                    level.addParticle(ParticleTypes.FLAME, d + i, e + j, f + k, 0.0, 0.0, 0.0)
                }

            }
        }
    }

    companion object {
        private val INSIDE: VoxelShape = box(2.0, 7.0, 2.0, 14.0, 16.0, 14.0)

        val SHAPE: VoxelShape = Shapes.join(
            Shapes.block(),
            INSIDE,
            BooleanOp.ONLY_FIRST
        )

        val STRUCTURE: Supplier<MultiBlockHorizontalDirectionStructure> =
            Supplier<MultiBlockHorizontalDirectionStructure> {
                (MultiBlockHorizontalDirectionStructure.of(
                    MultiBlockStructure.StructurePiece(
                        0,
                        1,
                        0,
                        WitcheryBlocks.CAULDRON_COMPONENT.get().defaultBlockState()
                    )
                ))
            }

        fun litBlockEmission(lightValue: Int): ToIntFunction<BlockState> {
            return ToIntFunction { blockState: BlockState ->
                if (blockState.getValue(
                        BlockStateProperties.LIT
                    ) as Boolean
                ) lightValue else 0
            }
        }
    }
}