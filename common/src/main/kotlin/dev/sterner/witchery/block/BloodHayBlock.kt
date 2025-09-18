package dev.sterner.witchery.block

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.HayBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.Heightmap
import kotlin.math.max

class BloodHayBlock(properties: Properties) : HayBlock(properties) {

    data class Coord(val x: Int, val y: Int)

    private val structure: List<Coord> = buildList {
        addAll(listOf(Coord(1,0), Coord(2,0)))
        addAll(listOf(Coord(1,1), Coord(2,1)))
        addAll(listOf(Coord(1,2), Coord(2,2)))
        addAll(listOf(Coord(0,3), Coord(1,3), Coord(2,3), Coord(3,3)))
        addAll(listOf(Coord(0,4), Coord(1,4), Coord(2,4), Coord(3,4)))
        addAll(listOf(Coord(1,5), Coord(2,5)))
        addAll(listOf(Coord(1,6), Coord(2,6)))
    }

    override fun onPlace(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        oldState: BlockState,
        movedByPiston: Boolean
    ) {
        super.onPlace(state, level, pos, oldState, movedByPiston)
        onBloodHayPlaced(level, pos)
    }

    private fun onBloodHayPlaced(level: Level, pos: BlockPos) {
        if (checkBloodHayStructure(level, pos)) {
            trySpawnHorned(level, pos)
        }
    }

    private fun checkBloodHayStructure(level: Level, placedPos: BlockPos): Boolean {
        val orientations = listOf<(Int, Int) -> Pair<Int, Int>>(
            { x, z -> Pair(x, z) },
            { x, z -> Pair(-x, z) },
            { x, z -> Pair(z, x) },
            { x, z -> Pair(z, -x) }
        )

        for (orient in orientations) {
            for (anchor in structure) {
                val (ax, az) = orient(anchor.x, 0)

                val baseX = placedPos.x - ax
                val baseY = placedPos.y - anchor.y
                val baseZ = placedPos.z - az

                var allMatch = true
                for (coord in structure) {
                    val (dx, dz) = orient(coord.x, 0)
                    val checkPos = BlockPos(baseX + dx, baseY + coord.y, baseZ + dz)
                    val block = level.getBlockState(checkPos).block
                    if (block !is BloodHayBlock) {
                        allMatch = false
                        break
                    }
                }

                if (allMatch) {
                    trySpawnHorned(level, BlockPos(baseX, baseY, baseZ))
                    return true
                }
            }
        }
        return false
    }

    private fun trySpawnHorned(level: Level, basePos: BlockPos) {
        if (level.isClientSide) return

        val random = level.random
        var spawnPos: BlockPos? = null

        for (i in 0 until 50) {
            val dx = random.nextInt(41) - 20
            val dz = random.nextInt(41) - 20
            val x = basePos.x + dx
            val z = basePos.z + dz
            val y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z)
            val candidate = BlockPos(x, y, z)

            if (level.getBlockState(candidate.below()).isSolid &&
                level.getBlockState(candidate).isAir
            ) {
                spawnPos = candidate
                break
            }
        }

        if (spawnPos == null) {
            return
        }


        val orientations = listOf<(Int, Int) -> Pair<Int, Int>>(
            { x, z -> Pair(x, z) },
            { x, z -> Pair(-x, z) },
            { x, z -> Pair(z, x) },
            { x, z -> Pair(z, -x) }
        )
        for (orient in orientations) {
            for (anchor in structure) {
                val (ax, az) = orient(anchor.x, 0)
                val baseX = basePos.x
                val baseY = basePos.y
                val baseZ = basePos.z

                for (coord in structure) {
                    val (dx, dz) = orient(coord.x, 0)
                    val checkPos = BlockPos(baseX + dx, baseY + coord.y, baseZ + dz)
                    if (level.getBlockState(checkPos).block is BloodHayBlock) {
                        level.destroyBlock(checkPos, false)
                    }
                }
            }
        }

        val huntsman = WitcheryEntityTypes.HORNED_HUNTSMAN.get().create(level)
        if (huntsman != null) {
            huntsman.moveTo(spawnPos.x + 0.5, spawnPos.y.toDouble(), spawnPos.z + 0.5, random.nextFloat() * 360f, 0f)
            level.addFreshEntity(huntsman)
        }
    }
}
