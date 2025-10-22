package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.core.api.Ritual
import net.minecraft.core.BlockPos
import net.minecraft.server.commands.WeatherCommand
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BedBlockEntity
import net.minecraft.world.level.levelgen.Heightmap

class RainingToadRitual : Ritual("raining_toad") {

    private var tickCounter = 0

    override fun onStartRitual(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ): Boolean {
        super.onStartRitual(level, blockPos, goldenChalkBlockEntity)
        tickCounter = 0

        if (!level.isClientSide) {
            val serverLevel = level as ServerLevel
            serverLevel.setWeatherParameters(0, 6000, true, false)
        }
        return true
    }

    override fun onTickRitual(
        level: Level,
        pos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ) {
        super.onTickRitual(level, pos, goldenChalkBlockEntity)

        if (level.isClientSide) return

        tickCounter++
        if (tickCounter % (20 * 5) == 0) {
            val frog = EntityType.FROG.create(level) ?: return

            val dx = level.random.nextInt(65) - 32
            val dz = level.random.nextInt(65) - 32

            val spawnX = pos.x + dx.toDouble()
            val spawnZ = pos.z + dz.toDouble()

            val surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, spawnX.toInt(), spawnZ.toInt())

            val spawnY = surfaceY + 40.0

            frog.setPos(spawnX, spawnY, spawnZ)

            val data = RainingToadAttachment.Data(isPoisonous = true, safeFall = true)
            RainingToadAttachment.setData(frog, data)

            level.addFreshEntity(frog)
        }
    }


    override fun onEndRitual(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ) {
        super.onEndRitual(level, blockPos, goldenChalkBlockEntity)

        if (!level.isClientSide) {
            val serverLevel = level as ServerLevel
            serverLevel.setWeatherParameters(6000, 0, false, false)
        }
    }
}
