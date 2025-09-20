package dev.sterner.witchery.block.effigy

import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import team.lodestar.lodestone.systems.multiblock.MultiblockComponentBlock

class EffigyCompanionBlock(properties: BlockBehaviour.Properties) : MultiblockComponentBlock(properties.noOcclusion()) {

    override fun canBeReplaced(state: BlockState, fluid: Fluid): Boolean {
        return false
    }

    override fun canBeReplaced(state: BlockState, useContext: BlockPlaceContext): Boolean {
        return false
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    companion object {

        private val shapeTop: VoxelShape = box(3.0, 6.0, 3.0, 13.0, 16.0, 13.0)
        private val shapeShaft: VoxelShape = box(4.0, 0.0, 4.0, 12.0, 6.0, 12.0)
        val SHAPE = Shapes.join(shapeTop, shapeShaft, BooleanOp.OR)

    }
}