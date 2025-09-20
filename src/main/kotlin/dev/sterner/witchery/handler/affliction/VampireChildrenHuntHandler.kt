package dev.sterner.witchery.handler.affliction

import dev.architectury.event.events.common.TickEvent
import dev.sterner.witchery.entity.VampireEntity
import dev.sterner.witchery.handler.BloodPoolHandler
import dev.sterner.witchery.payload.SpawnSmokeParticlesS2CPayload
import dev.sterner.witchery.platform.transformation.VampireChildrenHuntLevelAttachment.Data
import dev.sterner.witchery.platform.transformation.VampireChildrenHuntLevelAttachment.HuntData
import dev.sterner.witchery.platform.transformation.VampireChildrenHuntLevelAttachment.getData
import dev.sterner.witchery.platform.transformation.VampireChildrenHuntLevelAttachment.setData
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.phys.AABB
import java.util.*
import java.util.stream.Stream

object VampireChildrenHuntHandler {

    fun registerEvents() {
        TickEvent.SERVER_POST.register(::tickHuntAllLevels)
    }

    private fun findSpawnPosition(serverLevel: ServerLevel, coffinPos: BlockPos): BlockPos? {
        val directions: Stream<BlockPos> = BlockPos.betweenClosedStream(AABB.ofSize(coffinPos.center, 10.0, 10.0, 10.0))

        return directions
            .filter { pos -> serverLevel.getBlockState(pos).isAir && serverLevel.getBlockState(pos.above()).isAir }
            .findFirst()
            .orElse(null)
    }

    @JvmStatic
    fun returnFromHunt(serverLevel: ServerLevel, huntData: HuntData): VampireEntity? {
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

        val data = getData(serverLevel)
        val mutableData = data.data.toMutableMap()
        val hunts = mutableData.getOrPut(playerUUID) { mutableListOf() }

        if (vampireEntity.huntedLastNight) {
            return
        }

        val savedNbt = CompoundTag()
        vampireEntity.saveAsPassenger(savedNbt)
        val huntData = HuntData(
            entityNbt = savedNbt,
            coffinPos = vampireEntity.coffinPos
                ?: vampireEntity.creationPos
                ?: serverLevel.getPlayerByUUID(playerUUID)?.blockPosition()
                ?: return,
            creationPos = vampireEntity.creationPos ?: return
        )
        hunts.add(huntData)

        setData(serverLevel, Data(mutableData))

        WitcheryPayloads.sendToPlayers(
            serverLevel,
            vampireEntity.blockPosition(),
            SpawnSmokeParticlesS2CPayload(vampireEntity.position())
        )
        vampireEntity.remove(Entity.RemovalReason.DISCARDED)
    }

    fun tickHuntAllLevels(minecraftServer: MinecraftServer?) {
        if (minecraftServer == null) {
            return
        }

        for (serverLevel in minecraftServer.allLevels) {
            val data = getData(serverLevel)
            if (data.data.isNotEmpty()) {
                tickHunt(serverLevel, data)
            }
        }
    }

    private fun tickHunt(serverLevel: ServerLevel, data: Data) {
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

            setData(serverLevel, data)
        }
    }
}