package dev.sterner.witchery.content.block.soul_cage

import dev.sterner.witchery.content.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.content.entity.AbstractSpectralEntity
import dev.sterner.witchery.features.necromancy.EtherealEntityAttachment
import dev.sterner.witchery.features.chain.ChainManager
import dev.sterner.witchery.features.chain.ChainType
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.monster.ZombieVillager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.math.*

class SoulCageBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.SOUL_CAGE.get(), blockPos, blockState) {

    var hasSoul = false
    var chainChargeUp = 0
    var maxChargeUp = 2 //In seconds

    //Clientside:
    // Current rotation values
    private var currentYaw = 0f
    private var currentPitch = 0f

    // Target rotation values
    private var targetYaw = 0f
    private var targetPitch = 0f

    // Previous tick's rotation for interpolation
    private var prevYaw = 0f
    private var prevPitch = 0f

    // Tracking state
    private var isTrackingPlayer = false
    private var previousTrackingState = false

    //Soul barrier
    private var saltCount: Int = 0
    private var fuelTime: Int = 0
    private var maxFuelTime: Int = 0
    private var animationTime: Int = 0
    private var wasProcessing: Boolean = false

    fun getAnimationTime(): Int = animationTime

    fun lookAndConsumeSoul(level: Level, pos: BlockPos, radius: Double = 8.0) {
        if (hasSoul) return

        if (level is ServerLevel) {
            val area = AABB.ofSize(pos.center, radius * 2, radius * 2, radius * 2)

            val villagers = level.getEntitiesOfClass(ZombieVillager::class.java, area)
                .filter {
                    EtherealEntityAttachment.getData(it).isEthereal
                }

            if (villagers.isNotEmpty()) {

                chainChargeUp++

                if (chainChargeUp > maxChargeUp) {
                    chainChargeUp = 0

                    val closestVillager = villagers.minByOrNull { it.distanceToSqr(pos.center) }
                    closestVillager?.let { villager ->

                        entityToSoulCageMap[villager.uuid] = pos

                        ChainManager.createHookAndPullChain(
                            level,
                            pos.center.add(0.0, 0.25, 0.0),
                            villager,
                            pullDelay = 20,
                            extensionSpeed = 0.4f,
                            chainType = ChainType.SOUL
                        )

                        level.playSound(
                            null,
                            pos,
                            SoundEvents.CHAIN_PLACE,
                            SoundSource.BLOCKS,
                            1.0f,
                            0.5f
                        )
                        return
                    }
                }
                setChanged()
            }
        }
    }

    fun extractSoul(): Boolean {
        if (hasSoul) {
            hasSoul = false
            setChanged()
            level?.setBlockAndUpdate(blockPos, blockState.setValue(BlockStateProperties.LIT, false))
            return true
        }
        return false
    }

    override fun tick(level: Level, pos: BlockPos, blockState: BlockState) {
        super.tick(level, pos, blockState)

        tickSoulBox(level, pos)

        if (!hasSoul && !isProcessing()) {
            level.setBlockAndUpdate(blockPos, blockState.setValue(BlockStateProperties.LIT, false))
        }

        if (!hasSoul && level.gameTime % 20 == 0L) {
            lookAndConsumeSoul(level, pos)
        }

        makeAmbientParticles(level, pos)

        if (level is ServerLevel) {
            return
        }
        prevYaw = currentYaw
        prevPitch = currentPitch

        val closestPlayer = findClosestPlayerInRange(level, pos)
        previousTrackingState = isTrackingPlayer
        isTrackingPlayer = closestPlayer != null

        if (isTrackingPlayer && closestPlayer != null) {
            val dx = closestPlayer.x - (pos.x + 0.5)
            val dy = closestPlayer.y + closestPlayer.eyeHeight - (pos.y + 0.5)
            val dz = closestPlayer.z - (pos.z + 0.5)

            val newTargetYaw = (Math.toDegrees(atan2(-dx, dz)).toFloat() + 180) % 360

            val horizontalDistance = sqrt(dx * dx + dz * dz)
            val newTargetPitch = -Math.toDegrees(atan2(dy, horizontalDistance)).toFloat()

            targetYaw = normalizeYawDifference(currentYaw, newTargetYaw)
            targetPitch = newTargetPitch.coerceIn(-45f, 45f)
        } else {
            val gameTime = level.gameTime
            targetYaw = (gameTime * 0.5f) % 360
            targetPitch = (sin(gameTime * 0.05) * 10).toFloat()
        }

        currentYaw = lerpAngle(currentYaw, targetYaw)
        currentPitch = lerpAngle(currentPitch, targetPitch)
    }

