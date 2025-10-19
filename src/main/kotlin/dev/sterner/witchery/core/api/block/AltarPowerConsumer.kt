package dev.sterner.witchery.core.api.block

import dev.sterner.witchery.content.block.altar.AltarBlockEntity
import dev.sterner.witchery.core.data_attachment.AltarLevelAttachment
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import kotlin.math.pow
import kotlin.math.sqrt

interface AltarPowerConsumer {

    fun tryConsumeAltarPower(level: Level, pos: BlockPos, amount: Int, simulate: Boolean): Boolean {
        val be = level.getBlockEntity(pos)
        if (be is AltarBlockEntity) {
            return be.consumeAltarPower(amount, simulate)
        }
        return false
    }

    fun getAltarPos(level: ServerLevel, origin: BlockPos): BlockPos? {
        return AltarLevelAttachment.getAltarPos(level).minByOrNull { pos ->
            fun distance(pos1: BlockPos, pos2: BlockPos): Double {
                return sqrt(
                    (pos1.x - pos2.x).toDouble().pow(2) +
                            (pos1.y - pos2.y).toDouble().pow(2) +
                            (pos1.z - pos2.z).toDouble().pow(2)
                )
            }
            distance(origin, pos)
        }
    }
}