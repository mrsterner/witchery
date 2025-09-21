package dev.sterner.witchery.data_attachment

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import java.util.*

object SleepingLevelAttachment {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(WitcheryDataAttachments.SLEEPING_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(WitcheryDataAttachments.SLEEPING_PLAYER_DATA_ATTACHMENT, data)
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