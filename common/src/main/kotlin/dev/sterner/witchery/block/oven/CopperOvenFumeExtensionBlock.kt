package dev.sterner.witchery.block.oven

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.WeatheringCopper
import net.minecraft.world.level.block.state.BlockState

class CopperOvenFumeExtensionBlock(val state: WeatheringCopper.WeatherState, properties: Properties) :
    OvenFumeExtensionBlock(properties), WeatheringCopper {

    override fun getAge(): WeatheringCopper.WeatherState {
        return state
    }

    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        this.changeOverTime(state, level, pos, random)
    }
}