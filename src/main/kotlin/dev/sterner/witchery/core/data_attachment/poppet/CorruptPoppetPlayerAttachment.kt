package dev.sterner.witchery.core.data_attachment.poppet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncCorruptPoppetS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object CorruptPoppetPlayerAttachment {

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.CORRUPT_POPPET_DATA_ATTACHMENT, data)
    }

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.CORRUPT_POPPET_DATA_ATTACHMENT)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                SyncCorruptPoppetS2CPayload(player, data)
            )
        }
    }

    data class Data(
        val corruptedPoppetCount: Int = 0,
        val corruptedPoppets: MutableSet<ResourceLocation> = mutableSetOf()
    ) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("corruptedPoppetCount").forGetter { it.corruptedPoppetCount },
                    ResourceLocation.CODEC.listOf().xmap(
                        { it.toMutableSet() },
                        { it.toList() }
                    ).fieldOf("corruptedPoppets").forGetter { it.corruptedPoppets }
                ).apply(instance, ::Data)
            }
            val ID: ResourceLocation = Witchery.id("corrupt_poppet_data")
        }
    }
}