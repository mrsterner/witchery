package dev.sterner.witchery.content.block.soul_cage


import dev.sterner.witchery.content.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.content.block.cauldron.WitcheryCauldronBlock.Companion.litBlockEmission
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class SoulCageBlock(properties: Properties) : WitcheryBaseEntityBlock(
    properties.lightLevel(
        litBlockEmission(5)
    )
) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.LIT, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.LIT)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return SoulCageBlockEntity(pos, state)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.box(3.0 / 16.0, 0.0 / 16.0, 3.0 / 16.0, 13.0 / 16.0, 12.0 / 16.0, 13.0 / 16.0)
    }
}