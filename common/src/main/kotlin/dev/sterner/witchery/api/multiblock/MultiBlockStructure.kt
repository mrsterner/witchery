package dev.sterner.witchery.api.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext


open class MultiBlockStructure(val structurePieces: ArrayList<StructurePiece>) {

    fun canPlace(context: BlockPlaceContext): Boolean {
        val direction = context.horizontalDirection.opposite
        return structurePieces.all { piece ->
            val rotatedOffset = rotateOffset(piece.offset, direction)
            val targetPos = context.clickedPos.offset(rotatedOffset)
            piece.canPlaceAtPosition(context, targetPos)
        }
    }

    open fun place(context: BlockPlaceContext) {
        val direction = context.horizontalDirection.opposite
        structurePieces.forEach { piece ->
            val rotatedOffset = rotateOffset(piece.offset, direction)
            piece.place(context.clickedPos.offset(rotatedOffset), context.level, piece.state)
        }
    }


    class StructurePiece(xOffset: Int, yOffset: Int, zOffset: Int, val state: BlockState) {
        val offset: Vec3i = Vec3i(xOffset, yOffset, zOffset)

        fun canPlaceAtPosition(context: BlockPlaceContext, pos: BlockPos): Boolean {
            val level = context.level
            val player = context.player
            val existingState = level.getBlockState(pos)
            val collisionContext = if (player == null) CollisionContext.empty() else CollisionContext.of(player)
            return existingState.canBeReplaced() && level.isUnobstructed(state, pos, collisionContext)
        }

        fun place(pos: BlockPos, level: Level, state: BlockState?) {
            level.setBlock(pos, state, 3)
            val component = level.getBlockEntity(pos)
            if (component is MultiBlockComponentBlockEntity) {
                component.corePos = pos // Store the core position for reference
            }
        }
    }

    companion object {
        fun of(vararg pieces: StructurePiece): MultiBlockStructure {
            return MultiBlockStructure(ArrayList(listOf(*pieces)))
        }

        fun rotateOffset(offset: Vec3i, direction: Direction): Vec3i {
            return when (direction) {
                Direction.NORTH -> Vec3i(offset.x, offset.y, offset.z)
                Direction.SOUTH -> Vec3i(-offset.x, offset.y, -offset.z)
                Direction.WEST -> Vec3i(offset.z, offset.y, -offset.x)
                Direction.EAST -> Vec3i(-offset.z, offset.y, offset.x)
                else -> offset
            }
        }
    }
}