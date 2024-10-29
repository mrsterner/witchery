package dev.sterner.witchery.registry

import dev.architectury.networking.NetworkManager
import dev.architectury.platform.Platform
import dev.architectury.utils.Env
import dev.sterner.witchery.payload.*
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level


object WitcheryPayloads {

    fun register() {
        registerS2C(SyncCauldronS2CPacket.ID, SyncCauldronS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(CauldronPoofS2CPacket.ID, CauldronPoofS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(AltarMultiplierSyncS2CPacket.ID, AltarMultiplierSyncS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(
            MutandisRemenantParticleS2CPacket.ID,
            MutandisRemenantParticleS2CPacket.STREAM_CODEC
        ) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncInfusionS2CPacket.ID, SyncInfusionS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(CauldronEffectParticleS2CPayload.ID, CauldronEffectParticleS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }

        registerS2C(SyncLightInfusionS2CPacket.ID, SyncLightInfusionS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncOtherwhereInfusionS2CPacket.ID, SyncOtherwhereInfusionS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SpawnPoofParticles.ID, SpawnPoofParticles.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncVoodooDataS2CPacket.ID, SyncVoodooDataS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncPoppetDataS2CPacket.ID, SyncPoppetDataS2CPacket.STREAM_CODEC) { payload, context ->
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

    fun <T : CustomPacketPayload?> sendToPlayers(level: Level, payload: T) {
        if (level is ServerLevel) {
            for (serverLevel in level.server!!.allLevels) {
                for (player in serverLevel.players())
                NetworkManager.sendToPlayer(
                    player as ServerPlayer,
                    payload
                )
            }
        }
    }

    fun <T : CustomPacketPayload?> sendToPlayers(level: Level, pos: BlockPos, payload: T) {
        if (level is ServerLevel) {
            sendToPlayers(level, pos, payload)
        }
    }

    fun <T : CustomPacketPayload?> sendToPlayers(level: ServerLevel, pos: BlockPos, payload: T) {
        val players = level.chunkSource.chunkMap.getPlayers(ChunkPos(pos), false)
        for (player in players) {
            NetworkManager.sendToPlayer(
                player as ServerPlayer,
                payload
            )
        }
    }


}