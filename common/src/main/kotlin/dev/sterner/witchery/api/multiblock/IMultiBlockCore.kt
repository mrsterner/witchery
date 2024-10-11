package dev.sterner.witchery.api.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level


interface IMultiBlockCore {
    val componentPositions: ArrayList<BlockPos?>

    val structure: MultiBlockStructure?

    fun setupMultiBlock(pos: BlockPos, direction: Direction) {
        if (structure == null) {
            return
        }
        structure!!.structurePieces.forEach { piece ->
            val rotatedOffset = MultiBlockStructure.rotateOffset(piece.offset, direction)
            componentPositions.add(pos.offset(rotatedOffset))
        }
    }

    fun destroyMultiBlock(player: Player?, level: Level, pos: BlockPos?) {
        componentPositions.forEach { p ->
            if (p != null && level.getBlockEntity(p) is MultiBlockComponentBlockEntity) {
                level.destroyBlock(p, false)
            }
        }
        val dropBlock = player == null || !player.isCreative
        if (pos != null && level.getBlockEntity(pos) is MultiBlockCoreEntity) {
            level.destroyBlock(pos, dropBlock)
        }
    }
}