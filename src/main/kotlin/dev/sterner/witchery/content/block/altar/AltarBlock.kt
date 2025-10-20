package dev.sterner.witchery.content.block.altar

import dev.sterner.witchery.core.api.multiblock.MultiBlockHorizontalDirectionStructure
import dev.sterner.witchery.core.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.content.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.features.misc.AltarLevelAttachment
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.function.Supplier

class AltarBlock(properties: Properties) : WitcheryBaseEntityBlock(properties.noOcclusion()) {

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.ENTITYBLOCK_ANIMATED
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

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.ALTAR.get().create(blockPos, blockState)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, movedByPiston: Boolean) {
        if (level is ServerLevel) {
            AltarLevelAttachment.setAltarPos(level, pos)
        }
        super.onPlace(state, level, pos, oldState, movedByPiston)
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        if (level is ServerLevel) {
            AltarLevelAttachment.removeAltarPos(level, pos)
        }
        Containers.dropItemStack(
            level,
            pos.x + 0.5,
            pos.y + 0.5,
            pos.z + 0.5,
            ItemStack(WitcheryItems.DEEPSLATE_ALTAR_BLOCK.get(), 6)
        )
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    companion object {

        val STRUCTURE: Supplier<MultiBlockHorizontalDirectionStructure> =
            Supplier<MultiBlockHorizontalDirectionStructure> {
                (MultiBlockHorizontalDirectionStructure.of(
                    MultiBlockStructure.StructurePiece(
                        -1,
                        0,
                        0,
                        WitcheryBlocks.ALTAR_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        1,
                        0,
                        0,
                        WitcheryBlocks.ALTAR_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        -1,
                        0,
                        1,
                        WitcheryBlocks.ALTAR_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        1,
                        0,
                        1,
                        WitcheryBlocks.ALTAR_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        0,
                        0,
                        1,
                        WitcheryBlocks.ALTAR_COMPONENT.get().defaultBlockState()
                    )
                ))
            }

    }
}