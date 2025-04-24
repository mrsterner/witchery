package dev.sterner.witchery.fetish

import dev.sterner.witchery.api.FetishEffect
import dev.sterner.witchery.block.effigy.EffigyBlockEntity
import dev.sterner.witchery.block.effigy.EffigyState
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class SummonDeathFetishEffect : FetishEffect() {

    override fun onTickEffect(
        level: Level,
        blockEntity: EffigyBlockEntity,
        state: EffigyState?,
        pos: BlockPos,
        taglock: NonNullList<ItemStack>,
        tickRate: Int
    ) {

    }
}