package dev.sterner.witchery.block

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class MandrakeCropBlock(properties: Properties) : WitcheryCropBlock(properties) {

    override fun playerDestroy(
        level: Level,
        player: Player,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        tool: ItemStack
    ) {
        //TODO spawn mandrake entity
        super.playerDestroy(level, player, pos, state, blockEntity, tool)
    }

    override fun getBaseSeedId() = WitcheryItems.MANDRAKE_SEEDS.get()

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return CUSTOM_SHAPE_BY_AGE[state.getValue(AGE)]
    }

    companion object {
        val CUSTOM_SHAPE_BY_AGE = arrayOf(
            box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0)
        )
    }
}