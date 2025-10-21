package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.core.api.Ritual
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
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

        // Load ore types from recipe ritual data
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

        // Initialize columns to process in a circular radius
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

        // Process 3 columns per tick
        val columnsThisTick = columnsToProcess.take(3)
        for ((x, z) in columnsThisTick) {
            columnsToProcess.remove(Pair(x, z))

            // Scan from bottom to ritual position
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

        // Spawn items periodically
        tickCounter++
        if (tickCounter >= 5) {  // Drop every 5 ticks (4 times per second)
            tickCounter = 0
            if (collectedItems.isNotEmpty()) {
                val toSpawn = collectedItems.take(20)  // Drop 20 items at a time
                collectedItems.removeAll(toSpawn.toSet())
                for (stack in toSpawn) {
                    val entity = ItemEntity(level, pos.x + 0.5, pos.y + 1.0, pos.z + 0.5, stack)
                    level.addFreshEntity(entity)
                }
            }
        }
    }

    override fun onEndRitual(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ) {
        // Spawn all remaining collected items before ending
        if (collectedItems.isNotEmpty()) {
            for (stack in collectedItems) {
                val entity = ItemEntity(level, blockPos.x + 0.5, blockPos.y + 1.0, blockPos.z + 0.5, stack)
                level.addFreshEntity(entity)
            }
            collectedItems.clear()
        }
        super.onEndRitual(level, blockPos, goldenChalkBlockEntity)
    }

    override fun saveState(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity,
        tag: CompoundTag
    ) {
        val columnsTag = ListTag()
        columnsToProcess.forEach { (x, z) ->
            val columnTag = CompoundTag()
            columnTag.putInt("x", x)
            columnTag.putInt("z", z)
            columnsTag.add(columnTag)
        }
        tag.put("columnsToProcess", columnsTag)

        val itemsTag = ListTag()
        collectedItems.forEach { stack ->
            itemsTag.add(stack.save(level.registryAccess(), CompoundTag()))
        }
        tag.put("collectedItems", itemsTag)

        tag.putInt("tickCounter", tickCounter)

        tag.putString("targetOre", BuiltInRegistries.BLOCK.getKey(targetOre).toString())
        tag.putString("targetDeepslateOre", BuiltInRegistries.BLOCK.getKey(targetDeepslateOre).toString())
    }

    override fun loadState(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity,
        tag: CompoundTag
    ) {
        columnsToProcess.clear()
        val columnsTag = tag.getList("columnsToProcess", 10)
        for (i in 0 until columnsTag.size) {
            val columnTag = columnsTag.getCompound(i)
            columnsToProcess.add(columnTag.getInt("x") to columnTag.getInt("z"))
        }

        collectedItems.clear()
        val itemsTag = tag.getList("collectedItems", 10)
        for (i in 0 until itemsTag.size) {
            val itemTag = itemsTag.getCompound(i)
            ItemStack.parse(level.registryAccess(), itemTag).ifPresent { collectedItems.add(it) }
        }

        tickCounter = tag.getInt("tickCounter")

        if (tag.contains("targetOre")) {
            targetOre = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString("targetOre")))
        }
        if (tag.contains("targetDeepslateOre")) {
            targetDeepslateOre = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString("targetDeepslateOre")))
        }
    }
}