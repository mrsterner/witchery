package dev.sterner.witchery.handler

import dev.sterner.witchery.api.EntityChainInterface
import dev.sterner.witchery.entity.ChainEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import kotlin.math.cos
import kotlin.math.sin

object ChainManager {

    /**
     * Creates a chain entity that connects from a specific position to a target entity
     */
    fun createChain(level: Level, position: Vec3, targetEntity: Entity, lifetime: Int = -1): ChainEntity {
        val chain = ChainEntity(level)
        chain.setPos(position.x, position.y, position.z)
        chain.setTargetEntity(targetEntity)

        if (lifetime > 0) {
            chain.setLife(lifetime)
        }

        level.addFreshEntity(chain)
        return chain
    }

    /**
     * Creates multiple chains in a circle around the target entity
     *
     * @param level The world level
     * @param targetEntity The entity to target with chains
     * @param numChains The number of chains to create
     * @param radius The radius around the target to place chains
     * @param lifetime The lifetime of the chains in ticks (-1 for permanent)
     * @return List of created chain entities
     */
    fun createMultipleChains(
        level: Level,
        targetEntity: Entity,
        numChains: Int = 5,
        radius: Double = 8.0,
        lifetime: Int = -1
    ): List<ChainEntity> {
        val chainPositions = findChainPositions(level, targetEntity, numChains, radius)

        return chainPositions.map { pos ->
            createChain(level, pos, targetEntity, lifetime)
        }
    }

    /**
     * Finds valid positions for chains in a circle around the target entity
     * with line of sight checks
     *
     * @param level The world level
     * @param targetEntity The entity to create chains around
     * @param numChains The number of chain positions to find
     * @param radius The maximum radius around the target
     * @return List of valid positions for chains
     */
    private fun findChainPositions(
        level: Level,
        targetEntity: Entity,
        numChains: Int = 5,
        radius: Double = 8.0
    ): List<Vec3> {
        val targetPos = targetEntity.position()
        val result = mutableListOf<Vec3>()
        var attempts = 0
        val maxAttempts = numChains * 6

        while (result.size < numChains && attempts < maxAttempts) {
            attempts++

            val angle = level.random.nextDouble() * Math.PI * 2
            val distance = (radius * 0.5) + (level.random.nextDouble() * radius * 0.5)

            val x = targetPos.x + sin(angle) * distance
            val y = targetPos.y + (level.random.nextDouble() * 8 - 4)
            val z = targetPos.z + cos(angle) * distance

            val pos = Vec3(x, y, z)

            val blockPos = BlockPos(pos.x.toInt(), pos.y.toInt(), pos.z.toInt())
            if (!level.getBlockState(blockPos).isAir) {
                continue
            }

            if (hasLineOfSight(level, pos, targetEntity)) {
                result.add(pos)
            }
        }

        return result
    }

    /**
     * Checks if there is a clear line of sight from position to target entity
     */
    private fun hasLineOfSight(level: Level, pos: Vec3, targetEntity: Entity): Boolean {
        val targetPos = targetEntity.position().add(0.0, targetEntity.bbHeight / 2.0, 0.0)

        val context = ClipContext(
            pos,
            targetPos,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            null as CollisionContext?
        )

        val result = level.clip(context)

        val hitPos = result.location
        return hitPos.distanceTo(targetPos) < 0.5
    }

    /**
     * Remove all chains from a specific entity
     */
    fun releaseEntity(target: Entity) {
        if (target is EntityChainInterface) {
            val chains = (target as EntityChainInterface).`witchery$getRestrainingChains`()
            chains.forEach { it.discard() }
        }
    }

    fun tryReleaseEntity(chainEntity: ChainEntity, target: Entity) {
        if (target is EntityChainInterface) {
            val chains = (target as EntityChainInterface).`witchery$getRestrainingChains`()
            chains.forEach {
                if (it == chainEntity) {
                    it.discard()
                }
            }
        }
    }
}