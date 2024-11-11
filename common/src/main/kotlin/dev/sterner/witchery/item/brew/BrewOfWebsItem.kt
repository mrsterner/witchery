package dev.sterner.witchery.item.brew

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

class BrewOfWebsItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun applyEffectOnEntities(level: Level, livingEntity: LivingEntity, hasFrog: Boolean) {
        livingEntity.addEffect(MobEffectInstance(MobEffects.WEAVING, 20 * 20, 0))
    }

    override fun applyEffectOnHitLocation(level: Level, location: Vec3, hasFrog: Boolean) {
        val blockPos = BlockPos.containing(location)
        val block = level.getBlockState(blockPos)
        if (block.isAir) {
            level.setBlockAndUpdate(blockPos, Blocks.COBWEB.defaultBlockState())
        }

        val list = collectPositionsInDullSphere(blockPos, 2)
        for (pos in list) {
            val extraPos = level.getBlockState(pos)
            if (level.random.nextDouble() > 0.75 && (extraPos.canBeReplaced() || extraPos.isAir)) {
                level.setBlockAndUpdate(pos, Blocks.COBWEB.defaultBlockState())
            }
        }
    }

    companion object {
        @JvmStatic
        fun collectPositionsInDullSphere(center: BlockPos, radius: Int): List<BlockPos> {
            val positions = mutableListOf<BlockPos>()
            for (x in -radius..radius) {
                for (y in -radius + 1..<radius) {
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