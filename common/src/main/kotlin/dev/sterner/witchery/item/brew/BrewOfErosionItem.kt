package dev.sterner.witchery.item.brew

import dev.sterner.witchery.data.ErosionHandler
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.phys.BlockHitResult

class BrewOfErosionItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    @Suppress("UNCHECKED_CAST")
    override fun applyEffectOnBlock(level: Level, blockHit: BlockHitResult, hasFrog: Boolean) {
        if (level.isClientSide) {
            return
        }

        val centerPos = blockHit.blockPos
        val radius = 3
        val positions = collectPositionsInSphere(centerPos, radius)

        for (pos in positions) {

            val blockState = level.getBlockState(pos)
            val fromBlock = blockState.block

            val toBlock = ErosionHandler.EROSION_PAIR[fromBlock]
            if (toBlock != null) {
                var toBlockState = toBlock.defaultBlockState()

                for (property in blockState.properties) {
                    if (toBlockState.hasProperty(property)) {
                        val value = blockState.getValue(property) as Comparable<Any>
                        toBlockState = toBlockState.setValue(property as Property<Comparable<Any>>, value)
                    }
                }

                level.setBlock(pos, toBlockState, 3)
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