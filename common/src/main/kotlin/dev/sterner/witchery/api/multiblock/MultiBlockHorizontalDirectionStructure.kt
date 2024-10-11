package dev.sterner.witchery.api.multiblock

import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.state.properties.BlockStateProperties


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
            if (component is MultiBlockComponentBlockEntity) {
                component.corePos = context.clickedPos
            }
        }
    }

    companion object {
        fun of(vararg pieces: StructurePiece): MultiBlockHorizontalDirectionStructure {
            return MultiBlockHorizontalDirectionStructure(ArrayList(listOf(*pieces)))
        }
    }
}
