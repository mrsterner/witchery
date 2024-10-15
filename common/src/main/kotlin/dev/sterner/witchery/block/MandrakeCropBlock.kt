package dev.sterner.witchery.block

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

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
}