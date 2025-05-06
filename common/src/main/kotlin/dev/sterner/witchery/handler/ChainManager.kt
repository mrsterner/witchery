package dev.sterner.witchery.handler

import dev.sterner.witchery.api.EntityChainInterface
import dev.sterner.witchery.api.ServerTickTask
import dev.sterner.witchery.api.TickTaskScheduler
import dev.sterner.witchery.entity.ChainEntity
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
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
     * with animation support.
     *
     * @param level The world level
     * @param position Starting position for the chain
     * @param targetEntity The entity to target with the chain
     * @param extensionSpeed How quickly the chain extends (default: 0.05f)
     * @param retractionSpeed How quickly the chain retracts (default: 0.03f)
     * @param pullStrength How strongly the chain pulls the target (default: 0.15f)
     * @param lifetime How long the chain lasts in ticks (-1 for permanent)
     * @return The created chain entity
     */
    fun createChain(
        level: Level,
        position: Vec3,
        targetEntity: Entity,
        extensionSpeed: Float = 0.05f,
        retractionSpeed: Float = 0.03f,
        pullStrength: Float = 0.15f,
        lifetime: Int = -1
    ): ChainEntity {
        val chain = ChainEntity(level)
        chain.setPos(position.x, position.y, position.z)
        chain.setTargetEntity(targetEntity)

        chain.setExtensionSpeed(extensionSpeed)
        chain.setRetractionSpeed(retractionSpeed)
        chain.setPullStrength(pullStrength)

        if (lifetime > 0) {
            chain.setLife(lifetime)
        }

        level.addFreshEntity(chain)
        return chain
    }

    /**
     * Creates a chain entity with randomized animation properties
     * for more varied visual effects
     */
    fun createRandomizedChain(
        level: Level,
        position: Vec3,
        targetEntity: Entity,
        lifetime: Int = -1,
        noPull: Boolean = false,
    ): ChainEntity {
        val random = level.random

        val extensionSpeed = 0.04f + (random.nextFloat() * 0.03f)
        val retractionSpeed = 0.02f + (random.nextFloat() * 0.02f)
        val pullStrength = if(noPull) 0f else 0.12f + (random.nextFloat() * 0.08f)

        return createChain(
            level,
            position,
            targetEntity,
            extensionSpeed,
            retractionSpeed,
            pullStrength,
            lifetime
        )
    }

    /**
     * Creates multiple chains in a circle around the target entity with animation
     *
     * @param level The world level
     * @param targetEntity The entity to target with chains
     * @param numChains The number of chains to create
     * @param radius The radius around the target to place chains
     * @param sequentialDelay Delay between chains spawning (0 for all at once)
     * @param lifetime The lifetime of the chains in ticks (-1 for permanent)
     * @return List of created chain entities
     */
    fun createMultipleChains(
        level: Level,
        targetEntity: Entity,
        numChains: Int = 5,
        radius: Double = 8.0,
        sequentialDelay: Int = 0,
        lifetime: Int = -1,
        noPull: Boolean = false,
    ): List<ChainEntity> {
        val chainPositions = findChainPositions(level, targetEntity, numChains, radius)
        val chains = mutableListOf<ChainEntity>()

        if (sequentialDelay <= 0) {
            return chainPositions.map { pos ->
                createRandomizedChain(level, pos, targetEntity, lifetime)
            }
        } else {
            var delay = 0

            chainPositions.forEach { pos ->
                if (level is ServerLevel) {
                    TickTaskScheduler.addTask(ServerTickTask(delay) {
                        val chain = createRandomizedChain(level, pos, targetEntity, lifetime, noPull)
                        chains.add(chain)
                    })
                }
                delay += sequentialDelay
            }

            return chains
        }
    }

    /**
     * Creates multiple chains that shoot down from above the target
     *
     * @param level The world level
     * @param targetEntity The entity to target with chains
     * @param numChains The number of chains to create
     * @param radius The radius around the target to place chains
     * @param height The height above the target to start chains
     * @param sequentialDelay Delay between chains spawning (0 for all at once)
     * @param lifetime The lifetime of the chains in ticks (-1 for permanent)
     * @return List of created chain entities
     */
    fun createAerialChains(
        level: Level,
        targetEntity: Entity,
        numChains: Int = 3,
        radius: Double = 3.0,
        height: Double = 10.0,
        sequentialDelay: Int = 5,
        lifetime: Int = -1
    ): List<ChainEntity> {
        val targetPos = targetEntity.position()
        val chains = mutableListOf<ChainEntity>()

        for (i in 0 until numChains) {
            val angle = level.random.nextDouble() * Math.PI * 2
            val distance = level.random.nextDouble() * radius

            val x = targetPos.x + sin(angle) * distance
            val y = targetPos.y + height
            val z = targetPos.z + cos(angle) * distance

            val chainPos = Vec3(x, y, z)

            if (level is ServerLevel) {
                TickTaskScheduler.addTask(ServerTickTask(i * sequentialDelay) {
                    val chain = createRandomizedChain(level, chainPos, targetEntity, lifetime)
                    chain.setExtensionSpeed(0.08f + (level.random.nextFloat() * 0.04f))
                    chains.add(chain)
                })
            }
        }

        return chains
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
     * Add a "hook and pull" effect to an entity
     * Creates a chain, and once it connects, it pulls the target toward the source point
     *
     * @param level The world level
     * @param sourcePos Starting position of the chain
     * @param targetEntity Entity to hook and pull
     * @param pullStrength Strength of the pull effect (higher = faster pull)
     * @param extensionSpeed How fast the chain extends
     * @param pullDelay Ticks to wait before starting to pull
     * @return The created chain entity
     */
    fun createHookAndPullChain(
        level: Level,
        sourcePos: Vec3,
        targetEntity: Entity,
        pullStrength: Float = 0.2f,
        extensionSpeed: Float = 0.1f,
        pullDelay: Int = 10
    ): ChainEntity {
        val chain = createChain(
            level,
            sourcePos,
            targetEntity,
            extensionSpeed,
            0.02f,
            pullStrength
        )

        if (level is ServerLevel) {
            TickTaskScheduler.addTask(ServerTickTask(pullDelay) {
                chain.startRetracting()
            })
        }

        return chain
    }

    /**
     * Create a dynamic cage of chains around an entity
     *
     * @param level The world level
     * @param targetEntity Entity to cage
     * @param numChainsHorizontal Number of chains in horizontal circle
     * @param numChainsVertical Number of chains in vertical circle
     * @param radius Radius of the cage
     * @param lifetime How long the chains last (-1 for permanent)
     * @return List of created chain entities
     */
    fun createChainCage(
        level: Level,
        targetEntity: Entity,
        numChainsHorizontal: Int = 8,
        numChainsVertical: Int = 6,
        radius: Double = 3.0,
        lifetime: Int = -1
    ): List<ChainEntity> {
        val chains = mutableListOf<ChainEntity>()
        val targetPos = targetEntity.position()

        for (i in 0 until numChainsHorizontal) {
            val angle = (i.toDouble() / numChainsHorizontal) * Math.PI * 2
            val x = targetPos.x + sin(angle) * radius
            val y = targetPos.y + 0.5
            val z = targetPos.z + cos(angle) * radius

            val pos = Vec3(x, y, z)
            chains.add(createChain(level, pos, targetEntity, 0.15f, 0.01f, 0.0f, lifetime))
        }

        for (i in 0 until numChainsVertical) {
            val angle = (i.toDouble() / numChainsVertical) * Math.PI
            val x = targetPos.x + sin(angle) * radius
            val y = targetPos.y + cos(angle) * radius
            val z = targetPos.z

            val pos = Vec3(x, y, z)
            chains.add(createChain(level, pos, targetEntity, 0.15f, 0.01f, 0.0f, lifetime))

            val x2 = targetPos.x
            val y2 = targetPos.y + cos(angle) * radius
            val z2 = targetPos.z + sin(angle) * radius

            val pos2 = Vec3(x2, y2, z2)
            chains.add(createChain(level, pos2, targetEntity, 0.15f, 0.01f, 0.0f, lifetime))
        }

        return chains
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

    /**
     * Try to release a specific chain from an entity
     */
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

    /**
     * Makes all chains attached to an entity start retracting
     *
     * @param target The entity whose chains should retract
     */
    fun retractAllChains(target: Entity) {
        if (target is EntityChainInterface) {
            val chains = (target as EntityChainInterface).`witchery$getRestrainingChains`()
            chains.forEach {
                it.startRetracting()
            }
        }
    }
}