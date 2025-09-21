package dev.sterner.witchery.handler

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.world.WitcheryWallWorldState
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings

object VillageWallHandler {
    private val wallStraightStructure = Witchery.id("wall_straight") // 8x7x5
    private val wallCornerStructure = Witchery.id("wall_corner") // 7x8x7

    private val walledVillages = mutableSetOf<BoundingBox>()

    // Data class for wall segment information
    data class WallSegment(
        val structureId: ResourceLocation,
        val pos: BlockPos,
        val rotation: Rotation
    ) {
        companion object {
            val CODEC: Codec<WallSegment> = RecordCodecBuilder.create { instance ->
                instance.group(
                    ResourceLocation.CODEC.fieldOf("structure_id").forGetter { it.structureId },
                    BlockPos.CODEC.fieldOf("pos").forGetter { it.pos },
                    Rotation.CODEC.fieldOf("rotation").forGetter { it.rotation }
                ).apply(instance, ::WallSegment)
            }
        }
    }

    fun markVillage(bounds: BoundingBox, level: WorldGenLevel): Boolean {
        if (true) {
            return false
        }
        if (walledVillages.contains(bounds)) return false

        walledVillages.add(bounds)

        val segments = generateWallSegments(bounds)
        println("Generated ${segments.size} wall segments for village at: $bounds")

        for (segment in segments) {
            val chunkPos = ChunkPos(segment.pos)
            val data = WitcheryWallWorldState.get(level.level)
            val old = data.cachedSegments.getOrElse(chunkPos) { mutableListOf() }.toMutableList()
            old.add(segment)
            data.cachedSegments[chunkPos] = old
            data.setDirty()
            println("Saved segment at: ${segment.pos} to chunk: $chunkPos")
        }

        return true
    }


    private fun generateWallSegments(bounds: BoundingBox): List<WallSegment> {
        val segments = mutableListOf<WallSegment>()

        val minX = bounds.minX()
        val maxX = bounds.maxX()
        val minZ = bounds.minZ()
        val maxZ = bounds.maxZ()

        val wallSize = 8

        val corners = listOf(
            Triple(BlockPos(minX, 0, minZ), Rotation.NONE, Direction.EAST),  // NW
            Triple(BlockPos(maxX, 0, minZ), Rotation.CLOCKWISE_90, Direction.SOUTH), // NE
            Triple(BlockPos(maxX, 0, maxZ), Rotation.CLOCKWISE_180, Direction.WEST), // SE
            Triple(BlockPos(minX, 0, maxZ), Rotation.COUNTERCLOCKWISE_90, Direction.NORTH) // SW
        )

        for ((index, corner) in corners.withIndex()) {
            val (cornerPos, cornerRotation, _) = corner
            segments.add(WallSegment(wallCornerStructure, cornerPos, cornerRotation))

            val next = corners[(index + 1) % 4]
            val nextPos = next.first

            val dx = nextPos.x - cornerPos.x
            val dz = nextPos.z - cornerPos.z

            val distance = if (dx != 0) dx else dz
            val stepDir = if (dx != 0) Direction.EAST else Direction.SOUTH
            val step = stepDir.normal

            val absDistance = kotlin.math.abs(distance)
            val segmentCount = absDistance / wallSize

            val gateIndex = segmentCount / 2

            for (i in 1 until segmentCount) {
                val offset = i * wallSize
                val pos = cornerPos.offset(step.x * offset, 0, step.z * offset)
                val rotation = when (stepDir) {
                    Direction.EAST -> Rotation.NONE
                    Direction.SOUTH -> Rotation.CLOCKWISE_90
                    else -> Rotation.NONE
                }

                val structure = if (i == gateIndex) wallCornerStructure else wallStraightStructure
                segments.add(WallSegment(structure, pos, rotation))
            }
        }

        return segments
    }


    fun tick(level: ServerLevel) {

    }

    private fun getRotatedSize(size: BlockPos, rotation: Rotation): BlockPos {
        return when (rotation) {
            Rotation.NONE, Rotation.CLOCKWISE_180 -> size
            Rotation.CLOCKWISE_90, Rotation.COUNTERCLOCKWISE_90 -> BlockPos(size.z, size.y, size.x)
        }
    }

    private fun placeWallSegment(level: ServerLevel, segment: WallSegment) {
        try {
            val structureManager = level.structureManager
            val template = structureManager.getOrCreate(segment.structureId)

            val size = template.size
            val rotatedSize = getRotatedSize(BlockPos(size), segment.rotation)

            val x = segment.pos.x - rotatedSize.x / 2
            val z = segment.pos.z - rotatedSize.z / 2
            val y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, segment.pos.x, segment.pos.z)

            val placePos = BlockPos(x, y, z)

            val settings = StructurePlaceSettings().setRotation(segment.rotation)
            template.placeInWorld(level, placePos, placePos, settings, level.random, 2)
        } catch (e: Exception) {
            println("Error placing wall segment: ${e.message}")
        }
    }

    fun loadChunk(chunkAccess: ChunkAccess, serverLevel: ServerLevel?, compoundTag: CompoundTag?) {
        if (serverLevel != null) {
            val segments = WitcheryWallWorldState.get(serverLevel).cachedSegments.remove(chunkAccess.pos)
            if (segments.isNullOrEmpty()) {
                println("No wall segments found for chunk: ${chunkAccess.pos}")
            } else {
                segments.forEach { segment ->
                    println("Placing wall segment at ${segment.pos}")
                    placeWallSegment(serverLevel, segment)
                }
            }
        }
    }
}