    private fun makeAmbientParticles(level: Level, pos: BlockPos) {
        if (hasSoul && level.gameTime % 20 == 0L) {
            if (level is ServerLevel) {
                val blockWidthX = 12.0 / 16.0
                val blockWidthZ = 12.0 / 16.0

                val marginX = (1.0 - blockWidthX) / 2.0 + 0.75
                val marginZ = (1.0 - blockWidthZ) / 2.0 + 0.75

                val particleType = ParticleTypes.SOUL

                when (level.random.nextInt(4)) {
                    0 -> {
                        level.sendParticles(
                            particleType,
                            pos.x + 0.5 + (level.random.nextDouble() - 0.5) * 0.4,
                            pos.y + 1.0 + level.random.nextDouble() * 0.2,
                            pos.z + 0.5 + (level.random.nextDouble() - 0.5) * 0.4,
                            1, 0.0, 0.02, 0.0, 0.01
                        )
                    }

                    1 -> {
                        val cornerX = if (level.random.nextBoolean()) pos.x + marginX else pos.x + 1 - marginX
                        val cornerZ = if (level.random.nextBoolean()) pos.z + marginZ else pos.z + 1 - marginZ

                        level.sendParticles(
                            particleType,
                            cornerX,
                            pos.y + 0.8 + level.random.nextDouble() * 0.3,
                            cornerZ,
                            1, 0.0, 0.03, 0.0, 0.01
                        )
                    }

                    2, 3 -> {
                        val side = level.random.nextInt(4)
                        val (particleX, particleZ) = when (side) {
                            0 -> Pair(
                                pos.x + marginX + level.random.nextDouble() * 0.1,
                                pos.z + 0.5 + (level.random.nextDouble() - 0.5) * 0.4
                            )

                            1 -> Pair(
                                pos.x + 1 - marginX - level.random.nextDouble() * 0.1,
                                pos.z + 0.5 + (level.random.nextDouble() - 0.5) * 0.4
                            )

                            2 -> Pair(
                                pos.x + 0.5 + (level.random.nextDouble() - 0.5) * 0.4,
                                pos.z + marginZ + level.random.nextDouble() * 0.1
                            )

                            else -> Pair(
                                pos.x + 0.5 + (level.random.nextDouble() - 0.5) * 0.4,
                                pos.z + 1 - marginZ - level.random.nextDouble() * 0.1
                            )
                        }

                        level.sendParticles(
                            particleType,
                            particleX,
                            pos.y + 0.4 + level.random.nextDouble() * 0.5,
                            particleZ,
                            1, 0.0, 0.02, 0.0, 0.01
                        )
                    }
                }
            }
        }
    }

