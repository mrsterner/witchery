package dev.sterner.witchery.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import team.lodestar.lodestone.systems.multiblock.MultiBlockComponentEntity
import team.lodestar.lodestone.systems.multiblock.MultiBlockStructure


class MultiBlockHorizontalDirectionStructure(structurePieces: ArrayList<StructurePiece>?) :
    MultiBlockStructure(structurePieces!!) {

    override fun place(context: BlockPlaceContext) {
        val direction = context.horizontalDirection.opposite
        structurePieces.forEach { s: StructurePiece ->
            val rotatedOffset = rotateOffset(s.offset, direction)
            val pos = context.clickedPos.offset(rotatedOffset)
            val stateWithDirection = s.state.setValue(BlockStateProperties.HORIZONTAL_FACING, direction)
            context.level.setBlock(pos, stateWithDirection, 3)

            val component = context.level.getBlockEntity(pos)
            if (component is MultiBlockComponentEntity) {
                component.corePos = context.clickedPos
            }
        }
    }

    fun placeNoContext(level: Level, pos: BlockPos, horizontalDirection: Direction) {
        val direction = horizontalDirection.opposite
        structurePieces.forEach { s: StructurePiece ->
            val rotatedOffset = rotateOffset(s.offset, direction)
            val componentPos = pos.offset(rotatedOffset)
            val stateWithDirection = s.state.setValue(BlockStateProperties.HORIZONTAL_FACING, direction)
            level.setBlock(componentPos, stateWithDirection, 3)

            // Check if the block entity is a MultiblockComponentBlockEntity
            val component = level.getBlockEntity(componentPos)
            if (component is MultiBlockComponentEntity) {
                component.corePos = pos // Set corePos to the main altar position
            }
        }
    }

    companion object {
        fun of(vararg pieces: StructurePiece): MultiBlockHorizontalDirectionStructure {
            return MultiBlockHorizontalDirectionStructure(ArrayList(listOf(*pieces)))
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