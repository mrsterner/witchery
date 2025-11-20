package dev.sterner.witchery.content.block.mirror

import dev.sterner.witchery.content.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.content.block.effigy.EffigyBlockEntity
import dev.sterner.witchery.content.item.MirrorItem
import dev.sterner.witchery.core.api.multiblock.MultiBlockHorizontalDirectionStructure
import dev.sterner.witchery.core.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.Supplier

class MirrorBlock : WitcheryBaseEntityBlock(Properties.of()) {

    init {
        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue<Direction?, Direction?>(DoorBlock.FACING, Direction.NORTH)
        )
    }

    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return MirrorBlockEntity(pos, state)
    }

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player
    ): ItemStack {
        val stack = WitcheryItems.MIRROR.get().defaultInstance
        val be = level.getBlockEntity(pos)
        if (be is MirrorBlockEntity && be.linkedMirror != null) {
            MirrorItem.setLinkedPos(stack, be.linkedMirror!!)
            return stack
        }
        return super.getCloneItemStack(state, target, level, pos, player)
    }

    override fun playerDestroy(
        level: Level,
        player: Player,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        tool: ItemStack
    ) {
        if (blockEntity is MirrorBlockEntity && !level.isClientSide && !player.isCreative && !player.isSpectator) {
            val itemStack = WitcheryItems.MIRROR.get().defaultInstance
            if (blockEntity.linkedMirror != null) {
                MirrorItem.setLinkedPos(itemStack, blockEntity.linkedMirror!!)
            }
            val itemEntity = ItemEntity(
                level,
                pos.x + 0.5,
                pos.y + 0.5,
                pos.z + 0.5,
                itemStack
            )
            itemEntity.setDefaultPickUpDelay()
            level.addFreshEntity(itemEntity)

        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val blockPos = context.clickedPos
        val level = context.level
        return if (blockPos.y < level.maxBuildHeight - 1 && level.getBlockState(blockPos.above()).canBeReplaced(context)) {
            this.defaultBlockState().setValue(DoorBlock.FACING, context.horizontalDirection)
        } else {
            null
        }
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(DoorBlock.FACING)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val direction = state.getValue(FACING)

        return when (direction) {
            Direction.SOUTH -> SOUTH_AABB
            Direction.WEST ->  WEST_AABB
            Direction.NORTH -> NORTH_AABB
            else -> EAST_AABB
        }
    }
    
    companion object {

        val STRUCTURE: Supplier<MultiBlockHorizontalDirectionStructure> =
            Supplier<MultiBlockHorizontalDirectionStructure> {
                (MultiBlockHorizontalDirectionStructure.of(
                    MultiBlockStructure.StructurePiece(
                        0,
                        1,
                        0,
                        WitcheryBlocks.MIRROR_COMPONENT.get().defaultBlockState()
                    )
                ))
            }

        val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING

        val SOUTH_AABB: VoxelShape = box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0)
        val NORTH_AABB: VoxelShape = box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0)
        val WEST_AABB: VoxelShape = box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0)
        val EAST_AABB: VoxelShape = box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0)
    }
}