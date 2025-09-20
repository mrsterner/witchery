package dev.sterner.witchery.data_attachment.transformation

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncTransformationS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object TransformationPlayerAttachment {

    @JvmStatic
    fun getData(player: Player): TransformationPlayerAttachment.Data {
        return player.getData(WitcheryDataAttachments.TRANSFORMATION_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: TransformationPlayerAttachment.Data) {
        player.setData(WitcheryDataAttachments.TRANSFORMATION_PLAYER_DATA_ATTACHMENT, data)
        TransformationPlayerAttachment.sync(player, data)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, SyncTransformationS2CPayload(player, data))
        }
    }

    data class Data(
        val transformationType: TransformationType = TransformationType.NONE,
        val batFormTicker: Int = 0,
        val maxBatTimeClient: Int = 0,
    ) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    TransformationType.TRANSFORMATION_CODEC.fieldOf("transformationType")
                        .forGetter { it.transformationType },
                    Codec.INT.fieldOf("batFormTicker").forGetter { it.batFormTicker },
                    Codec.INT.fieldOf("maxBatTimeClient").forGetter { it.maxBatTimeClient },

                    ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("transformation_player_data")
        }
    }

    enum class TransformationType : StringRepresentable {
        NONE,
        BAT,
        WOLF,
        WEREWOLF;

        override fun getSerializedName(): String {
            return name.lowercase()
        }

        companion object {
            val TRANSFORMATION_CODEC: Codec<TransformationType> =
                StringRepresentable.fromEnum(TransformationType::values)
        }
    }
}