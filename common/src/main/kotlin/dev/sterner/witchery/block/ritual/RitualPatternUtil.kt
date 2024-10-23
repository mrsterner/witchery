package dev.sterner.witchery.block.ritual

import dev.sterner.witchery.api.DynamicRitual
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

object RitualPatternUtil {

    fun matchesPattern(level: Level, center: BlockPos, ritual: DynamicRitual): Boolean {
        return matchesPattern(level, center, ritual.pattern, ritual.blocks)
    }

    private fun matchesPattern(
        level: Level,
        center: BlockPos,
        pattern: List<String>,
        blockMapping: Map<Char, Block>
    ): Boolean {
        val size = pattern.size
        val halfSize = size / 2
        for (z in pattern.indices) {
            for (x in pattern[z].indices) {
                val char = pattern[z][x]

                if (char == '_') continue

                val targetBlockPos = center.offset(x - halfSize, 0, z - halfSize)
                val targetBlock = level.getBlockState(targetBlockPos).block

                if (x == halfSize && z == halfSize && targetBlock != WitcheryBlocks.GOLDEN_CHALK_BLOCK.get()) {
                    return false
                }

                if (blockMapping[char] != targetBlock) {
                    return false
                }
            }
        }
        return true
    }
}