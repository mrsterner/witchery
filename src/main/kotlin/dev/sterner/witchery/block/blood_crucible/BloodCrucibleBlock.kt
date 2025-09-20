package dev.sterner.witchery.block.blood_crucible


import dev.sterner.witchery.block.WitcheryBaseEntityBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class BloodCrucibleBlock(properties: Properties) : WitcheryBaseEntityBlock(properties.noOcclusion()) {

    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return BloodCrucibleBlockEntity(pos, state)
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape? {
        return SHAPE
    }

    companion object {
        val SHAPE_BASE: VoxelShape = box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0)
        val SHAPE_PILLAR: VoxelShape = box(4.0, 3.0, 4.0, 12.0, 7.0, 12.0)
        val SHAPE_BOTTOM_CRUCIBLE: VoxelShape = box(3.0, 7.0, 3.0, 13.0, 9.0, 13.0)
        val SHAPE_BOTTOM_TOP: VoxelShape = box(2.0, 9.0, 2.0, 14.0, 14.0, 14.0)

        val SHAPE: VoxelShape = Shapes.join(
            SHAPE_BASE,
            Shapes.join(SHAPE_PILLAR,
                Shapes.join(SHAPE_BOTTOM_CRUCIBLE, SHAPE_BOTTOM_TOP,
                    BooleanOp.OR),
                BooleanOp.OR),
            BooleanOp.OR
        )
    }
}