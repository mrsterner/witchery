package dev.sterner.witchery.handler

import dev.sterner.witchery.api.EntityChainInterface
import dev.sterner.witchery.entity.ChainEntity
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

// ChainManager.kt
object ChainManager {
    /**
     * Creates a chain entity that connects from a specific position to a target entity
     */
    fun createChain(level: Level, position: Vec3, targetEntity: Entity, lifetime: Int = -1): ChainEntity {
        val chain = ChainEntity(level)
        chain.setPos(position.x, position.y, position.z)
        chain.lockPosition(position)
        chain.setTargetEntity(targetEntity)

        if (lifetime > 0) {
            chain.setLife(lifetime)
        }

        level.addFreshEntity(chain)
        return chain
    }

    /**
     * Creates a chain entity that connects from a source entity to a target entity
     */
    fun createChain(level: Level, sourceEntity: Entity, targetEntity: Entity, lifetime: Int = -1): ChainEntity {
        return createChain(level, sourceEntity.position(), targetEntity, lifetime)
    }

    /**
     * Remove all chains from a specific entity
     */
    fun releaseEntity(entity: Entity) {
        if (entity is EntityChainInterface) {
            val chains = (entity as EntityChainInterface).`witchery$getRestrainingChains`()
            chains.forEach { it.discard() }
        }
    }
}