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
        //S2C
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
        registerS2C(
            CauldronEffectParticleS2CPayload.ID,
            CauldronEffectParticleS2CPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(
            CauldronPotionBrewParticleS2CPayload.ID,
            CauldronPotionBrewParticleS2CPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleS2C(payload, context)
        }

        registerS2C(SyncLightInfusionS2CPacket.ID, SyncLightInfusionS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }

        registerS2C(SyncNightmareS2CPacket.ID, SyncNightmareS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(
            SyncOtherwhereInfusionS2CPacket.ID,
            SyncOtherwhereInfusionS2CPacket.STREAM_CODEC
        ) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(
            SyncUnderWaterS2CPayload.ID,
            SyncUnderWaterS2CPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SpawnPoofParticles.ID, SpawnPoofParticles.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncVoodooDataS2CPacket.ID, SyncVoodooDataS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(OpenLecternGuidebook.ID, OpenLecternGuidebook.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncMiscS2CPacket.ID, SyncMiscS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncManifestationS2CPacket.ID, SyncManifestationS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncInfernalInfusionS2CPacket.ID, SyncInfernalInfusionS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncOwlAbilityS2CPayload.ID, SyncOwlAbilityS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncCurseS2CPacket.ID, SyncCurseS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncCorruptPoppetS2CPacket.ID, SyncCorruptPoppetS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncVampireS2CPacket.ID, SyncVampireS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncEtherealS2CPacket.ID, SyncEtherealS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncWerewolfS2CPacket.ID, SyncWerewolfS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncCovenS2CPacket.ID, SyncCovenS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncBloodS2CPacket.ID, SyncBloodS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncOtherBloodS2CPacket.ID, SyncOtherBloodS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SpawnBloodParticlesS2CPayload.ID, SpawnBloodParticlesS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SpawnItemParticlesS2CPayload.ID, SpawnItemParticlesS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(
            SpawnTransfixParticlesS2CPayload.ID,
            SpawnTransfixParticlesS2CPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SpawnSmokeParticlesS2CPayload.ID, SpawnSmokeParticlesS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SpawnNecroParticlesS2CPayload.ID, SpawnNecroParticlesS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(RefreshDimensionsS2CPayload.ID, RefreshDimensionsS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SpawnSmokePoofParticles.ID, SpawnSmokePoofParticles.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncTransformationS2CPayload.ID, SyncTransformationS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncChainS2CPayload.ID, SyncChainS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncBarkS2CPacket.ID, SyncBarkS2CPacket.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(
            SpawnSleepingDeathParticleS2CPayload.ID,
            SpawnSleepingDeathParticleS2CPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleS2C(payload, context)
        }
        //C2S
        registerC2S(DismountBroomC2SPayload.ID, DismountBroomC2SPayload.STREAM_CODEC) { payload, context ->
            payload.handleC2S(payload, context)
        }
        registerC2S(
            VampireAbilitySelectionC2SPayload.ID,
            VampireAbilitySelectionC2SPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleC2S(payload, context)
        }
        registerC2S(
            WerewolfAbilitySelectionC2SPayload.ID,
            WerewolfAbilitySelectionC2SPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleC2S(payload, context)
        }
        registerC2S(VampireAbilityUseC2SPayload.ID, VampireAbilityUseC2SPayload.STREAM_CODEC) { payload, context ->
            payload.handleC2S(payload, context)
        }
        registerC2S(WerewolfAbilityUseC2SPayload.ID, WerewolfAbilityUseC2SPayload.STREAM_CODEC) { payload, context ->
            payload.handleC2S(payload, context)
        }
    }

    private fun <T : CustomPacketPayload?> registerC2S(
        type: CustomPacketPayload.Type<T>,
        codec: StreamCodec<in RegistryFriendlyByteBuf, T>,
        receiver: NetworkManager.NetworkReceiver<T>
    ) {
        NetworkManager.registerReceiver(NetworkManager.c2s(), type, codec, receiver)
    }

    private fun <T : CustomPacketPayload?> registerS2C(
        type: CustomPacketPayload.Type<T>,
        codec: StreamCodec<in RegistryFriendlyByteBuf, T>,
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
            for (serverLevel in level.server.allLevels) {
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