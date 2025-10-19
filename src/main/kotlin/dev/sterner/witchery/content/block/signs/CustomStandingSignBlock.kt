package dev.sterner.witchery.content.block.signs

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.StandingSignBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.WoodType

class CustomStandingSignBlock(type: WoodType, properties: Properties) : StandingSignBlock(type, properties) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = CustomSignBE(pos, state)
}