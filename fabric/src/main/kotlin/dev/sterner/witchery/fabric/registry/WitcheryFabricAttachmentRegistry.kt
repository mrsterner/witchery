package dev.sterner.witchery.fabric.registry

import dev.sterner.witchery.platform.*
import dev.sterner.witchery.platform.infusion.*
import dev.sterner.witchery.platform.poppet.PoppetLevelAttachment
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.VoodooPoppetData
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType

object WitcheryFabricAttachmentRegistry {

    @Suppress("UnstableApiUsage")
    val MUTANDIS_LEVEL_DATA_TYPE: AttachmentType<MutandisLevelAttachment.MutandisDataCodec> =
        AttachmentRegistry.builder<MutandisLevelAttachment.MutandisDataCodec>()
            .persistent(MutandisLevelAttachment.MutandisDataCodec.CODEC)
            .initializer { MutandisLevelAttachment.MutandisDataCodec() }
            .buildAndRegister(MutandisLevelAttachment.ID)

    @Suppress("UnstableApiUsage")
    val ALTAR_LEVEL_DATA_TYPE: AttachmentType<AltarLevelAttachment.AltarDataCodec> =
        AttachmentRegistry.builder<AltarLevelAttachment.AltarDataCodec>()
            .persistent(AltarLevelAttachment.AltarDataCodec.CODEC)
            .initializer { AltarLevelAttachment.AltarDataCodec() }
            .buildAndRegister(AltarLevelAttachment.AltarDataCodec.ID)

    @Suppress("UnstableApiUsage")
    val INFUSION_PLAYER_DATA_TYPE: AttachmentType<InfusionData> =
        AttachmentRegistry.builder<InfusionData>()
            .persistent(InfusionData.CODEC)
            .copyOnDeath()
            .initializer { InfusionData(InfusionType.NONE) }
            .buildAndRegister(InfusionData.ID)

    @Suppress("UnstableApiUsage")
    val LIGHT_INFUSION_PLAYER_DATA_TYPE: AttachmentType<LightInfusionData> =
        AttachmentRegistry.builder<LightInfusionData>()
            .persistent(LightInfusionData.CODEC)
            .copyOnDeath()
            .initializer { LightInfusionData(false, 0) }
            .buildAndRegister(LightInfusionData.ID)

    @Suppress("UnstableApiUsage")
    val OTHERWHERE_INFUSION_PLAYER_DATA_TYPE: AttachmentType<OtherwhereInfusionData> =
        AttachmentRegistry.builder<OtherwhereInfusionData>()
            .persistent(OtherwhereInfusionData.CODEC)
            .copyOnDeath()
            .initializer { OtherwhereInfusionData(0, 0) }
            .buildAndRegister(OtherwhereInfusionData.ID)

    @Suppress("UnstableApiUsage")
    val VOODOO_POPPET_DATA_TYPE: AttachmentType<VoodooPoppetData> =
        AttachmentRegistry.builder<VoodooPoppetData>()
            .persistent(VoodooPoppetLivingEntityAttachment.VoodooPoppetData.CODEC)
            .initializer { VoodooPoppetData(false) }
            .buildAndRegister(VoodooPoppetLivingEntityAttachment.VoodooPoppetData.ID)

    @Suppress("UnstableApiUsage")
    val POPPET_DATA_TYPE: AttachmentType<PoppetLevelAttachment.PoppetData> =
        AttachmentRegistry.builder<PoppetLevelAttachment.PoppetData>()
            .persistent(PoppetLevelAttachment.PoppetData.CODEC)
            .initializer { PoppetLevelAttachment.PoppetData(mutableListOf()) }
            .buildAndRegister(PoppetLevelAttachment.PoppetData.ID)

