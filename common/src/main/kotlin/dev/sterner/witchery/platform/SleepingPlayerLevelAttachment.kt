package dev.sterner.witchery.platform

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import java.util.*

object SleepingPlayerLevelAttachment {

    @ExpectPlatform
    @JvmStatic
    fun getData(level: ServerLevel): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        throw AssertionError()
    }

    fun getPlayerFromSleeping(playerUUID: UUID, level: ServerLevel): PlayerSleepingData? {
        return getData(level).entries[playerUUID]
    }

    fun add(playerUUID: UUID, sleepingUUID: UUID, pos: BlockPos, level: ServerLevel) {
        val oldData = getData(level)
        val updatedEntries = oldData.entries.toMutableMap()
        updatedEntries[playerUUID] = PlayerSleepingData(sleepingUUID, pos)
        setData(level, Data(updatedEntries))
    }

    fun remove(playerUUID: UUID, level: ServerLevel) {
        val oldData = getData(level)
        val updatedEntries = oldData.entries.toMutableMap()
        updatedEntries.remove(playerUUID)
        setData(level, Data(updatedEntries))
    }

    data class PlayerSleepingData(val uuid: UUID, val pos: BlockPos) {

        companion object {
            val CODEC: Codec<PlayerSleepingData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codecs.UUID.fieldOf("uuid").forGetter { it.uuid },
                    BlockPos.CODEC.fieldOf("pos").forGetter { it.pos }
                ).apply(instance, ::PlayerSleepingData)
            }
        }
    }

    data class Data(val entries: MutableMap<UUID, PlayerSleepingData> = mutableMapOf()) {

        companion object {
            val ID: ResourceLocation = Witchery.id("sleeping_player_map")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.unboundedMap(Codecs.UUID, PlayerSleepingData.CODEC)
                        .fieldOf("entries").forGetter { it.entries }
                ).apply(instance, ::Data)
            }
        }
    }
}