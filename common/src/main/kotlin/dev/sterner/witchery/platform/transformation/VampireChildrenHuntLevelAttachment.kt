package dev.sterner.witchery.platform.transformation

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.VampireEntity
import dev.sterner.witchery.payload.SpawnPoofParticles
import dev.sterner.witchery.payload.SpawnSmokeParticlesS2CPayload
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.phys.AABB
import java.util.UUID
import java.util.stream.Stream

object VampireChildrenHuntLevelAttachment {

    @ExpectPlatform
    @JvmStatic
    fun getData(serverLevel: ServerLevel): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(serverLevel: ServerLevel, data: Data) {
        throw AssertionError()
    }

    @JvmStatic
    fun getPlayerHunts(serverLevel: ServerLevel, playerUUID: UUID): List<HuntData> {
        val data = getData(serverLevel)
        return data.data[playerUUID]?.toList() ?: emptyList()
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

        entity.moveTo(spawnPos.x + 0.5, spawnPos.y.toDouble(), spawnPos.z + 0.5, serverLevel.random.nextFloat() * 360F, 0F)
        serverLevel.addFreshEntity(entity)
        return entity as VampireEntity
    }

    @JvmStatic
    fun tryStarHunt(serverLevel: ServerLevel, vampireEntity: VampireEntity, playerUUID: UUID) {
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

        WitcheryPayloads.sendToPlayers(serverLevel, vampireEntity.blockPosition(), SpawnSmokeParticlesS2CPayload(vampireEntity.position()))
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




    class HuntData(
        val entityNbt: CompoundTag,
        val coffinPos: BlockPos,
        val creationPos: BlockPos,
    ) {

        companion object {
            val CODEC: Codec<HuntData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    CompoundTag.CODEC.fieldOf("entityNbt").forGetter { it.entityNbt },
                    BlockPos.CODEC.fieldOf("coffinPos").forGetter { it.coffinPos },
                    BlockPos.CODEC.fieldOf("creationPos").forGetter { it.creationPos },
                ).apply(instance, ::HuntData)
            }
        }
    }

    data class Data(val data: MutableMap<UUID, MutableList<HuntData>> = mutableMapOf()){

        companion object {
            val ID: ResourceLocation = Witchery.id("vampire_hunt_level_data")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.unboundedMap(
                        Codecs.UUID,
                        HuntData.CODEC.listOf().xmap({ it.toMutableList() }, { it.toList() })
                    ).fieldOf("data").forGetter { it.data }
                ).apply(instance, ::Data)
            }
        }
    }
}