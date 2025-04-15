package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player


class DismountBroomC2SPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(CompoundTag())

    constructor() : this(CompoundTag())

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(nbt)
    }

    fun handleC2S(payload: DismountBroomC2SPayload, context: NetworkManager.PacketContext?) {
        val player: Player? = context?.player
        if (player != null) {
            player.vehicle?.stopRiding()
            player.stopRiding()
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<DismountBroomC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("dismount_broom"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, DismountBroomC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> DismountBroomC2SPayload(buf) }
            )
    }
}