package dev.sterner.witchery.api.block

import dev.sterner.witchery.block.altar.AltarBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

interface AltarPowerConsumer {
    fun receiveAltarPosition(corePos: BlockPos)

    fun tryConsumeAltarPower(level: Level, pos: BlockPos, amount: Int, simulate: Boolean): Boolean {
        val be = level.getBlockEntity(pos)
        if (be is AltarBlockEntity)
            return be.consumeAltarPower(amount, simulate)
        return false
    }
}