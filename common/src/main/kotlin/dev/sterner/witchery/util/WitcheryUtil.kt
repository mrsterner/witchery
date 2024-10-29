package dev.sterner.witchery.util

import net.minecraft.core.Direction
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape



object WitcheryUtil {

    fun rotateShape(from: Direction, to: Direction, shape: VoxelShape): VoxelShape {
        val buffer = arrayOf<VoxelShape>(shape, Shapes.empty())

        val times: Int = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4
        for (i in 0 until times) {
            buffer[0].forAllBoxes { minX, minY, minZ, maxX, maxY, maxZ ->
                buffer[1] = Shapes.join(
                    buffer[1],
                    Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX),
                    BooleanOp.OR
                )
            }
            buffer[0] = buffer[1]
            buffer[1] = Shapes.empty()
        }
        return buffer[0]
    }
}