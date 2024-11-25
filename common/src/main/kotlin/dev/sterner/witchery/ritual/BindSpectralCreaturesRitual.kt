package dev.sterner.witchery.ritual

import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.block.effigy.EffigyBlockEntity
import dev.sterner.witchery.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.entity.BansheeEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB

class BindSpectralCreaturesRitual : Ritual("bind_spectral_creatures") {

    override fun onEndRitual(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity) {
        if (level is ServerLevel) {
            val area = AABB.ofSize(blockPos.center, 16.0, 16.0, 16.0)

            val unboundEntities = level.getEntitiesOfClass(LivingEntity::class.java, area).filter { (it is BansheeEntity) }//TODO add the other spectral creatures
            val possibleEffigies = BlockPos.betweenClosedStream(area).filter{ level.getBlockEntity(it) is EffigyBlockEntity }.findAny()

            if (unboundEntities.isNotEmpty() && possibleEffigies.isPresent) {
                val effigy = level.getBlockEntity(possibleEffigies.get()) as EffigyBlockEntity
                for (entity in unboundEntities) {
                    if (entity is BansheeEntity) {
                        effigy.bansheeCount += 1
                        makeParticles(level, entity)
                    }
                    /* TODO add when theres more spectral creatures
                    if (entity is Spectre) {
                        effigy.bansheeCount += 1
                        makeParticles(level, entity)
                    }
                    if (entity is PoltergeistEntity) {
                        effigy.poltergeistCount += 1
                        makeParticles(level, entity)
                    }

                     */
                }



            } else {
                Containers.dropContents(level, blockPos, blockEntity)
            }
        }
    }

    private fun makeParticles(level: ServerLevel, entity: LivingEntity) {
        for (i in 0..10) {
            val offsetX = (level.random.nextDouble() - 0.5) * 0.5
            val offsetY = (level.random.nextDouble() - 0.5) * 0.5 + 0.5
            val offsetZ = (level.random.nextDouble() - 0.5) * 0.5
            level.sendParticles(
                ParticleTypes.END_ROD,
                entity.x + offsetX,
                entity.y + offsetY,
                entity.z + offsetZ,
                1,
                0.0, 0.0, 0.0,
                0.1
            )
        }
    }
}