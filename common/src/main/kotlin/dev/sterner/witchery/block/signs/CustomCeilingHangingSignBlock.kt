package dev.sterner.witchery.block.signs

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.CeilingHangingSignBlock
import net.minecraft.world.level.block.WallHangingSignBlock
import net.minecraft.world.level.block.WallSignBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.WoodType

class CustomCeilingHangingSignBlock(type: WoodType, properties: Properties): CeilingHangingSignBlock(type, properties) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = CustomHangingSignBE(pos, state)
}