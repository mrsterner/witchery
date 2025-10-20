package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.core.registry.WitcheryTags
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.Heightmap
import kotlin.random.Random

class BestialCallRitual : Ritual("bestial_call") {

    override fun onStartRitual(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ) {
        super.onStartRitual(level, blockPos, goldenChalkBlockEntity)

        if (level !is ServerLevel) return

        val animalTypes = BuiltInRegistries.ENTITY_TYPE
            .filter { it.create(level) is Animal && !it.`is`(WitcheryTags.BESTIAL_CALL_BLACKLIST)}
            .toList()

        repeat(10) {
            val randomType = animalTypes.randomOrNull() ?: return@repeat

            val offsetX = Random.nextInt(-5, 6)
            val offsetZ = Random.nextInt(-5, 6)
            val spawnX = blockPos.x + offsetX + 0.5
            val spawnZ = blockPos.z + offsetZ + 0.5

            val spawnY = level.getHeight(Heightmap.Types.WORLD_SURFACE, spawnX.toInt(), spawnZ.toInt())

            val entity = randomType.create(level) as? Animal ?: return@repeat
            entity.moveTo(spawnX, spawnY.toDouble(), spawnZ, level.random.nextFloat() * 360f, 0f)
            level.addFreshEntity(entity)
        }
    }
}
