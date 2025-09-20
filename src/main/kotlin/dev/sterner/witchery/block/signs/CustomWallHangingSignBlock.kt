package dev.sterner.witchery.block.signs

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.WallHangingSignBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.WoodType

class CustomWallHangingSignBlock(type: WoodType, properties: Properties) : WallHangingSignBlock(type, properties) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = CustomHangingSignBE(pos, state)
}