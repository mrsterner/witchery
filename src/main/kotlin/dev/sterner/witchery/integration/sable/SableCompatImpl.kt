package dev.sterner.witchery.integration.sable

import dev.ryanhcode.sable.companion.SableCompanion
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level
import org.joml.Vector3d

internal object SableCompatImpl {

    fun projectOutOfSubLevel(level: Level, pos: BlockPos): BlockPos {
        return pos
        /*TODO Not necessary?
        val vec3d = Vector3d(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        val projected = SableCompanion.INSTANCE.projectOutOfSubLevel(level, vec3d)
        return BlockPos(projected.x.toInt(), projected.y.toInt(), projected.z.toInt())

         */
    }

    fun distanceSquaredWithSubLevels(level: Level, a: BlockPos, b: BlockPos): Double {
        return SableCompanion.INSTANCE.distanceSquaredWithSubLevels(
            level,
            a.x.toDouble(), a.y.toDouble(), a.z.toDouble(),
            b.x.toDouble(), b.y.toDouble(), b.z.toDouble()
        )
    }
}