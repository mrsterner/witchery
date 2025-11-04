package dev.sterner.witchery.features.brazier

import dev.sterner.witchery.content.block.brazier.BrazierBlockEntity
import dev.sterner.witchery.core.api.BrazierPassive
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.phys.AABB

class DrainGrowthBrazierPassive : BrazierPassive("drain_growth") {

    override fun onTickBrazier(level: Level, pos: BlockPos, blockEntity: BrazierBlockEntity) {
        super.onTickBrazier(level, pos, blockEntity)

        val radius = 16
        val affectedArea = BlockPos.betweenClosed(
            pos.offset(-radius, -2, -radius),
            pos.offset(radius, 2, radius)
        )

        val entitiesToHeal = level.getEntitiesOfClass(
            LivingEntity::class.java,
            AABB(pos).inflate(radius.toDouble())
        ).filter { it.health < it.maxHealth }

        if (entitiesToHeal.isEmpty()) return

        for (cropPos in affectedArea) {
            val blockState = level.getBlockState(cropPos)
            if (blockState.hasProperty(CropBlock.AGE)) {
                val age = blockState.getValue(CropBlock.AGE)

                if (age > 0 && entitiesToHeal.isNotEmpty()) {
                    val entity = entitiesToHeal.firstOrNull { it.health < it.maxHealth } ?: continue
                    entity.heal(1f)

                    val newAge = age - 1
                    if (newAge <= 0) {
                        level.removeBlock(cropPos, false)
                    } else {
                        level.setBlock(cropPos, blockState.setValue(CropBlock.AGE, newAge), 3)
                    }
                }
            }
        }
    }
}
