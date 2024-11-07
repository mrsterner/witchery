package dev.sterner.witchery.item.brew

import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.block.altar.AltarBlock
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlock
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.BlockTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DoorHingeSide
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class BrewOfFlowingSpiritItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        return InteractionResultHolder.pass(player.mainHandItem)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val clickedPos = context.clickedPos
        val state = level.getBlockState(clickedPos)

        if (state.block is DoorBlock && state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            val half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF)
            val direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING)

            // Get the position of the other half of the clicked door
            val otherHalfPos = if (half == DoubleBlockHalf.LOWER) clickedPos.above() else clickedPos.below()

            // Determine the potential positions for adjacent doors (left and right of the clicked door)
            val leftDoorPos = clickedPos.relative(direction.counterClockWise)
            val rightDoorPos = clickedPos.relative(direction.clockWise)

            // Check if there's a door at either the left or right position
            val leftState = level.getBlockState(leftDoorPos)
            val rightState = level.getBlockState(rightDoorPos)

            val adjacentDoorPos = when {
                leftState.block is DoorBlock && leftState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == half -> leftDoorPos
                rightState.block is DoorBlock && rightState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == half -> rightDoorPos
                else -> null
            }

            if (adjacentDoorPos != null) {
                // Position of the other half of the adjacent door
                val adjacentOtherHalfPos = if (half == DoubleBlockHalf.LOWER) adjacentDoorPos.above() else adjacentDoorPos.below()

                // Determine the bottom positions of both doors
                val bottomPos1 = if (half == DoubleBlockHalf.LOWER) clickedPos else clickedPos.below()
                val bottomPos2 = if (half == DoubleBlockHalf.LOWER) adjacentDoorPos else adjacentDoorPos.below()

                // Determine the leftmost bottom position based on the door's facing direction
                val leftMostBottomPos = when (direction) {
                    Direction.NORTH, Direction.SOUTH -> if (bottomPos1.x < bottomPos2.x) bottomPos1 else bottomPos2
                    Direction.EAST, Direction.WEST -> if (bottomPos1.z < bottomPos2.z) bottomPos1 else bottomPos2
                    else -> bottomPos1
                }

                // Call makePortal with the leftmost bottom position and the door's facing direction
                makePortal(level, leftMostBottomPos, direction)

                return InteractionResult.SUCCESS
            }
        }

        return super.useOn(context)
    }

    fun makePortal(level: Level, pos: BlockPos, direction: Direction) {
        // Determine the core position based on the facing direction
        val corePosition = when (direction) {
            Direction.NORTH -> pos.relative(Direction.WEST)
            Direction.SOUTH -> pos.relative(Direction.EAST)
            Direction.EAST -> pos.relative(Direction.NORTH)
            Direction.WEST -> pos.relative(Direction.SOUTH)
            else -> pos
        }

        // Place the portal structure
        SpiritPortalBlock.STRUCTURE.get().placeNoContext(level, pos, direction)

        // Set the Spirit Portal block at the given position, facing the opposite direction
        level.setBlockAndUpdate(
            pos,
            WitcheryBlocks.SPIRIT_PORTAL.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, direction.opposite)
        )

        // Set the core position in the MultiBlockComponentBlockEntity, if present
        if (level.getBlockEntity(corePosition) is MultiBlockComponentBlockEntity) {
            (level.getBlockEntity(corePosition) as MultiBlockComponentBlockEntity).corePos = corePosition
        }
    }

    override fun applyEffect(level: Level, livingEntity: LivingEntity?, result: HitResult) {
        var pos = BlockPos.containing(result.location)
        if (level.getBlockState(pos).canBeReplaced()) {
            level.setBlockAndUpdate(pos, WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get().defaultBlockState())
        } else {
            if (result.type == HitResult.Type.BLOCK) {
                val blockHitResult = result as BlockHitResult
                pos = pos.relative(blockHitResult.direction)
            }
            level.setBlockAndUpdate(pos, WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get().defaultBlockState())
        }
    }
}