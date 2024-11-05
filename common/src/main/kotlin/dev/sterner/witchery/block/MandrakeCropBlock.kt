package dev.sterner.witchery.block

import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class MandrakeCropBlock(properties: Properties) : WitcheryCropBlock(properties) {

    init {
        this.registerDefaultState(this.defaultBlockState().setValue(AWAKE, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder.add(AWAKE))
    }

    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        super.randomTick(state, level, pos, random)

        if (level.dayTime in 0..12000 && !state.getValue(AWAKE))
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(AWAKE, true))
        else if (state.getValue(AWAKE) && random.nextFloat() > 0.8)
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(AWAKE, false))
        else if (!state.getValue(AWAKE) && random.nextFloat() > 0.8)
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(AWAKE, false))
    }

    override fun playerDestroy(
        level: Level,
        player: Player,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        tool: ItemStack
    ) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool)

        if (level is ServerLevel && state.getValue(AWAKE))
            WitcheryEntityTypes.MANDRAKE.get().spawn(level, pos, MobSpawnType.NATURAL)
    }

    override fun getBaseSeedId(): ItemNameBlockItem = WitcheryItems.MANDRAKE_SEEDS.get()

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return CUSTOM_SHAPE_BY_AGE[state.getValue(AGE)]
    }

    companion object {
        val AWAKE: BooleanProperty = BooleanProperty.create("awake")

        val CUSTOM_SHAPE_BY_AGE = arrayOf(
            box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0)
        )
    }
}