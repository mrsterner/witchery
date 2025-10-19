package dev.sterner.witchery.content.block.sacrificial_circle


import dev.sterner.witchery.content.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.content.block.cauldron.CauldronBlock.Companion.litBlockEmission
import dev.sterner.witchery.core.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
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
import java.util.function.Supplier

class SacrificialBlock(properties: Properties) : WitcheryBaseEntityBlock(
    properties.noOcclusion().lightLevel(
        litBlockEmission(14)
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

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.SACRIFICIAL_CIRCLE.get().create(pos, state)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0 / 16, 1.0)
    }

    companion object {

        val STRUCTURE: Supplier<MultiBlockStructure> =
            Supplier<MultiBlockStructure> {
                (MultiBlockStructure.of(
                    MultiBlockStructure.StructurePiece(
                        -1,
                        0,
                        0,
                        WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        1,
                        0,
                        0,
                        WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        -1,
                        0,
                        1,
                        WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        1,
                        0,
                        1,
                        WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        0,
                        0,
                        1,
                        WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        1,
                        0,
                        -1,
                        WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        0,
                        0,
                        -1,
                        WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        -1,
                        0,
                        -1,
                        WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get().defaultBlockState()
                    )
                ))
            }

    }
}