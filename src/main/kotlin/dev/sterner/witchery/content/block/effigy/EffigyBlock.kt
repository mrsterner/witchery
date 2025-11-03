package dev.sterner.witchery.content.block.effigy


import dev.sterner.witchery.core.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.content.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
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
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.Supplier

class EffigyBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            blockPlaceContext.horizontalDirection.opposite
        )
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.EFFIGY.get().create(pos, state)
    }

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player
    ): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is EffigyBlockEntity && !level.isClientSide) {
            return makeEffigyItem(state, blockEntity)
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
        val be = level.getBlockEntity(pos)
        if (be is EffigyBlockEntity && !level.isClientSide && !player.isCreative && !player.isSpectator) {
            val itemStack = makeEffigyItem(state, be)
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


    private fun makeEffigyItem(state: BlockState, blockEntity: EffigyBlockEntity): ItemStack {
        val baseItem = when {
            state.`is`(WitcheryBlocks.CLAY_EFFIGY.get()) -> WitcheryItems.CLAY_EFFIGY.get().defaultInstance
            state.`is`(WitcheryBlocks.SCARECROW.get()) -> WitcheryItems.SCARECROW.get().defaultInstance
            else -> WitcheryItems.WITCHES_LADDER.get().defaultInstance
        }

        baseItem.set(WitcheryDataComponents.SPIRIT_COUNT.get(), blockEntity.spiritCount)
        baseItem.set(WitcheryDataComponents.BANSHEE_COUNT.get(), blockEntity.bansheeCount)
        baseItem.set(WitcheryDataComponents.SPECTRE_COUNT.get(), blockEntity.specterCount)

        return baseItem
    }


    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val foot = box(4.0, 0.0, 4.0, 12.0, 2.0, 12.0)
        val clay = Shapes.joinUnoptimized(foot, box(5.0, 0.0, 5.0, 11.0, 14.0, 11.0), BooleanOp.OR)
        val scarecrow = box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0)
        return if (state.`is`(WitcheryBlocks.CLAY_EFFIGY.get())) clay else scarecrow
    }

    companion object {
        val STRUCTURE: Supplier<MultiBlockStructure> =
            Supplier<MultiBlockStructure> {
                (MultiBlockStructure.of(
                    MultiBlockStructure.StructurePiece(
                        0,
                        1,
                        0,
                        WitcheryBlocks.EFFIGY_COMPONENT.get().defaultBlockState()
                    )
                ))
            }
    }
}