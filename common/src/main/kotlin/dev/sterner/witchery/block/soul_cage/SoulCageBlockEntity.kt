package dev.sterner.witchery.block.soul_cage

import dev.architectury.event.EventResult
import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.api.event.ChainEvent
import dev.sterner.witchery.handler.chain.ChainManager
import dev.sterner.witchery.handler.chain.ChainType
import dev.sterner.witchery.platform.EtherealEntityAttachment
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
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
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

        if (!hasSoul && level.gameTime % 20 == 0L) {
            lookAndConsumeSoul(level, pos)
        }

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
                            0 -> Pair(pos.x + marginX + level.random.nextDouble() * 0.1, pos.z + 0.5 + (level.random.nextDouble() - 0.5) * 0.4)
                            1 -> Pair(pos.x + 1 - marginX - level.random.nextDouble() * 0.1, pos.z + 0.5 + (level.random.nextDouble() - 0.5) * 0.4)
                            2 -> Pair(pos.x + 0.5 + (level.random.nextDouble() - 0.5) * 0.4, pos.z + marginZ + level.random.nextDouble() * 0.1)
                            else -> Pair(pos.x + 0.5 + (level.random.nextDouble() - 0.5) * 0.4, pos.z + 1 - marginZ - level.random.nextDouble() * 0.1)
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
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putBoolean("HasSoul", hasSoul)
    }

    companion object {
        private val entityToSoulCageMap = HashMap<UUID, BlockPos>()

        fun registerEvents() {
            ChainEvent.ON_DISCARD.register { entity, _ ->
                handleChainDiscard(entity)
                EventResult.pass()
            }
        }

        private fun handleChainDiscard(entity: Entity?) {
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
                        level.setBlockAndUpdate(soulCagePos, level.getBlockState(soulCagePos).setValue(BlockStateProperties.LIT, true))
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

                for (i in 0 until 10) {
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
}