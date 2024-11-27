package dev.sterner.witchery.block.effigy

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
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

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.EFFIGY.get().create(pos, state)
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is EffigyBlockEntity) {
            if (!level.isClientSide) {
                val bl = state.`is`(WitcheryBlocks.TRENT_EFFIGY.get())
                val bl2 = state.`is`(WitcheryBlocks.SCARECROW.get())
                val itemStack = if (bl) {
                    WitcheryItems.TRENT_EFFIGY.get().defaultInstance
                } else if (bl2) {
                    WitcheryItems.SCARECROW.get().defaultInstance
                } else  {
                    WitcheryItems.WITCHES_LADDER.get().defaultInstance
                }
                itemStack.set(WitcheryDataComponents.BANSHEE_COUNT.get(), blockEntity.bansheeCount)
                itemStack.set(WitcheryDataComponents.SPECTRE_COUNT.get(), blockEntity.specterCount)
                itemStack.set(WitcheryDataComponents.POLTERGEIST_COUNT.get(), blockEntity.poltergeistCount)

                val itemEntity = ItemEntity(level, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, itemStack)
                itemEntity.setDefaultPickUpDelay()
                level.addFreshEntity(itemEntity)
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0)
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