    /**
     * Find the closest player within specified range
     */
    private fun findClosestPlayerInRange(level: Level, pos: BlockPos, range: Double = 4.0): Player? {
        return level.players().stream()
            .filter { player ->
                player.distanceToSqr(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) <= range * range
            }
            .min(Comparator.comparingDouble { player ->
                player.distanceToSqr(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
            })
            .orElse(null)
    }

    /**
     * Get interpolated rotation for rendering
     */
    fun getInterpolatedRotation(partialTick: Float): Pair<Float, Float> {
        val interpYaw = Mth.lerp(partialTick, prevYaw, currentYaw)
        val interpPitch = Mth.lerp(partialTick, prevPitch, currentPitch)
        return Pair(interpYaw, interpPitch)
    }

    /**
     * Linear interpolation for angles, handling wraparound
     */
    private fun lerpAngle(current: Float, target: Float, factor: Float = 0.15f): Float {
        return current + factor * Mth.wrapDegrees(target - current)
    }

    /**
     * Normalize the target yaw to prevent spinning more than 180 degrees
     */
    private fun normalizeYawDifference(current: Float, target: Float): Float {
        val diff = Mth.wrapDegrees(target - current)
        return current + diff
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        if (pTag.contains("HasSoul")) {
            hasSoul = pTag.getBoolean("HasSoul")
        }
        loadSoulBarrier(tag = pTag)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putBoolean("HasSoul", hasSoul)
        saveSoulBarrier(tag)
    }


    override fun onUseWithItem(
        pPlayer: Player,
        pStack: ItemStack,
        pHand: InteractionHand
    ): ItemInteractionResult {
        if (pStack.`is`(WitcheryItems.SPECTRAL_DUST.get())) {
            if (saltCount < 1) {
                pStack.shrink(1)
                animationTime = 0
                saltCount = 1
                if (!isProcessing()) {
                    startProcessing()
                }
                setChanged()
                return ItemInteractionResult.SUCCESS
            }
            return ItemInteractionResult.FAIL
        }
        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    companion object {
        const val TOTAL_DURATION: Float = 20 * 1f
        const val SALT_TIME = 20 * 60
        private const val CONTAINMENT_RADIUS = 5.0
        private const val PARTICLE_OFFSET = 0.1

        private val entityToSoulCageMap = HashMap<UUID, BlockPos>()


        fun handleChainDiscard(entity: Entity?) {
            val entityId = entity?.uuid ?: return
            val soulCagePos = entityToSoulCageMap[entityId] ?: return

            val level = entity.level()
            val blockEntity = level.getBlockEntity(soulCagePos)

            if (blockEntity is SoulCageBlockEntity) {
                when (entity) {
                    is ZombieVillager -> {
                        blockEntity.hasSoul = true
                        blockEntity.setChanged()

                        level.playSound(
                            null,
                            soulCagePos,
                            SoundEvents.SOUL_ESCAPE.value(),
                            SoundSource.BLOCKS,
                            1.0f,
                            0.8f + level.random.nextFloat() * 0.4f
                        )

                        entityToSoulCageMap.remove(entityId)
                        entity.discard()

                        makeBindingParticles(level, entity.position(), blockEntity.blockPos.center)
                        level.setBlockAndUpdate(
                            soulCagePos,
                            level.getBlockState(soulCagePos).setValue(BlockStateProperties.LIT, true)
                        )
                    }
                }
            }
        }

        private fun makeBindingParticles(level: Level, entityPos: Vec3, soulCagePos: Vec3) {
            if (level is ServerLevel) {
                val particleCount = 30
                val direction = soulCagePos.subtract(entityPos).normalize()

                for (i in 0 until particleCount) {
                    val offset = Vec3(
                        level.random.nextDouble() - 0.5,
                        level.random.nextDouble() - 0.5,
                        level.random.nextDouble() - 0.5
                    ).scale(0.5)

                    val position = entityPos.add(direction.scale(i / particleCount.toDouble()))

                    level.sendParticles(
                        ParticleTypes.SOUL,
                        position.x + offset.x,
                        position.y + offset.y,
                        position.z + offset.z,
                        1, 0.0, 0.0, 0.0, 0.0
                    )
                }

                (0 until 10).forEach { i ->
                    level.sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        soulCagePos.x + (level.random.nextDouble() - 0.5) * 0.75,
                        soulCagePos.y + 0.2 + level.random.nextDouble() * 0.5,
                        soulCagePos.z + (level.random.nextDouble() - 0.5) * 0.75,
                        1, 0.0, 0.05, 0.0, 0.02
                    )
                }
            }
        }
    }

    //Soul barrier stuff

    private fun startProcessing() {
        if (saltCount > 0) {
            fuelTime = SALT_TIME
            maxFuelTime = SALT_TIME
            saltCount--
            level?.setBlockAndUpdate(blockPos, blockState.setValue(BlockStateProperties.LIT, true))
            setChanged()
        }
    }

    fun isProcessing(): Boolean = fuelTime > 0

    fun tickSoulBox(level: Level, pos: BlockPos) {

        if (!level.isClientSide) {
            val isProcessingNow = isProcessing()

            if (isProcessingNow && !wasProcessing) {
                animationTime = 0
            }

            if (isProcessingNow && animationTime < TOTAL_DURATION) {
                animationTime++
            }

            wasProcessing = isProcessingNow


            if (fuelTime > 0) {
                fuelTime--

                if (fuelTime == 0 && saltCount > 0) {
                    startProcessing()
                }

                val box = AABB(
                    pos.x - CONTAINMENT_RADIUS,
                    pos.y - CONTAINMENT_RADIUS,
                    pos.z - CONTAINMENT_RADIUS,
                    pos.x + CONTAINMENT_RADIUS + 1.0,
                    pos.y + CONTAINMENT_RADIUS + 1.0,
                    pos.z + CONTAINMENT_RADIUS + 1.0
                )

                containSpectralEntities(level, box, pos)

                setChanged()
            }
        } else {
            if (isProcessing() && level.random.nextFloat() < 0.3f) {
                level.addParticle(
                    ParticleTypes.END_ROD,
                    pos.x + 0.5 + (level.random.nextDouble() - 0.5),
                    pos.y + 0.8,
                    pos.z + 0.5 + (level.random.nextDouble() - 0.5),
                    0.0, 0.02, 0.0
                )
            }
        }
    }

    private fun containSpectralEntity(entity: AbstractSpectralEntity, pos: BlockPos, level: Level) {
        val centerX = pos.x + 0.5
        val centerY = pos.y + 0.5
        val centerZ = pos.z + 0.5

        val entityBounds = entity.boundingBox
        val entityCenterX = (entityBounds.minX + entityBounds.maxX) / 2.0
        val entityCenterY = (entityBounds.minY + entityBounds.maxY) / 2.0
        val entityCenterZ = (entityBounds.minZ + entityBounds.maxZ) / 2.0

        val minX = centerX - CONTAINMENT_RADIUS
        val maxX = centerX + CONTAINMENT_RADIUS
        val minY = centerY - CONTAINMENT_RADIUS
        val maxY = centerY + CONTAINMENT_RADIUS
        val minZ = centerZ - CONTAINMENT_RADIUS
        val maxZ = centerZ + CONTAINMENT_RADIUS

        var hitBoundary = false
        var particleX = entityCenterX
        var particleY = entityCenterY
        var particleZ = entityCenterZ

        if (entityBounds.maxX > maxX) {
            hitBoundary = true
            particleX = maxX - PARTICLE_OFFSET
            particleY = entityCenterY
            particleZ = entityCenterZ
        } else if (entityBounds.minX < minX) {
            hitBoundary = true
            particleX = minX + PARTICLE_OFFSET
            particleY = entityCenterY
            particleZ = entityCenterZ
        }

        if (entityBounds.maxY > maxY) {
            hitBoundary = true
            particleY = maxY - PARTICLE_OFFSET
            if (entityBounds.minX >= minX && entityBounds.maxX <= maxX) {
                particleX = entityCenterX
            }
            if (entityBounds.minZ >= minZ && entityBounds.maxZ <= maxZ) {
                particleZ = entityCenterZ
            }
        } else if (entityBounds.minY < minY) {
            hitBoundary = true
            particleY = minY + PARTICLE_OFFSET
            if (entityBounds.minX >= minX && entityBounds.maxX <= maxX) {
                particleX = entityCenterX
            }
            if (entityBounds.minZ >= minZ && entityBounds.maxZ <= maxZ) {
                particleZ = entityCenterZ
            }
        }

        if (entityBounds.maxZ > maxZ) {
            hitBoundary = true
            particleZ = maxZ - PARTICLE_OFFSET
            if (entityBounds.minX >= minX && entityBounds.maxX <= maxX) {
                particleX = entityCenterX
            }
            if (entityBounds.minY >= minY && entityBounds.maxY <= maxY) {
                particleY = entityCenterY
            }
        } else if (entityBounds.minZ < minZ) {
            hitBoundary = true
            particleZ = minZ + PARTICLE_OFFSET
            if (entityBounds.minX >= minX && entityBounds.maxX <= maxX) {
                particleX = entityCenterX
            }
            if (entityBounds.minY >= minY && entityBounds.maxY <= maxY) {
                particleY = entityCenterY
            }
        }

        if (hitBoundary) {

            var moveX = 0.0
            var moveY = 0.0
            var moveZ = 0.0

            if (entityBounds.maxX > maxX) moveX = maxX - entityBounds.maxX
            else if (entityBounds.minX < minX) moveX = minX - entityBounds.minX

            if (entityBounds.maxY > maxY) moveY = maxY - entityBounds.maxY
            else if (entityBounds.minY < minY) moveY = minY - entityBounds.minY

            if (entityBounds.maxZ > maxZ) moveZ = maxZ - entityBounds.maxZ
            else if (entityBounds.minZ < minZ) moveZ = minZ - entityBounds.minZ

            entity.setPos(entity.x + moveX, entity.y + moveY, entity.z + moveZ)

            val dx = (centerX - entityCenterX) * 0.05
            val dy = (centerY - entityCenterY) * 0.05
            val dz = (centerZ - entityCenterZ) * 0.05

            entity.deltaMovement = entity.deltaMovement.add(dx, dy, dz)
            entity.hurtMarked = true

            if (level is ServerLevel) {
                createBarrierParticlesForBoundingBox(
                    level,
                    particleX, particleY, particleZ,
                    centerX, centerY, centerZ,
                    entityBounds
                )
            }
        }
    }

    private fun createBarrierParticlesForBoundingBox(
        level: ServerLevel,
        particleX: Double,
        particleY: Double,
        particleZ: Double,
        centerX: Double,
        centerY: Double,
        centerZ: Double,
        entityBounds: AABB
    ) {
        val dirX = particleX - centerX
        val dirY = particleY - centerY
        val dirZ = particleZ - centerZ

        val length = sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ)
        val normalizedX = if (length > 0) dirX / length else 0.0
        val normalizedY = if (length > 0) dirY / length else 0.0
        val normalizedZ = if (length > 0) dirZ / length else 0.0

        val entityHeight = entityBounds.maxY - entityBounds.minY
        val particleCount = 3.coerceAtLeast((entityHeight * 2).toInt())

        for (i in 0 until particleCount) {
            val heightOffset = if (particleCount > 1) {
                (i.toDouble() / (particleCount - 1)) * entityHeight - entityHeight / 2
            } else {
                0.0
            }

            val adjustedY = particleY + heightOffset

            val offsetX = (level.random.nextDouble() - 0.5) * 0.2
            val offsetZ = (level.random.nextDouble() - 0.5) * 0.2

            level.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                particleX + offsetX,
                adjustedY,
                particleZ + offsetZ,
                1,
                normalizedX * 0.05,
                normalizedY * 0.02,
                normalizedZ * 0.05,
                0.01
            )

            if (i % 2 == 0) {
                level.sendParticles(
                    ParticleTypes.END_ROD,
                    particleX + offsetX,
                    adjustedY,
                    particleZ + offsetZ,
                    1,
                    normalizedX * 0.02,
                    normalizedY * 0.01,
                    normalizedZ * 0.02,
                    0.0
                )
            }
        }

