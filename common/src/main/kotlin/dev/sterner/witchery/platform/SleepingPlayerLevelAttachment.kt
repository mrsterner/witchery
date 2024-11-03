package dev.sterner.witchery.platform

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
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

    fun getPlayerFromSleeping(playerUUID: UUID, level: ServerLevel): UUID? {
        return getData(level).entries[playerUUID]
    }

    fun add(playerUUID: UUID, sleepingUUID: UUID, level: ServerLevel) {
        val oldData = getData(level)
        val updatedEntries = oldData.entries.toMutableMap()
        updatedEntries[playerUUID] = sleepingUUID
        setData(level, Data(updatedEntries))
    }

    fun remove(playerUUID: UUID, level: ServerLevel) {
        val oldData = getData(level)
        val updatedEntries = oldData.entries.toMutableMap()
        updatedEntries.remove(playerUUID)
        setData(level, Data(updatedEntries))
    }

    data class Data(val entries: MutableMap<UUID, UUID> = mutableMapOf()) {
        companion object {
            val ID: ResourceLocation = Witchery.id("sleeping_player_map")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.unboundedMap(Codecs.UUID, Codecs.UUID).fieldOf("entries").forGetter { it.entries }
                ).apply(instance, ::Data)
            }
        }
    }
}