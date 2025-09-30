package dev.sterner.witchery.handler.affliction.vampire

import dev.sterner.witchery.data_attachment.affliction.VampireChildrenHuntLevelAttachment
import dev.sterner.witchery.entity.VampireEntity
import dev.sterner.witchery.handler.BloodPoolHandler
import dev.sterner.witchery.payload.SpawnSmokeParticlesS2CPayload
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.network.PacketDistributor
import java.util.*
import java.util.stream.Stream

object VampireChildrenHuntHandler {


    private fun findSpawnPosition(serverLevel: ServerLevel, coffinPos: BlockPos): BlockPos? {
        val directions: Stream<BlockPos> = BlockPos.betweenClosedStream(AABB.ofSize(coffinPos.center, 10.0, 10.0, 10.0))

        return directions
            .filter { pos -> serverLevel.getBlockState(pos).isAir && serverLevel.getBlockState(pos.above()).isAir }
            .findFirst()
            .orElse(null)
    }

    @JvmStatic
    fun returnFromHunt(
        serverLevel: ServerLevel,
        huntData: VampireChildrenHuntLevelAttachment.HuntData
    ): VampireEntity? {
        val coffinPos = huntData.coffinPos
        val spawnPos = findSpawnPosition(serverLevel, coffinPos) ?: return null

        val entity = EntityType.loadEntityRecursive(huntData.entityNbt, serverLevel) { it as? VampireEntity }
            ?: return null

        if (entity is VampireEntity) {
            entity.moveTo(
                spawnPos.x + 0.5,
                spawnPos.y.toDouble(),
                spawnPos.z + 0.5,
                serverLevel.random.nextFloat() * 360F,
                0F
            )
            serverLevel.addFreshEntity(entity)

            val bloodCollected = calculateBloodCollected(serverLevel)

            entity.returnFromHunt(bloodCollected)

            return entity
        }

        return null
    }

    private fun calculateBloodCollected(serverLevel: ServerLevel): Int {
        val baseAmount = 25
        val randomBonus = serverLevel.random.nextInt(75)
        return baseAmount + randomBonus
    }


    @JvmStatic
    fun tryStartHunt(serverLevel: ServerLevel, vampireEntity: VampireEntity, playerUUID: UUID) {
        if (serverLevel.isDay) {
            return
        }

        val data = VampireChildrenHuntLevelAttachment.getData(serverLevel)
        val mutableData = data.data.toMutableMap()
        val hunts = mutableData.getOrPut(playerUUID) { mutableListOf() }

        if (vampireEntity.huntedLastNight) {
            return
        }

        val savedNbt = CompoundTag()
        vampireEntity.saveAsPassenger(savedNbt)
        val huntData = VampireChildrenHuntLevelAttachment.HuntData(
            entityNbt = savedNbt,
            coffinPos = vampireEntity.coffinPos
                ?: vampireEntity.creationPos
                ?: serverLevel.getPlayerByUUID(playerUUID)?.blockPosition()
                ?: return,
            creationPos = vampireEntity.creationPos ?: return
        )
        hunts.add(huntData)

        VampireChildrenHuntLevelAttachment.setData(serverLevel, VampireChildrenHuntLevelAttachment.Data(mutableData))

        PacketDistributor.sendToPlayersTrackingChunk(
            serverLevel, vampireEntity.chunkPosition(),
            SpawnSmokeParticlesS2CPayload(vampireEntity.position())
        )
        vampireEntity.remove(Entity.RemovalReason.DISCARDED)
    }

    fun tickHuntAllLevels(minecraftServer: MinecraftServer?) {
        if (minecraftServer == null) {
            return
        }

        for (serverLevel in minecraftServer.allLevels) {
            val data = VampireChildrenHuntLevelAttachment.getData(serverLevel)
            if (data.data.isNotEmpty()) {
                tickHunt(serverLevel, data)
            }
        }
    }

    private fun tickHunt(serverLevel: ServerLevel, data: VampireChildrenHuntLevelAttachment.Data) {
        if (serverLevel.isDay) {
            val currentTime = serverLevel.dayTime
            val iterator = data.data.entries.iterator()

            while (iterator.hasNext()) {
                val (_, hunts) = iterator.next()
                val huntsIterator = hunts.iterator()

                while (huntsIterator.hasNext()) {
                    val huntData = huntsIterator.next()

                    val vampireEntity = returnFromHunt(serverLevel, huntData)
                    if (vampireEntity != null) {
                        vampireEntity.huntedLastNight = true
                        vampireEntity.lastHuntTimestamp = currentTime
                        BloodPoolHandler.increaseBlood(vampireEntity, 300)

                        huntsIterator.remove()
                    }
                }

                if (hunts.isEmpty()) {
                    iterator.remove()
                }
            }

            VampireChildrenHuntLevelAttachment.setData(serverLevel, data)
        }
    }
}