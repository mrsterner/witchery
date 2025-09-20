package dev.sterner.witchery.block.werewolf_altar

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.function.Supplier

class WerewolfAltarBlock(properties: Properties) : WitcheryBaseEntityBlock(properties.noOcclusion()) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        val bl = blockPlaceContext.player?.isShiftKeyDown ?: false
        return defaultBlockState().setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            if (bl) blockPlaceContext.horizontalDirection.opposite else blockPlaceContext.horizontalDirection
        )
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return WerewolfAltarBlockEntity(pos, state)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    companion object {
        val STRUCTURE: Supplier<MultiBlockStructure> =
            Supplier<MultiBlockStructure> {
                (MultiBlockStructure.of(
                    MultiBlockStructure.StructurePiece(
                        0,
                        1,
                        0,
                        WitcheryBlocks.WEREWOLF_ALTAR_COMPONENT.get().defaultBlockState()
                    )
                ))
            }
    }
}