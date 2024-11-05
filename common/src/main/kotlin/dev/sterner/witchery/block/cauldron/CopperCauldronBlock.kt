package dev.sterner.witchery.block.cauldron

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.WeatheringCopper
import net.minecraft.world.level.block.state.BlockState

class CopperCauldronBlock(val state: WeatheringCopper.WeatherState, properties: Properties) : CauldronBlock(properties),
    WeatheringCopper {

    override fun getAge(): WeatheringCopper.WeatherState {
        return state
    }

    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        this.changeOverTime(state, level, pos, random)
    }
}