package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.core.api.Ritual
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks

class BlocksBelowRitual : Ritual("blocks_below") {

    private val columnsToProcess = mutableListOf<Pair<Int, Int>>()
    private val collectedIron = mutableListOf<ItemStack>()
    private var tickCounter = 0

    override fun onStartRitual(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ) {
        super.onStartRitual(level, blockPos, goldenChalkBlockEntity)

        val radius = 9
        for (dx in -radius..radius) {
            for (dz in -radius..radius) {
                if (dx*dx + dz*dz <= radius*radius) {
                    columnsToProcess.add(blockPos.x + dx to blockPos.z + dz)
                }
            }
        }
    }

    override fun onTickRitual(
        level: Level,
        pos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ) {
        super.onTickRitual(level, pos, goldenChalkBlockEntity)

        if (columnsToProcess.isEmpty() && collectedIron.isEmpty()) return

        val columnsThisTick = columnsToProcess.take(2)
        for ((x, z) in columnsThisTick) {
            columnsToProcess.remove(Pair(x, z))

            for (y in (level.minBuildHeight until pos.y).reversed()) {
                val blockPos = BlockPos(x, y, z)
                val block = level.getBlockState(blockPos).block
                if (block == Blocks.IRON_ORE) {
                    collectedIron.add(ItemStack(Items.IRON_ORE))
                    level.setBlockAndUpdate(blockPos, Blocks.STONE.defaultBlockState())
                } else if (block == Blocks.DEEPSLATE_IRON_ORE) {
                    collectedIron.add(ItemStack(Items.DEEPSLATE_IRON_ORE))
                    level.setBlockAndUpdate(blockPos, Blocks.DEEPSLATE.defaultBlockState())
                }
            }
        }

        tickCounter++
        if (tickCounter >= 20) {
            tickCounter = 0
            if (collectedIron.isNotEmpty()) {
                val toSpawn = collectedIron.take(5)
                collectedIron.removeAll(toSpawn)
                for (stack in toSpawn) {
                    val entity = ItemEntity(level, pos.x + 0.5, pos.y + 1.0, pos.z + 0.5, stack)
                    level.addFreshEntity(entity)
                }
            }
        }
    }
}