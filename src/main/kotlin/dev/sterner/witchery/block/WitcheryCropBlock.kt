package dev.sterner.witchery.block

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty

open class WitcheryCropBlock(properties: Properties) : CropBlock(properties) {

    override fun getAgeProperty(): IntegerProperty {
        return AGE
    }

    override fun getMaxAge(): Int {
        return 4
    }

    override fun getAge(state: BlockState): Int {
        return state.getValue(this.ageProperty) as Int
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(AGE)
    }

    companion object {
        val AGE: IntegerProperty = BlockStateProperties.AGE_4
    }
}