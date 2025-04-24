package dev.sterner.witchery.handler

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.mixin.LevelChunkAccessor
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.BoundingBox

import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.Mirror

object VillageWallHandler {
    private val seenVillages: MutableSet<String> = mutableSetOf()
    private val toDecorate: MutableList<VillageData> = mutableListOf()

    private val wallStraightStructure = Witchery.id("wall_straight") // 8x7x5
    private val wallCornerStructure = Witchery.id("wall_corner") // 7x8x7

    private const val SEGMENT_LENGTH = 8
    private const val WALL_WIDTH = 5

    fun tick(level: ServerLevel) {
        if (toDecorate.isEmpty()) return
        val copy = ArrayList(toDecorate)
        toDecorate.clear()

        for (village in copy) {
            if (isChunkLoaded(level, village.villageCenter)) {
                placeWallSegmentsAround(level, village)
            } else {
                toDecorate.add(village)
            }
        }
    }

    private fun getCenterOfBoundingBox(box: BoundingBox): BlockPos {
        val centerX = (box.minX() + box.maxX()) / 2
        val centerZ = (box.minZ() + box.maxZ()) / 2
        val y = box.minY()
        return BlockPos(centerX, y, centerZ)
    }

    private fun getAdjustedBoundingBox(box: BoundingBox): BoundingBox {
        val expandedMinX = box.minX() - WALL_WIDTH
        val expandedMaxX = box.maxX() + WALL_WIDTH
        val expandedMinZ = box.minZ() - WALL_WIDTH
        val expandedMaxZ = box.maxZ() + WALL_WIDTH

        return BoundingBox(expandedMinX, box.minY(), expandedMinZ, expandedMaxX, box.maxY(), expandedMaxZ)
    }

    private fun placeCornerStructures(level: ServerLevel, box: BoundingBox, template: StructureTemplate) {
        val cornerPositions = listOf(
            BlockPos(box.minX(), getHeightAt(level, box.minX(), box.minZ()), box.minZ()),
            BlockPos(box.minX(), getHeightAt(level, box.minX(), box.maxZ()), box.maxZ()),
            BlockPos(box.maxX(), getHeightAt(level, box.maxX(), box.minZ()), box.minZ()),
            BlockPos(box.maxX(), getHeightAt(level, box.maxX(), box.maxZ()), box.maxZ())
        )

        for (pos in cornerPositions) {
            placeStructure(template, level, pos, Rotation.NONE)
        }
    }

    private fun placeStraightWalls(level: ServerLevel, box: BoundingBox, template: StructureTemplate) {
        for (x in (box.minX() + SEGMENT_LENGTH) until box.maxX() step SEGMENT_LENGTH) {
            val y = getHeightAt(level, x, box.minZ())
            val pos = BlockPos(x, y, box.minZ() - WALL_WIDTH)
            placeStructure(template, level, pos, Rotation.NONE)
        }

        for (x in (box.minX() + SEGMENT_LENGTH) until box.maxX() step SEGMENT_LENGTH) {
            val y = getHeightAt(level, x, box.maxZ())
            val pos = BlockPos(x, y, box.maxZ() + 1)
            placeStructure(template, level, pos, Rotation.NONE)
        }

        for (z in (box.minZ() + SEGMENT_LENGTH) until box.maxZ() step SEGMENT_LENGTH) {
            val y = getHeightAt(level, box.minX(), z)
            val pos = BlockPos(box.minX() - WALL_WIDTH, y, z)
            placeStructure(template, level, pos, Rotation.CLOCKWISE_90)
        }

        for (z in (box.minZ() + SEGMENT_LENGTH) until box.maxZ() step SEGMENT_LENGTH) {
            val y = getHeightAt(level, box.maxX(), z)
            val pos = BlockPos(box.maxX() + 1, y, z)
            placeStructure(template, level, pos, Rotation.CLOCKWISE_90)
        }
    }

    private fun placeStructure(
        template: StructureTemplate,
        level: ServerLevel,
        pos: BlockPos,
        rotation: Rotation
    ) {
        val settings = StructurePlaceSettings()
            .setRotation(rotation)
            .setMirror(Mirror.NONE)
            .setIgnoreEntities(true)

        template.placeInWorld(level, pos, pos, settings, level.random, 2)
    }

    private fun getHeightAt(level: ServerLevel, x: Int, z: Int): Int {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1
    }

    private fun isChunkLoaded(level: ServerLevel, pos: BlockPos): Boolean {
        val chunk = level.getChunkAt(pos)
        return (chunk as LevelChunkAccessor).isLoaded
    }
    private fun placeWallSegmentsAround(level: ServerLevel, village: VillageData) {
        val straightTemplate = level.structureManager.getOrCreate(wallStraightStructure)
        val cornerTemplate = level.structureManager.getOrCreate(wallCornerStructure)


        placeCornerStructures(level, village.adjustedBox, cornerTemplate)
        placeStraightWalls(level, village.adjustedBox, straightTemplate)
    }

    fun markVillage(box: BoundingBox): Boolean {
        val center = getCenterOfBoundingBox(box)
        val adjustedBox = getAdjustedBoundingBox(box)

        val key = "${box.minX()},${box.minY()},${box.minZ()},${box.maxX()},${box.maxY()},${box.maxZ()}"
        return if (seenVillages.add(key)) {
            toDecorate.add(VillageData(center, box, adjustedBox))
            true
        } else false
    }

    data class VillageData(
        val villageCenter: BlockPos,
        val boundingBox: BoundingBox,
        val adjustedBox: BoundingBox
    )
}
