package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import dev.sterner.witchery.features.petrification.PetrificationHandler
import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity


class PetrificationRitual : Ritual("petrification") {

    companion object {
        private const val RITUAL_RADIUS = 10.0
        private const val PETRIFICATION_DURATION = 20 * 60
    }

    override fun onStartRitual(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ): Boolean {
        if (level !is ServerLevel) return false

        val box = AABB.ofSize(blockPos.center, RITUAL_RADIUS * 2, RITUAL_RADIUS * 2, RITUAL_RADIUS * 2)
        val entities = level.getEntitiesOfClass(LivingEntity::class.java, box) { entity ->
            entity != null && entity.isAlive && !entity.isSpectator
        }

        if (entities.isEmpty()) {
            return false
        }

        entities.forEach { entity ->
            PetrificationHandler.petrify(entity, PETRIFICATION_DURATION)
        }

        spawnPetrificationEffects(level, blockPos, entities)

        return true
    }

    override fun onTickRitual(
        level: Level,
        pos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ) {
        if (level !is ServerLevel) return

        if (level.gameTime % 10 == 0L) {
            val box = AABB.ofSize(pos.center, RITUAL_RADIUS * 2, RITUAL_RADIUS * 2, RITUAL_RADIUS * 2)
            val entities = level.getEntitiesOfClass(LivingEntity::class.java, box)

            entities.forEach { entity ->
                val data = entity.getData(WitcheryDataAttachments.PETRIFIED_ENTITY)
                if (data.isPetrified()) {
                    PetrificationHandler.spawnPetrifiedParticles(level, entity)
                }
            }
        }
    }

    override fun onEndRitual(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ) {
        if (level is ServerLevel) {
            spawnCompletionEffects(level, blockPos)
        }
    }

    private fun spawnPetrificationEffects(level: ServerLevel, pos: BlockPos, entities: List<LivingEntity>) {
        entities.forEach { entity ->
            PetrificationHandler.spawnPetrificationWave(level, pos.center, entity.position())

            PetrificationHandler.spawnTransformationParticles(level, entity)
        }
    }

    private fun spawnCompletionEffects(level: ServerLevel, pos: BlockPos) {
        val center = pos.center
        for (i in 0..20) {
            val angle = (i / 20.0) * Math.PI * 2
            val x = center.x + Math.cos(angle) * RITUAL_RADIUS
            val z = center.z + Math.sin(angle) * RITUAL_RADIUS

            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.END_ROD,
                x, center.y + 1.0, z,
                5,
                0.1, 0.5, 0.1,
                0.02
            )
        }
    }
}
