package dev.sterner.witchery.block.phylactery


import dev.sterner.witchery.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.block.censer.CenserBlock
import dev.sterner.witchery.block.censer.CenserBlock.Companion.TYPE
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.AbstractCandleBlock.LIT
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LanternBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.WaterloggedTransparentBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.ToIntFunction

class PhylacteryBlock(properties: Properties) : WitcheryBaseEntityBlock(properties.lightLevel(
    litBlockEmission(8)
)), SimpleWaterloggedBlock {

    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(LanternBlock.WATERLOGGED, false).setValue(LIT, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(LanternBlock.WATERLOGGED, TYPE, LIT)
    }

    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return PhylacteryBlockEntity(pos, state)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0)
    }

    companion object {
        fun litBlockEmission(lightValue: Int): ToIntFunction<BlockState> {
            return ToIntFunction { blockState: BlockState ->
                if (blockState.getValue(
                        LIT
                    ) == true
                ) lightValue else 0
            }
        }
    }
}