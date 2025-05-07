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

object SleepingLevelAttachment {

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