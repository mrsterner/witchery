package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.werewolf.WerewolfEventHandler
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player


class WerewolfAbilityUseC2SPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(ordinal: Int) : this(CompoundTag().apply {
        putInt("Ordinal", ordinal)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(nbt)
    }

    fun handleC2S(payload: WerewolfAbilityUseC2SPayload, context: NetworkManager.PacketContext?) {
        val player: Player? = context?.player
        val ordinal = payload.nbt.getInt("Ordinal")

        if (player != null) {
            WerewolfEventHandler.parseAbilityFromIndex(player, ordinal)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<WerewolfAbilityUseC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("werewolf_use_ability"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, WerewolfAbilityUseC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> WerewolfAbilityUseC2SPayload(buf) }
            )
    }
}