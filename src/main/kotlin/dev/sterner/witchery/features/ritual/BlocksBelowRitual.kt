package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.core.api.Ritual
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks

class BlocksBelowRitual(
    private var targetOre: Block = Blocks.IRON_ORE,
    private var targetDeepslateOre: Block = Blocks.DEEPSLATE_IRON_ORE
) : Ritual("blocks_below") {

    private val columnsToProcess = mutableListOf<Pair<Int, Int>>()
    private val collectedItems = mutableListOf<ItemStack>()
    private var tickCounter = 0

    constructor() : this(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE)

    override fun onStartRitual(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ) {
        super.onStartRitual(level, blockPos, goldenChalkBlockEntity)

        // Load ore types from recipe if available
        goldenChalkBlockEntity.ritualRecipe?.let { recipe ->
            val ritualData = recipe.ritualData
            if (ritualData.contains("targetOre")) {
                targetOre = BuiltInRegistries.BLOCK.get(
                    ResourceLocation.parse(ritualData.getString("targetOre"))
                )
            }
            if (ritualData.contains("targetDeepslateOre")) {
                targetDeepslateOre = BuiltInRegistries.BLOCK.get(
                    ResourceLocation.parse(ritualData.getString("targetDeepslateOre"))
                )
            }
        }

        val radius = 9
        for (dx in -radius..radius) {
            for (dz in -radius..radius) {
                if (dx * dx + dz * dz <= radius * radius) {
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

        if (columnsToProcess.isEmpty() && collectedItems.isEmpty()) return

        val columnsThisTick = columnsToProcess.take(2)
        for ((x, z) in columnsThisTick) {
            columnsToProcess.remove(Pair(x, z))

            for (y in (level.minBuildHeight until pos.y).reversed()) {
                val blockPos = BlockPos(x, y, z)
                val block = level.getBlockState(blockPos).block

                when (block) {
                    targetOre -> {
                        collectedItems.add(targetOre.asItem().defaultInstance)
                        level.setBlockAndUpdate(blockPos, Blocks.STONE.defaultBlockState())
                    }
                    targetDeepslateOre -> {
                        collectedItems.add(targetDeepslateOre.asItem().defaultInstance)
                        level.setBlockAndUpdate(blockPos, Blocks.DEEPSLATE.defaultBlockState())
                    }
                }
            }
        }

        tickCounter++
        if (tickCounter >= 20) {
            tickCounter = 0
            if (collectedItems.isNotEmpty()) {
                val toSpawn = collectedItems.take(5)
                collectedItems.removeAll(toSpawn)
                for (stack in toSpawn) {
                    val entity = ItemEntity(level, pos.x + 0.5, pos.y + 1.0, pos.z + 0.5, stack)
                    level.addFreshEntity(entity)
                }
            }
        }
    }
}