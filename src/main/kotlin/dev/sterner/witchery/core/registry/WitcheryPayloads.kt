package dev.sterner.witchery.registry

import dev.sterner.witchery.payload.*
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext

object WitcheryPayloads {

    fun onRegisterPayloadHandlers(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")

        // --- S2C Payloads ---
        registrar.playToClient(SyncChainS2CPayload.ID, SyncChainS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncCauldronS2CPayload.ID, SyncCauldronS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(CauldronPoofS2CPayload.ID, CauldronPoofS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            AltarMultiplierSyncS2CPayload.ID,
            AltarMultiplierSyncS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            MutandisRemenantParticleS2CPayload.ID,
            MutandisRemenantParticleS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncInfusionS2CPayload.ID, SyncInfusionS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncInventoryLockS2CPayload.ID, SyncInventoryLockS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            CauldronEffectParticleS2CPayload.ID,
            CauldronEffectParticleS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            CauldronPotionBrewParticleS2CPayload.ID,
            CauldronPotionBrewParticleS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncLightInfusionS2CPayload.ID, SyncLightInfusionS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncNightmareS2CPayload.ID, SyncNightmareS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            SyncOtherwhereInfusionS2CPayload.ID,
            SyncOtherwhereInfusionS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            HighlightOresS2CPayload.ID,
            HighlightOresS2CPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncUnderWaterS2CPayload.ID, SyncUnderWaterS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            SpawnPoofParticlesS2CPayload.ID,
            SpawnPoofParticlesS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncVoodooDataS2CPayload.ID, SyncVoodooDataS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            OpenLecternGuidebookS2CPayload.ID,
            OpenLecternGuidebookS2CPayload.STREAM_CODEC
        ) { payload, ctx ->
            payload.handleOnClient(ctx)
        }
        registrar.playToClient(
            OpenTarotScreenS2CPayload.ID,
            OpenTarotScreenS2CPayload.STREAM_CODEC
        ) { payload, ctx ->
            payload.handleOnClient(ctx)
        }
        registrar.playToClient(
            OptimizedSelectiveSyncPayload.ID,
            OptimizedSelectiveSyncPayload.STREAM_CODEC
        ) { payload, ctx ->
            payload.handleOnClient(ctx)
        }
        registrar.playToClient(SyncMiscS2CPayload.ID, SyncMiscS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncManifestationS2CPayload.ID, SyncManifestationS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            SyncInfernalInfusionS2CPayload.ID,
            SyncInfernalInfusionS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncOwlAbilityS2CPayload.ID, SyncOwlAbilityS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncSleepingShellS2CPayload.ID, SyncSleepingShellS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncCurseS2CPayload.ID, SyncCurseS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            SyncTarotS2CPayload.ID,
            SyncTarotS2CPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleOnClient()
        }

        registrar.playToServer(
            LockInTarotCardsC2SPayload.ID,
            LockInTarotCardsC2SPayload.STREAM_CODEC
        ) { payload, context ->
            context.enqueueWork {
                payload.handleOnServer(context)
            }
        }

        registrar.playToServer(
            ReadTabletC2SPayload.ID,
            ReadTabletC2SPayload.STREAM_CODEC
        ) { payload, context ->
            context.enqueueWork {
                payload.handleOnServer(context)
            }
        }
        registrar.playToClient(SyncCorruptPoppetS2CPayload.ID, SyncCorruptPoppetS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncAfflictionS2CPayload.ID, SyncAfflictionS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncEtherealS2CPayload.ID, SyncEtherealS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncCovenS2CPayload.ID, SyncCovenS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncBloodS2CPayload.ID, SyncBloodS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncBindingCurseS2CPayload.ID, SyncBindingCurseS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncSoulS2CPayload.ID, SyncSoulS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncOtherBloodS2CPayload.ID, SyncOtherBloodS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            SpawnBloodParticlesS2CPayload.ID,
            SpawnBloodParticlesS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            SpawnItemParticlesS2CPayload.ID,
            SpawnItemParticlesS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            SpawnTransfixParticlesS2CPayload.ID,
            SpawnTransfixParticlesS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            SpawnSmokeParticlesS2CPayload.ID,
            SpawnSmokeParticlesS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            SpawnNecroParticlesS2CPayload.ID,
            SpawnNecroParticlesS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            RefreshDimensionsS2CPayload.ID,
            RefreshDimensionsS2CPayload.STREAM_CODEC
        ) { payload, ctx ->
            payload.handleOnClient(ctx)
        }
        registrar.playToClient(
            SpawnSmokePoofParticlesS2CPayload.ID,
            SpawnSmokePoofParticlesS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            SyncTransformationS2CPayload.ID,
            SyncTransformationS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncBarkS2CPayload.ID, SyncBarkS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(SyncAIEntityToggleS2CPayload.ID, SyncAIEntityToggleS2CPayload.STREAM_CODEC) { payload, _ ->
            payload.handleOnClient()
        }
        registrar.playToClient(
            SpawnSleepingDeathParticleS2CPayload.ID,
            SpawnSleepingDeathParticleS2CPayload.STREAM_CODEC
        ) { payload, _ ->
            payload.handleOnClient()
        }

        registrar.playToClient(
            SyncPossessionComponentS2CPayload.TYPE,
            SyncPossessionComponentS2CPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleOnClient()
        }

        registrar.playToClient(
            SyncPossessedDataS2CPayload.TYPE,
            SyncPossessedDataS2CPayload.STREAM_CODEC
        ) { payload, context ->
            payload.handleOnClient()
        }

        // --- C2S Payloads ---
        registrar.playToServer(DismountBroomC2SPayload.ID, DismountBroomC2SPayload.STREAM_CODEC) { payload, ctx ->
            payload.handleOnServer(ctx)
        }
        registrar.playToServer(
            AfflictionAbilitySelectionC2SPayload.ID,
            AfflictionAbilitySelectionC2SPayload.STREAM_CODEC
        ) { payload, ctx ->
            payload.handleOnServer(ctx)
        }
        registrar.playToServer(
            AfflictionAbilityUseC2SPayload.ID,
            AfflictionAbilityUseC2SPayload.STREAM_CODEC
        ) { payload, ctx ->
            payload.handleOnServer(ctx)
        }
        registrar.playToServer(
            SelectUrnPotionC2SPayload.ID,
            SelectUrnPotionC2SPayload.STREAM_CODEC
        ) { payload, ctx ->
            payload.handleOnServer(ctx)
        }
        registrar.playToServer(
            GrantWitcheryAdvancementsC2SPayload.ID,
            GrantWitcheryAdvancementsC2SPayload.STREAM_CODEC
        ) { payload, ctx ->
            payload.handleOnServer(ctx)
        }
        registrar.playToServer(
            UpdateSelectedAbilitiesC2SPayload.ID,
            UpdateSelectedAbilitiesC2SPayload.STREAM_CODEC
        ) { payload, ctx ->
            payload.handleOnServer(ctx)
        }
    }
}
