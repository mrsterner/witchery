package dev.sterner.witchery.core.api.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import java.util.function.Consumer


open class MultiBlockStructure(val structurePieces: ArrayList<StructurePiece>) {
    fun canPlace(context: BlockPlaceContext): Boolean {
        return structurePieces.stream().allMatch { p: StructurePiece ->
            p.canPlace(
                context
            )
        }
    }

    open fun place(context: BlockPlaceContext) {
        structurePieces.forEach(Consumer { s: StructurePiece ->
            s.place(
                context.clickedPos,
                context.level
            )
        })
    }

    class StructurePiece(xOffset: Int, yOffset: Int, zOffset: Int, val state: BlockState) {
        val offset: Vec3i = Vec3i(xOffset, yOffset, zOffset)

        fun canPlace(context: BlockPlaceContext): Boolean {
            val level = context.level
            val player: Player? = context.player
            val pos = context.clickedPos.offset(offset)
            val existingState = context.level.getBlockState(pos)
            val collisioncontext = if (player == null) CollisionContext.empty() else CollisionContext.of(player)
            return existingState.canBeReplaced() && level.isUnobstructed(state, pos, collisioncontext)
        }

        fun canPlace(level: Level, player: Player, clickedPos: BlockPos): Boolean {
            val pos = clickedPos.offset(offset)
            val existingState = level.getBlockState(pos)
            val collisioncontext = if (player == null) CollisionContext.empty() else CollisionContext.of(player)
            return existingState.canBeReplaced() && level.isUnobstructed(state, pos, collisioncontext)
        }

        @JvmOverloads
        fun place(core: BlockPos, level: Level, state: BlockState = this.state) {
            val pos = core.offset(offset)
            level.setBlock(pos, state, 3)
            if (level.getBlockEntity(pos) is MultiBlockComponentBlockEntity) {
                val component = level.getBlockEntity(pos) as MultiBlockComponentBlockEntity
                component.corePos = core
            }
        }
    }

    companion object {
        fun of(vararg pieces: StructurePiece): MultiBlockStructure {
            return MultiBlockStructure(ArrayList(listOf(*pieces)))
        }
    }
}