package dev.sterner.witchery.block.ancient_tablet


import dev.sterner.witchery.block.WitcheryBaseEntityBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class AncientTabletBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {
    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return AncientTabletBlockEntity(pos, state)
    }
}