    @Suppress("UnstableApiUsage")
    val ENT_DATA_TYPE: AttachmentType<EntSpawnLevelAttachment.Data> =
        AttachmentRegistry.builder<EntSpawnLevelAttachment.Data>()
            .persistent(EntSpawnLevelAttachment.Data.DATA_CODEC)
            .initializer { EntSpawnLevelAttachment.Data() }
            .buildAndRegister(EntSpawnLevelAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val SLEEPING_PLAYER_DATA_TYPE: AttachmentType<SleepingLevelAttachment.Data> =
        AttachmentRegistry.builder<SleepingLevelAttachment.Data>()
            .persistent(SleepingLevelAttachment.Data.CODEC)
            .copyOnDeath()
            .initializer { SleepingLevelAttachment.Data() }
            .buildAndRegister(SleepingLevelAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val TELEPORT_QUEUE_DATA_ATTACHMENT: AttachmentType<TeleportQueueLevelAttachment.Data> =
        AttachmentRegistry.builder<TeleportQueueLevelAttachment.Data>()
            .persistent(TeleportQueueLevelAttachment.Data.CODEC)
            .initializer { TeleportQueueLevelAttachment.Data() }
            .buildAndRegister(TeleportQueueLevelAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val MISC_PLAYER_DATA_ATTACHMENT: AttachmentType<MiscPlayerAttachment.Data> =
        AttachmentRegistry.builder<MiscPlayerAttachment.Data>()
            .persistent(MiscPlayerAttachment.Data.CODEC)
            .copyOnDeath()
            .initializer { MiscPlayerAttachment.Data() }
            .buildAndRegister(MiscPlayerAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val MANIFESTATION_PLAYER_DATA_ATTACHMENT: AttachmentType<ManifestationPlayerAttachment.Data> =
        AttachmentRegistry.builder<ManifestationPlayerAttachment.Data>()
            .persistent(ManifestationPlayerAttachment.Data.CODEC)
            .initializer { ManifestationPlayerAttachment.Data() }
            .buildAndRegister(ManifestationPlayerAttachment.Data.ID)


    @Suppress("UnstableApiUsage")
    val INFERNAL_INFUSION_PLAYER_DATA_TYPE: AttachmentType<InfernalInfusionData> =
        AttachmentRegistry.builder<InfernalInfusionData>()
            .persistent(InfernalInfusionData.CODEC)
            .copyOnDeath()
            .initializer { InfernalInfusionData() }
            .buildAndRegister(InfernalInfusionData.ID)

    @Suppress("UnstableApiUsage")
    val DEATH_QUEUE_LEVEL_DATA_TYPE: AttachmentType<DeathQueueLevelAttachment.Data> =
        AttachmentRegistry.builder<DeathQueueLevelAttachment.Data>()
            .persistent(DeathQueueLevelAttachment.Data.CODEC)
            .initializer { DeathQueueLevelAttachment.Data() }
            .buildAndRegister(DeathQueueLevelAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val FAMILIAR_LEVEL_DATA_TYPE: AttachmentType<FamiliarLevelAttachment.Data> =
        AttachmentRegistry.builder<FamiliarLevelAttachment.Data>()
            .persistent(FamiliarLevelAttachment.Data.CODEC)
            .initializer { FamiliarLevelAttachment.Data() }
            .buildAndRegister(FamiliarLevelAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val CURSE_PLAYER_DATA_TYPE: AttachmentType<CursePlayerAttachment.Data> =
        AttachmentRegistry.builder<CursePlayerAttachment.Data>()
            .persistent(CursePlayerAttachment.Data.CODEC)
            .copyOnDeath()
            .initializer { CursePlayerAttachment.Data() }
            .buildAndRegister(CursePlayerAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val NIGHTMARE_PLAYER_DATA_TYPE: AttachmentType<NightmarePlayerAttachment.Data> =
        AttachmentRegistry.builder<NightmarePlayerAttachment.Data>()
            .persistent(NightmarePlayerAttachment.Data.CODEC)
            .initializer { NightmarePlayerAttachment.Data() }
            .buildAndRegister(NightmarePlayerAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val VAMPIRE_PLAYER_DATA_TYPE: AttachmentType<VampirePlayerAttachment.Data> =
        AttachmentRegistry.builder<VampirePlayerAttachment.Data>()
            .persistent(VampirePlayerAttachment.Data.CODEC)
            .copyOnDeath()
            .initializer { VampirePlayerAttachment.Data() }
            .buildAndRegister(VampirePlayerAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val BLOOD_LIVING_DATA_TYPE: AttachmentType<BloodPoolLivingEntityAttachment.Data> =
        AttachmentRegistry.builder<BloodPoolLivingEntityAttachment.Data>()
            .persistent(BloodPoolLivingEntityAttachment.Data.CODEC)
            .initializer { BloodPoolLivingEntityAttachment.Data(1200, 1200) }
            .buildAndRegister(BloodPoolLivingEntityAttachment.Data.ID)


    fun init() {

    }
}