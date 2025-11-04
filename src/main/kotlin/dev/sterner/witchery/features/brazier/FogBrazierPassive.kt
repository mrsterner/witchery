package dev.sterner.witchery.features.brazier

import dev.sterner.witchery.content.block.brazier.BrazierBlockEntity
import dev.sterner.witchery.core.api.BrazierPassive
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

class FogBrazierPassive : BrazierPassive("fog") {

    override fun onTickBrazier(
        level: Level,
        pos: BlockPos,
        blockEntity: BrazierBlockEntity
    ) {
        super.onTickBrazier(level, pos, blockEntity)
    }
}