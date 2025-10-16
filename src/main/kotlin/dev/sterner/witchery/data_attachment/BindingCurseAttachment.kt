package dev.sterner.witchery.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.payload.SyncBindingCurseS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object BindingCurseAttachment {


    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.BINDING_CURSE)
    }

    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.BINDING_CURSE, data)
    }

    fun sync(player: Player) {
        if (player is ServerPlayer) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                SyncBindingCurseS2CPayload(player, getData(player))
            )
        }
    }

    fun tick(player: Player) {
        if (player !is ServerPlayer) return
        if (player.level().gameTime % 20 != 0L) return

        val data = BindingCurseAttachment.getData(player)
        if (!data.isActive) return

        data.duration--

        if (data.duration <= 0) {
            data.isActive = false
        }

        BindingCurseAttachment.setData(player, data)
    }

    data class Data(
        var centerPos: BlockPos = BlockPos.ZERO,
        var radius: Double = 0.0,
        var duration: Int = 0,
        var isActive: Boolean = false
    ) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    BlockPos.CODEC.fieldOf("centerPos").forGetter { it.centerPos },
                    Codec.DOUBLE.fieldOf("radius").forGetter { it.radius },
                    Codec.INT.fieldOf("duration").forGetter { it.duration },
                    Codec.BOOL.fieldOf("isActive").forGetter { it.isActive }
                ).apply(instance, ::Data)
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Data> = StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                Data::centerPos,
                ByteBufCodecs.DOUBLE,
                Data::radius,
                ByteBufCodecs.VAR_INT,
                Data::duration,
                ByteBufCodecs.BOOL,
                Data::isActive,
                ::Data
            )
        }
    }

}