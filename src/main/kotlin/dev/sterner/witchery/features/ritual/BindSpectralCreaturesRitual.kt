package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.content.block.effigy.EffigyBlockEntity
import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.content.entity.BansheeEntity
import dev.sterner.witchery.content.entity.PoltergeistEntity
import dev.sterner.witchery.content.entity.SpiritEntity
import dev.sterner.witchery.features.chain.ChainManager
import dev.sterner.witchery.features.chain.ChainType
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.*

class BindSpectralCreaturesRitual : Ritual("bind_spectral_creatures") {

    companion object {
        private val entityToEffigyMap = HashMap<UUID, BlockPos>()

        fun handleChainDiscard(entity: Entity?) {
            val entityId = entity?.uuid ?: return
            val effigyPos = entityToEffigyMap[entityId] ?: return

            val level = entity.level()
            val blockEntity = level.getBlockEntity(effigyPos)

            if (blockEntity is EffigyBlockEntity) {
                when (entity) {
                    is BansheeEntity -> {
                        blockEntity.bansheeCount += 1
                        blockEntity.setChanged()
                        entityToEffigyMap.remove(entityId)
                        entity.discard()
                    }

                    is PoltergeistEntity -> {
                        blockEntity.specterCount += 1
                        blockEntity.setChanged()
                        entityToEffigyMap.remove(entityId)
                        entity.discard()
                    }
                    is SpiritEntity -> {
                        blockEntity.spiritCount += 1
                        entityToEffigyMap.remove(entityId)
                        entity.discard()
                    }

                }

                makeBindingParticles(level, entity.position(), blockEntity.blockPos.center)
            }
        }

        private fun makeBindingParticles(level: Level, entityPos: Vec3, effigyPos: Vec3) {
            if (level is ServerLevel) {
                val particleCount = 20
                val direction = effigyPos.subtract(entityPos).normalize()

                for (i in 0 until particleCount) {
                    val offset = Vec3(
                        level.random.nextDouble() - 0.5,
                        level.random.nextDouble() - 0.5,
                        level.random.nextDouble() - 0.5
                    ).scale(0.5)

                    val position = entityPos.add(direction.scale(i / particleCount.toDouble()))

                    level.sendParticles(
                        ParticleTypes.WITCH,
                        position.x + offset.x,
                        position.y + offset.y,
                        position.z + offset.z,
                        1, 0.0, 0.0, 0.0, 0.0
                    )
                }
            }
        }
    }

    override fun onEndRitual(level: Level, blockPos: BlockPos, goldenChalkBlockEntity: GoldenChalkBlockEntity) {
        if (level is ServerLevel) {
            val area = AABB.ofSize(blockPos.center, 16.0, 16.0, 16.0)

            val unboundEntities = level.getEntitiesOfClass(LivingEntity::class.java, area)
                .filter { it is BansheeEntity || it is PoltergeistEntity || it is SpiritEntity } //TODO add the other spectral creatures

            val possibleEffigies =
                BlockPos.betweenClosedStream(area).filter { level.getBlockEntity(it) is EffigyBlockEntity }.findAny()

            if (unboundEntities.isNotEmpty() && possibleEffigies.isPresent) {
                val effigyPos = possibleEffigies.get()
                val effigyCenter = effigyPos.center

                for (entity in unboundEntities) {
                    entityToEffigyMap[entity.uuid] = effigyPos

                    makeParticles(level, entity)

                    ChainManager.createHookAndPullChain(
                        level, effigyCenter.add(0.0, 0.2, 0.0), entity,
                        pullDelay = 0,
                        extensionSpeed = 0.8f,
                        chainType = ChainType.SPIRIT
                    )
                }
            } else {
                Containers.dropContents(level, blockPos, goldenChalkBlockEntity)
            }
        }
    }

    private fun makeParticles(level: Level, entity: Entity) {
        if (level is ServerLevel) {
            val particleCount = 15

            (0 until particleCount).forEach { i ->
                level.sendParticles(
                    ParticleTypes.WITCH,
                    entity.x + level.random.nextDouble() - 0.5,
                    entity.y + level.random.nextDouble() * entity.boundingBox.size,
                    entity.z + level.random.nextDouble() - 0.5,
                    1, 0.0, 0.0, 0.0, 0.0
                )
            }
        }
    }
}