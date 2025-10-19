package dev.sterner.witchery.core.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.network.SyncSoulS2CPayload
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object SoulPoolPlayerAttachment {

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.SOUL_POOL_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.SOUL_POOL_PLAYER_DATA_ATTACHMENT, data)
        sync(player, data)
    }

    fun sync(entity: Player, data: Data) {
        if (entity.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, SyncSoulS2CPayload(entity, data))
        }
    }

    data class Data(
        val maxSouls: Int = 0,
        val soulPool: Int = 0
    ) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("maxSouls").forGetter { it.maxSouls },
                    Codec.INT.fieldOf("soulPool").forGetter { it.soulPool }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.Companion.id("soul_pool_data")
        }
    }
}