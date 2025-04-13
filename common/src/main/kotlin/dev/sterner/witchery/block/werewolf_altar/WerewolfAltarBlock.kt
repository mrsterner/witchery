package dev.sterner.witchery.block.werewolf_altar

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class WerewolfAltarBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WerewolfAltarBlockEntity(pos, state)
    }
}