        createRippleEffect(
            level,
            particleX,
            particleY,
            particleZ,
            normalizedX,
            normalizedY,
            normalizedZ
        )
    }

    private fun createRippleEffect(
        level: ServerLevel,
        particleX: Double,
        particleY: Double,
        particleZ: Double,
        normalizedX: Double,
        normalizedY: Double,
        normalizedZ: Double
    ) {
        val rippleRadius = 0.5

        for (angle in 0..360 step 60) {
            val radians = Math.toRadians(angle.toDouble())

            val perpX: Double
            val perpY: Double
            val perpZ: Double

            if (abs(normalizedY) > 0.7) {
                perpX = cos(radians) * rippleRadius
                perpY = 0.0
                perpZ = sin(radians) * rippleRadius
            } else if (abs(normalizedX) > abs(normalizedZ)) {
                perpX = 0.0
                perpY = cos(radians) * rippleRadius
                perpZ = sin(radians) * rippleRadius
            } else {
                perpX = cos(radians) * rippleRadius
                perpY = sin(radians) * rippleRadius
                perpZ = 0.0
            }

            level.sendParticles(
                ParticleTypes.ELECTRIC_SPARK,
                particleX + perpX,
                particleY + perpY,
                particleZ + perpZ,
                1,
                0.0, 0.0, 0.0,
                0.0
            )
        }
    }

    /**
     * Contain spectral entities within the lantern's range
     */
    private fun containSpectralEntities(level: Level, box: AABB, pos: BlockPos) {
        val allEntities = level.getEntitiesOfClass(Entity::class.java, box)

        for (entity in allEntities) {
            if (entity is AbstractSpectralEntity) {
                containSpectralEntity(entity, pos, level)
            }
        }
    }

    fun loadSoulBarrier(tag: CompoundTag) {
        saltCount = tag.getInt("SaltCount")
        fuelTime = tag.getInt("FuelTime")
        maxFuelTime = tag.getInt("MaxFuelTime")
        animationTime = tag.getInt("AnimationTime")
        wasProcessing = tag.getBoolean("WasProcessing")
    }

    fun saveSoulBarrier(tag: CompoundTag) {
        tag.putInt("SaltCount", saltCount)
        tag.putInt("FuelTime", fuelTime)
        tag.putInt("MaxFuelTime", maxFuelTime)
        tag.putInt("AnimationTime", animationTime)
        tag.putBoolean("WasProcessing", wasProcessing)
    }
}