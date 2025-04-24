package dev.sterner.witchery.handler

import dev.sterner.witchery.Witchery
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.BoundingBox

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.Mirror
object VillageWallHandler {
    private val seenVillages: MutableSet<String> = mutableSetOf()
    private val toDecorate: MutableList<BoundingBox> = mutableListOf()

    private val wallStraightStructure = Witchery.id("wall_straight") // 8x7x5
    private val wallCornerStructure = Witchery.id("wall_corner") // 7x8x7

    private const val SEGMENT_LENGTH = 8
    private const val WALL_HEIGHT = 16
    private const val WALL_WIDTH = 5

    fun markVillage(box: BoundingBox): Boolean {
        val key = "${box.minX()},${box.minY()},${box.minZ()},${box.maxX()},${box.maxY()},${box.maxZ()}"
        return if (seenVillages.add(key)) {
            toDecorate.add(box)
            true
        } else false
    }

    fun tick(level: ServerLevel) {
        if (toDecorate.isEmpty()) return
        val copy = ArrayList(toDecorate)
        toDecorate.clear()

        for (box in copy) {
            placeWallSegmentsAround(level, box)
        }
    }

    private fun placeWallSegmentsAround(level: ServerLevel, box: BoundingBox) {
        val straightTemplate = level.structureManager.getOrCreate(wallStraightStructure)
        val cornerTemplate = level.structureManager.getOrCreate(wallCornerStructure)

        val newBox = getExpandedBoundingBox(box)

        // Place corner structures at the 4 corners of the expanded bounding box
        placeCornerStructures(level, newBox.minX(), newBox.maxX(), newBox.minZ(), newBox.maxZ(), cornerTemplate)

        // Place straight walls along the sides of the expanded bounding box, avoiding overlap with corners
        placeStraightWalls(level, newBox.minX(), newBox.maxX(), newBox.minZ(), newBox.maxZ(), straightTemplate)
    }

    private fun getExpandedBoundingBox(box: BoundingBox): BoundingBox {
        val expandedMinX = box.minX() - WALL_WIDTH
        val expandedMaxX = box.maxX() + WALL_WIDTH
        val expandedMinZ = box.minZ() - WALL_WIDTH
        val expandedMaxZ = box.maxZ() + WALL_WIDTH

        return BoundingBox(expandedMinX, box.minY(), expandedMinZ, expandedMaxX, box.maxY(), expandedMaxZ)
    }

    private fun placeCornerStructures(level: ServerLevel, minX: Int, maxX: Int, minZ: Int, maxZ: Int, template: StructureTemplate) {
        val cornerPositions = listOf(
            BlockPos(minX, getHeightAt(level, minX, minZ), minZ), // NW corner
            BlockPos(minX, getHeightAt(level, minX, maxZ), maxZ), // SW corner
            BlockPos(maxX, getHeightAt(level, maxX, minZ), minZ), // NE corner
            BlockPos(maxX, getHeightAt(level, maxX, maxZ), maxZ)  // SE corner
        )

        for (pos in cornerPositions) {
            placeStructure(template, level, pos, Rotation.NONE)
        }
    }

    private fun placeStraightWalls(level: ServerLevel, minX: Int, maxX: Int, minZ: Int, maxZ: Int, template: StructureTemplate) {
        // North side (straight wall)
        for (x in (minX + SEGMENT_LENGTH) until maxX step SEGMENT_LENGTH) {
            if (x + SEGMENT_LENGTH < maxX) {
                val y = getHeightAt(level, x, minZ)
                val pos = BlockPos(x, y, minZ - WALL_WIDTH)
                placeStructure(template, level, pos, Rotation.NONE)
            }
        }

        // South side (straight wall)
        for (x in (minX + SEGMENT_LENGTH) until maxX step SEGMENT_LENGTH) {
            if (x + SEGMENT_LENGTH < maxX) {
                val y = getHeightAt(level, x, maxZ)
                val pos = BlockPos(x, y, maxZ + 1)
                placeStructure(template, level, pos, Rotation.NONE)
            }
        }

        // West side (straight wall)
        for (z in (minZ + SEGMENT_LENGTH) until maxZ step SEGMENT_LENGTH) {
            if (z + SEGMENT_LENGTH < maxZ) {
                val y = getHeightAt(level, minX, z)
                val pos = BlockPos(minX - WALL_WIDTH, y, z)
                placeStructure(template, level, pos, Rotation.CLOCKWISE_90)
            }
        }

        // East side (straight wall)
        for (z in (minZ + SEGMENT_LENGTH) until maxZ step SEGMENT_LENGTH) {
            if (z + SEGMENT_LENGTH < maxZ) {
                val y = getHeightAt(level, maxX, z)
                val pos = BlockPos(maxX + 1, y, z)
                placeStructure(template, level, pos, Rotation.CLOCKWISE_90)
            }
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
}
