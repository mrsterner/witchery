package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.core.api.Ritual
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.AABB
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level
import kotlin.math.sqrt

class PullMobsRitual : Ritual(Witchery.id("pull_mobs")) {

    private val radius = 4.0

    override fun onTickRitual(level: Level, pos: BlockPos, goldenChalkBlockEntity: GoldenChalkBlockEntity) {
        val entities = level.getEntitiesOfClass(LivingEntity::class.java,
            AABB(
                pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius
            )
        ) { it is Enemy }

        val center = Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)

        for (entity in entities) {
            val entityPos = entity.position()
            val dx = entityPos.x - center.x
            val dy = entityPos.y - center.y
            val dz = entityPos.z - center.z
            val distance = sqrt(dx * dx + dy * dy + dz * dz)

            if (distance > radius) {
                val pullStrength = 0.25
                val pullX = -(dx / distance) * pullStrength
                val pullY = -(dy / distance) * pullStrength
                val pullZ = -(dz / distance) * pullStrength

                entity.deltaMovement = entity.deltaMovement.add(pullX, pullY, pullZ)
            }
        }
    }
}
