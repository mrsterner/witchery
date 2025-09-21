package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.Item
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.SoundActions
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.function.Supplier

object WitcheryFluids {
    val FLUIDS: DeferredRegister<Fluid> = DeferredRegister.create(BuiltInRegistries.FLUID, Witchery.MODID)
    val FLUID_TYPES: DeferredRegister<FluidType> =
        DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Witchery.MODID)

    val FLOWING_SPIRIT_TYPE: DeferredHolder<FluidType, FluidType> = FLUID_TYPES.register("flowing_spirit", Supplier {
        FluidType(
            FluidType.Properties.create()
                .density(1000)
                .viscosity(1000)
                .temperature(300)
                .canSwim(true)
                .canDrown(true)
                .supportsBoating(true)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
        )
    })

    val FLOWING_SPIRIT_STILL: DeferredHolder<Fluid, FlowingFluid> = FLUIDS.register("flowing_spirit_still", Supplier {
        FlowingSpirit.Source()
    })

    val FLOWING_SPIRIT_FLOWING: DeferredHolder<Fluid, FlowingFluid> =
        FLUIDS.register("flowing_spirit_flowing", Supplier {
            FlowingSpirit.Flowing()
        })

    fun register(eventBus: IEventBus) {
        FLUIDS.register(eventBus)
        FLUID_TYPES.register(eventBus)
    }

    abstract class FlowingSpirit : FlowingFluid() {

        override fun getFluidType(): FluidType = FLOWING_SPIRIT_TYPE.get()

        override fun getSource(): Fluid = FLOWING_SPIRIT_STILL.get()

        override fun getFlowing(): Fluid = FLOWING_SPIRIT_FLOWING.get()

        @Deprecated("Deprecated in Java")
        override fun canConvertToSource(level: Level): Boolean = false

        override fun getDropOff(level: LevelReader): Int = 1

        override fun getTickDelay(level: LevelReader): Int = 8

        override fun getSlopeFindDistance(level: LevelReader): Int = 4

        override fun getExplosionResistance(): Float = 100.0f

        override fun getBucket(): Item = WitcheryItems.FLOWING_SPIRIT_BUCKET.get()

        override fun canBeReplacedWith(
            level: FluidState,
            blockGetter: BlockGetter,
            pos: BlockPos,
            fluid: Fluid,
            direction: Direction
        ): Boolean {
            return direction == Direction.DOWN && !isSame(fluid)
        }

        override fun createFluidStateDefinition(builder: StateDefinition.Builder<Fluid, FluidState>) {
            super.createFluidStateDefinition(builder)
            builder.add(LEVEL)
        }

        override fun isSource(state: FluidState): Boolean = false

        override fun getAmount(state: FluidState): Int = 0

        class Source : FlowingSpirit() {
            override fun getAmount(state: FluidState): Int = 8

            override fun isSource(state: FluidState): Boolean = true

            override fun createLegacyBlock(state: FluidState): BlockState {
                return WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get().defaultBlockState()
            }

            override fun beforeDestroyingBlock(
                level: LevelAccessor,
                pos: BlockPos,
                state: BlockState
            ) {
                val blockEntity = if (state.hasBlockEntity()) level.getBlockEntity(pos) else null
                Block.dropResources(state, level, pos, blockEntity)
            }
        }

        class Flowing : FlowingSpirit() {
            override fun createFluidStateDefinition(builder: StateDefinition.Builder<Fluid, FluidState>) {
                super.createFluidStateDefinition(builder)
            }

            override fun getAmount(state: FluidState): Int {
                return state.getValue(LEVEL)
            }

            override fun isSource(state: FluidState): Boolean = false

            override fun createLegacyBlock(state: FluidState): BlockState {
                return WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get()
                    .defaultBlockState()
                    .setValue(LiquidBlock.LEVEL, state.getValue(LEVEL))
            }

            override fun beforeDestroyingBlock(
                level: LevelAccessor,
                pos: BlockPos,
                state: BlockState
            ) {
                val blockEntity = if (state.hasBlockEntity()) level.getBlockEntity(pos) else null
                Block.dropResources(state, level, pos, blockEntity)
            }
        }
    }
}