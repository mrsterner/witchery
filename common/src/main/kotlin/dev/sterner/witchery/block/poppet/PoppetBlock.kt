package dev.sterner.witchery.block.poppet

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.block.arthana.ArthanaBlockEntity
import dev.sterner.witchery.platform.poppet.PoppetDataAttachment
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.util.WitcheryUtil
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment.Server
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
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class PoppetBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.POPPET.get().create(pos, state)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }


    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            blockPlaceContext.horizontalDirection.opposite
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        val shape = when (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction.NORTH -> SHAPE_NORTH
            Direction.SOUTH -> SHAPE_SOUTH
            Direction.WEST -> SHAPE_WEST
            Direction.EAST -> SHAPE_EAST
            else -> SHAPE_NORTH
        }

        return shape
    }

    override fun getCloneItemStack(level: LevelReader, pos: BlockPos, state: BlockState): ItemStack {
        val be = level.getBlockEntity(pos)
        if (be is PoppetBlockEntity)
            return be.poppetItemStack.copy()
        return WitcheryItems.POPPET.get().defaultInstance
    }

    override fun playerDestroy(
        level: Level,
        player: Player,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        tool: ItemStack
    ) {
        if (level is ServerLevel && blockEntity is PoppetBlockEntity) {
            val itemStack = PoppetDataAttachment.getPoppet(level, pos)
            if (itemStack != null) {
                val item = ItemEntity(level, pos.center.x, pos.center.y, pos.center.z, itemStack.copy())
                level.addFreshEntity(item)
            }
        }

        super.playerDestroy(level, player, pos, state, blockEntity, tool)
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        if (level is ServerLevel) {
            PoppetDataAttachment.handleBlockDestruction(level, pos)
        }

        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun canBeReplaced(state: BlockState, fluid: Fluid): Boolean {
        return false
    }

    override fun canBeReplaced(state: BlockState, useContext: BlockPlaceContext): Boolean {
        return false
    }

    companion object {
        val SHAPE_SOUTH: VoxelShape = box(4.0, 3.0, 13.0, 12.0, 13.0, 16.0)
        val SHAPE_NORTH: VoxelShape = WitcheryUtil.rotateShape(Direction.SOUTH, Direction.NORTH, SHAPE_SOUTH)
        val SHAPE_WEST: VoxelShape = WitcheryUtil.rotateShape(Direction.SOUTH, Direction.WEST, SHAPE_SOUTH)
        val SHAPE_EAST: VoxelShape = WitcheryUtil.rotateShape(Direction.SOUTH, Direction.EAST, SHAPE_SOUTH)
    }
}