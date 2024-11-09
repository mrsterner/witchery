package dev.sterner.witchery.item.brew

import dev.sterner.witchery.data.ErosionHandler
import dev.sterner.witchery.entity.BansheeEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class BrewOfErosionItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun applyEffectOnBlock(level: Level, blockHit: BlockHitResult) {
        if (level.isClientSide) {
            return
        }

        val centerPos = blockHit.blockPos
        val radius = 3
        val positions = collectPositionsInSphere(centerPos, radius)

        val convertedPositions = mutableSetOf<BlockPos>()

        for (pos in positions) {
            if (pos in convertedPositions) continue // Skip already converted positions

            val blockState = level.getBlockState(pos)
            val fromBlock = blockState.block

            val toBlock = ErosionHandler.EROSION_PAIR[fromBlock]
            if (toBlock != null) {
                var toBlockState = toBlock.defaultBlockState()

                if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS) && toBlockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
                    toBlockState = toBlockState.setValue(BlockStateProperties.HORIZONTAL_AXIS, blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS))
                }

                if (blockState.hasProperty(BlockStateProperties.AXIS) && toBlockState.hasProperty(BlockStateProperties.AXIS)) {
                    toBlockState = toBlockState.setValue(BlockStateProperties.AXIS, blockState.getValue(BlockStateProperties.AXIS))
                }

                if (blockState.hasProperty(BlockStateProperties.SLAB_TYPE) && toBlockState.hasProperty(BlockStateProperties.SLAB_TYPE)) {
                    toBlockState = toBlockState.setValue(BlockStateProperties.SLAB_TYPE, blockState.getValue(BlockStateProperties.SLAB_TYPE))
                }

                if (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && toBlockState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                    toBlockState = toBlockState.setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED))
                }

                if (blockState.hasProperty(BlockStateProperties.STAIRS_SHAPE) && toBlockState.hasProperty(BlockStateProperties.STAIRS_SHAPE)) {
                    toBlockState = toBlockState.setValue(BlockStateProperties.STAIRS_SHAPE, blockState.getValue(BlockStateProperties.STAIRS_SHAPE))
                }

                if (blockState.hasProperty(BlockStateProperties.HALF) && toBlockState.hasProperty(BlockStateProperties.HALF)) {
                    toBlockState = toBlockState.setValue(BlockStateProperties.HALF, blockState.getValue(BlockStateProperties.HALF))
                }

                if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && toBlockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                    toBlockState = toBlockState.setValue(BlockStateProperties.HORIZONTAL_FACING, blockState.getValue(BlockStateProperties.HORIZONTAL_FACING))
                }

                if (blockState.hasProperty(BlockStateProperties.FACING) && toBlockState.hasProperty(BlockStateProperties.FACING)) {
                    toBlockState = toBlockState.setValue(BlockStateProperties.FACING, blockState.getValue(BlockStateProperties.FACING))
                }

                level.setBlock(pos, toBlockState, 3)
                convertedPositions.add(pos)
            }
        }
    }

    companion object {
        @JvmStatic
        fun collectPositionsInSphere(center: BlockPos, radius: Int): List<BlockPos> {
            val positions = mutableListOf<BlockPos>()
            for (x in -radius..radius) {
                for (y in -radius..radius) {
                    for (z in -radius..radius) {
                        val offset = BlockPos(x, y, z)
                        if (offset.distSqr(Vec3i.ZERO) <= radius * radius) {
                            positions.add(center.offset(offset))
                        }
                    }
                }
            }
            return positions
        }
    }
}