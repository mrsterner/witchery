package dev.sterner.witchery.block

import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LightLayer
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.ToIntFunction

class SunCollectorBlock(properties: Properties) : Block(
    properties.lightLevel(
        litBlockEmission(14)
    )
) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(SPHERE_STATE, 0)
        )
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        if (stack.`is`(WitcheryItems.QUARTZ_SPHERE.get())) {
            val bl = stack.has(WitcheryDataComponents.HAS_SUN.get())
            if (bl && stack.get(WitcheryDataComponents.HAS_SUN.get()) == false || !bl) {
                stack.shrink(1)
                level.setBlockAndUpdate(pos, state.setValue(SPHERE_STATE, 1))
                return ItemInteractionResult.SUCCESS
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (state.getValue(SPHERE_STATE) != 0) {
            level.setBlockAndUpdate(pos, state.setValue(SPHERE_STATE, 0))
            val sun = WitcheryItems.QUARTZ_SPHERE.get().defaultInstance
            if (state.getValue(SPHERE_STATE) == 2) {
                sun.set(WitcheryDataComponents.HAS_SUN.get(), true)
            }
            player.setItemInHand(InteractionHand.MAIN_HAND, sun)
        }

        return super.useWithoutItem(state, level, pos, player, hitResult)
    }

    override fun playerDestroy(
        level: Level,
        player: Player,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        tool: ItemStack
    ) {
        if (state.getValue(SPHERE_STATE) != 0) {
            level.setBlockAndUpdate(pos, state.setValue(SPHERE_STATE, 0))
            val sun = WitcheryItems.QUARTZ_SPHERE.get().defaultInstance
            if (state.getValue(SPHERE_STATE) == 2) {
                sun.set(WitcheryDataComponents.HAS_SUN.get(), true)
            }
            Containers.dropItemStack(level, pos.x + 0.5, pos.y.toDouble(), pos.z + 0.5, sun)
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool)
    }

    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        val i = level.getBrightness(LightLayer.SKY, pos) - level.skyDarken
        if (i > 9 && random.nextFloat() > 0.85) {
            level.setBlockAndUpdate(pos, state.setValue(SPHERE_STATE, 2))
        }

        super.randomTick(state, level, pos, random)
    }

    override fun isRandomlyTicking(state: BlockState): Boolean {
        return state.getValue(SPHERE_STATE) == 1
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(SPHERE_STATE)
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return box(5.0, 0.0, 5.0, 11.0, 14.0, 11.0)
    }

    companion object {
        val SPHERE_STATE: IntegerProperty = IntegerProperty.create("sphere_state", 0, 2)

        fun litBlockEmission(lightValue: Int): ToIntFunction<BlockState> {
            return ToIntFunction { blockState: BlockState ->
                if (blockState.getValue(
                        SPHERE_STATE
                    ) == 2
                ) lightValue else 0
            }
        }
    }
}