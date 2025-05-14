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
        registerS2C(SyncCauldronS2CPayload.ID, SyncCauldronS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(CauldronPoofS2CPayload.ID, CauldronPoofS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(AltarMultiplierSyncS2CPayload.ID, AltarMultiplierSyncS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(
            MutandisRemenantParticleS2CPayload.ID,
            MutandisRemenantParticleS2CPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncInfusionS2CPayload.ID, SyncInfusionS2CPayload.STREAM_CODEC) { payload, context ->
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

        registerS2C(SyncLightInfusionS2CPayload.ID, SyncLightInfusionS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }

        registerS2C(SyncNightmareS2CPayload.ID, SyncNightmareS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(
            SyncOtherwhereInfusionS2CPayload.ID,
            SyncOtherwhereInfusionS2CPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(
            SyncUnderWaterS2CPayload.ID,
            SyncUnderWaterS2CPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SpawnPoofParticlesS2CPayload.ID, SpawnPoofParticlesS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncVoodooDataS2CPayload.ID, SyncVoodooDataS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(OpenLecternGuidebookS2CPayload.ID, OpenLecternGuidebookS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncMiscS2CPayload.ID, SyncMiscS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncManifestationS2CPayload.ID, SyncManifestationS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncInfernalInfusionS2CPayload.ID, SyncInfernalInfusionS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncOwlAbilityS2CPayload.ID, SyncOwlAbilityS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncCurseS2CPayload.ID, SyncCurseS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncCorruptPoppetS2CPayload.ID, SyncCorruptPoppetS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncVampireS2CPayload.ID, SyncVampireS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncEtherealS2CPayload.ID, SyncEtherealS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncWerewolfS2CPayload.ID, SyncWerewolfS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncCovenS2CPayload.ID, SyncCovenS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncBloodS2CPayload.ID, SyncBloodS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncOtherBloodS2CPayload.ID, SyncOtherBloodS2CPayload.STREAM_CODEC) { payload, context ->
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
        registerS2C(SpawnSmokePoofParticlesS2CPayload.ID, SpawnSmokePoofParticlesS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncTransformationS2CPayload.ID, SyncTransformationS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncChainS2CPayload.ID, SyncChainS2CPayload.STREAM_CODEC) { payload, context ->
            payload.handleS2C(payload, context)
        }
        registerS2C(SyncBarkS2CPayload.ID, SyncBarkS2CPayload.STREAM_CODEC) { payload, context ->
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