package dev.sterner.witchery.content.worldgen.patch

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration
import kotlin.math.cos
import kotlin.math.sin

class MushroomCircleFeature(codec: Codec<RandomPatchConfiguration>) : Feature<RandomPatchConfiguration>(codec) {

    override fun place(context: FeaturePlaceContext<RandomPatchConfiguration>): Boolean {
        val config = context.config()
        val randomSource = context.random()
        val origin = context.origin()
        val worldGenLevel = context.level()

        val radius = config.xzSpread()
        var successfulPlacements = 0

        for (angle in 0 until 360 step 10) {
            val radians = Math.toRadians(angle.toDouble())
            val xOffset = (radius * cos(radians)).toInt()
            val zOffset = (radius * sin(radians)).toInt()
            val targetX = origin.x + xOffset
            val targetZ = origin.z + zOffset

            val targetY = worldGenLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, targetX, targetZ)
            val targetPos = BlockPos(targetX, targetY, targetZ)

            if (worldGenLevel.getBlockState(targetPos.below()).isSolid && worldGenLevel.isEmptyBlock(targetPos)) {
                if (config.feature().value().place(worldGenLevel, context.chunkGenerator(), randomSource, targetPos)) {
                    successfulPlacements++
                }
            }
        }

        return successfulPlacements > 0
    }
}