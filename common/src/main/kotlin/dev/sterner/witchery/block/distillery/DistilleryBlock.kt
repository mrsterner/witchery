package dev.sterner.witchery.block.distillery

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.api.multiblock.MultiBlockHorizontalDirectionStructure
import dev.sterner.witchery.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.Supplier

class DistilleryBlock(properties: Properties) : WitcheryBaseEntityBlock(properties.noOcclusion()) {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.DISTILLERY.get().create(pos, state)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return shape
    }

    
    companion object {
        val bottomShape = box(2.0, 0.0, 2.0, 14.0, 5.0, 14.0)
        val coreShape = box(4.0, 5.0, 4.0, 12.0, 16.0, 12.0)
        val shape = Shapes.join(bottomShape, coreShape, BooleanOp.OR)

        val STRUCTURE: Supplier<MultiBlockStructure> =
            Supplier<MultiBlockStructure> {
                (MultiBlockStructure.of(
                    MultiBlockStructure.StructurePiece(
                        0,
                        1,
                        0,
                        WitcheryBlocks.DISTILLERY_COMPONENT.get().defaultBlockState()
                    )
                ))
            }
    }
}