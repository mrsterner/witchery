package dev.sterner.witchery.features.tarot

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.state.BlockState

class TheEmpressEffect : TarotEffect(4) {

    override fun getDisplayName(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_empress.reversed" else "tarot.witchery.the_empress"
    )

    override fun getDescription(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_empress.reversed.description" else "tarot.witchery.the_empress.description"
    )

    override fun onBlockBreak(player: Player, blockState: BlockState, pos: BlockPos, isReversed: Boolean) {
        if (isReversed) {
            if (blockState.block is CropBlock && player.level().random.nextFloat() < 0.3f) {
                player.level().destroyBlock(pos, false)
            }
        } else {
            if (blockState.block is CropBlock && player.level().random.nextFloat() < 0.25f) {
                Block.popResource(player.level(), pos, ItemStack(blockState.block.asItem()))
            }
        }
    }

    override fun onMorning(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            player.foodData.foodLevel = (player.foodData.foodLevel + 4).coerceAtMost(20)
        }
    }
}