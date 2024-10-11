package dev.sterner.witchery.registry

import dev.architectury.networking.NetworkManager
import dev.architectury.platform.Platform
import dev.architectury.utils.Env
import dev.sterner.witchery.payload.CauldronSmokeS2CPacket
import dev.sterner.witchery.payload.SyncCauldronS2CPacket
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload


object WitcheryPayloads {

    fun register() {
        registerS2C(SyncCauldronS2CPacket.ID, SyncCauldronS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(CauldronSmokeS2CPacket.ID, CauldronSmokeS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
    }

    private fun <T : CustomPacketPayload?> registerC2S(
        type: CustomPacketPayload.Type<T>,
        codec: StreamCodec<in RegistryFriendlyByteBuf?, T>,
        receiver: NetworkManager.NetworkReceiver<T>
    ) {
        NetworkManager.registerReceiver(NetworkManager.c2s(), type, codec, receiver)
    }

    private fun <T : CustomPacketPayload?> registerS2C(
        type: CustomPacketPayload.Type<T>,
        codec: StreamCodec<in RegistryFriendlyByteBuf?, T>,
        receiver: NetworkManager.NetworkReceiver<T>
    ) {
        if (Platform.getEnvironment() === Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(), type, codec, receiver)
        } else {
            NetworkManager.registerS2CPayloadType(type, codec)
        }
    }
}