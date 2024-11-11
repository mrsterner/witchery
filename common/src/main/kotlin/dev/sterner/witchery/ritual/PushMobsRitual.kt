package dev.sterner.witchery.ritual

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.block.ritual.GoldenChalkBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import kotlin.math.sqrt

class PushMobsRitual : Ritual(Witchery.id("push_mobs")) {

    override fun onTickRitual(level: Level, pos: BlockPos, goldenChalkBlockEntity: GoldenChalkBlockEntity) {
        val entities = level.getEntitiesOfClass(LivingEntity::class.java, AABB(pos).inflate(16.0, 6.0, 16.0)) {
            it is Enemy
        }

        for (entity in entities) {
            val entityPos = entity.position()
            val dx = entityPos.x - pos.x
            val dz = entityPos.z - pos.z
            val distance = sqrt(dx * dx + dz * dz)

            if (distance > 0) {
                val strength = 0.1
                val pushX = (dx / distance) * strength
                val pushZ = (dz / distance) * strength

                entity.deltaMovement = entity.deltaMovement.add(pushX, 0.083, pushZ)
            }
        }
    }
}