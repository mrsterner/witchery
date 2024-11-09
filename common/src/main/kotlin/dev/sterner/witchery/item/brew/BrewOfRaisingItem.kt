package dev.sterner.witchery.item.brew

import com.google.common.base.Predicate
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class BrewOfRaisingItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties, Predicate { it == Direction.UP }) {

    override fun applyEffectOnBlock(level: Level, blockHit: BlockHitResult) {
        val belowPos = blockHit.blockPos

        level.destroyBlock(belowPos, true)

        val random = level.random.nextFloat()

        val entity = when {
            random < 0.5f -> EntityType.ZOMBIE.create(level)
            random < 0.9f -> EntityType.SKELETON.create(level)
            else -> EntityType.ZOMBIFIED_PIGLIN.create(level)
        }

        entity?.moveTo(belowPos.center)
        entity?.let { level.addFreshEntity(it) }

    }
}