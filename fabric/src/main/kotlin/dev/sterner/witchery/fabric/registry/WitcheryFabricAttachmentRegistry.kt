package dev.sterner.witchery.fabric.registry

import dev.sterner.witchery.platform.*
import dev.sterner.witchery.platform.infusion.*
import dev.sterner.witchery.platform.poppet.CorruptPoppetPlayerAttachment
import dev.sterner.witchery.platform.poppet.PoppetLevelAttachment
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.VoodooPoppetData
import dev.sterner.witchery.platform.teleport.TeleportQueueLevelAttachment
import dev.sterner.witchery.platform.transformation.*
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
    val INFUSION_PLAYER_DATA_TYPE: AttachmentType<InfusionPlayerAttachment.Data> =
        AttachmentRegistry.builder<InfusionPlayerAttachment.Data>()
            .persistent(InfusionPlayerAttachment.Data.CODEC)
            .copyOnDeath()
            .initializer { InfusionPlayerAttachment.Data(InfusionType.NONE) }
            .buildAndRegister(InfusionPlayerAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val LIGHT_INFUSION_PLAYER_DATA_TYPE: AttachmentType<LightInfusionPlayerAttachment.Data> =
        AttachmentRegistry.builder<LightInfusionPlayerAttachment.Data>()
            .persistent(LightInfusionPlayerAttachment.Data.CODEC)
            .copyOnDeath()
            .initializer { LightInfusionPlayerAttachment.Data(false, 0) }
            .buildAndRegister(LightInfusionPlayerAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val OTHERWHERE_INFUSION_PLAYER_DATA_TYPE: AttachmentType<OtherwhereInfusionPlayerAttachment.Data> =
        AttachmentRegistry.builder<OtherwhereInfusionPlayerAttachment.Data>()
            .persistent(OtherwhereInfusionPlayerAttachment.Data.CODEC)
            .copyOnDeath()
            .initializer { OtherwhereInfusionPlayerAttachment.Data(0, 0) }
            .buildAndRegister(OtherwhereInfusionPlayerAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val VOODOO_POPPET_DATA_TYPE: AttachmentType<VoodooPoppetData> =
        AttachmentRegistry.builder<VoodooPoppetData>()
            .persistent(VoodooPoppetData.CODEC)
            .initializer { VoodooPoppetData(false) }
            .buildAndRegister(VoodooPoppetData.ID)

    @Suppress("UnstableApiUsage")
    val POPPET_DATA_TYPE: AttachmentType<PoppetLevelAttachment.PoppetData> =
        AttachmentRegistry.builder<PoppetLevelAttachment.PoppetData>()
            .persistent(PoppetLevelAttachment.PoppetData.CODEC)
            .initializer { PoppetLevelAttachment.PoppetData(mutableListOf()) }
            .buildAndRegister(PoppetLevelAttachment.PoppetData.ID)

    @Suppress("UnstableApiUsage")
    val CORRUPT_POPPET_DATA_TYPE: AttachmentType<CorruptPoppetPlayerAttachment.Data> =
        AttachmentRegistry.builder<CorruptPoppetPlayerAttachment.Data>()
            .persistent(CorruptPoppetPlayerAttachment.Data.CODEC)
            .initializer { CorruptPoppetPlayerAttachment.Data() }
            .buildAndRegister(CorruptPoppetPlayerAttachment.Data.ID)

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
    val NECRO_DATA_ATTACHMENT: AttachmentType<NecromancerLevelAttachment.NecroList> =
        AttachmentRegistry.builder<NecromancerLevelAttachment.NecroList>()
            .persistent(NecromancerLevelAttachment.NecroList.CODEC)
            .initializer { NecromancerLevelAttachment.NecroList() }
            .buildAndRegister(NecromancerLevelAttachment.NecroList.ID)

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
    val INFERNAL_INFUSION_PLAYER_DATA_TYPE: AttachmentType<InfernalInfusionPlayerAttachment.Data> =
        AttachmentRegistry.builder<InfernalInfusionPlayerAttachment.Data>()
            .persistent(InfernalInfusionPlayerAttachment.Data.CODEC)
            .copyOnDeath()
            .initializer { InfernalInfusionPlayerAttachment.Data() }
            .buildAndRegister(InfernalInfusionPlayerAttachment.Data.ID)

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
    val ETHEREAL_DATA_TYPE: AttachmentType<EtherealEntityAttachment.Data> =
        AttachmentRegistry.builder<EtherealEntityAttachment.Data>()
            .persistent(EtherealEntityAttachment.Data.CODEC)
            .copyOnDeath()
            .initializer { EtherealEntityAttachment.Data() }
            .buildAndRegister(EtherealEntityAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val VAMPIRE_PLAYER_DATA_TYPE: AttachmentType<VampirePlayerAttachment.Data> =
        AttachmentRegistry.builder<VampirePlayerAttachment.Data>()
            .persistent(VampirePlayerAttachment.Data.CODEC)
            .copyOnDeath()
            .initializer { VampirePlayerAttachment.Data() }
            .buildAndRegister(VampirePlayerAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val WEREWOLF_PLAYER_DATA_TYPE: AttachmentType<WerewolfPlayerAttachment.Data> =
        AttachmentRegistry.builder<WerewolfPlayerAttachment.Data>()
            .persistent(WerewolfPlayerAttachment.Data.CODEC)
            .copyOnDeath()
            .initializer { WerewolfPlayerAttachment.Data() }
            .buildAndRegister(WerewolfPlayerAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val COVEN_PLAYER_DATA_TYPE: AttachmentType<CovenPlayerAttachment.CovenData> =
        AttachmentRegistry.builder<CovenPlayerAttachment.CovenData>()
            .persistent(CovenPlayerAttachment.CovenData.CODEC)
            .copyOnDeath()
            .initializer { CovenPlayerAttachment.CovenData() }
            .buildAndRegister(CovenPlayerAttachment.CovenData.ID)

    @Suppress("UnstableApiUsage")
    val BLOOD_LIVING_DATA_TYPE: AttachmentType<BloodPoolLivingEntityAttachment.Data> =
        AttachmentRegistry.builder<BloodPoolLivingEntityAttachment.Data>()
            .persistent(BloodPoolLivingEntityAttachment.Data.CODEC)
            .initializer { BloodPoolLivingEntityAttachment.Data() }
            .buildAndRegister(BloodPoolLivingEntityAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val TRANSFORMATION_PLAYER_DATA_TYPE: AttachmentType<TransformationPlayerAttachment.Data> =
        AttachmentRegistry.builder<TransformationPlayerAttachment.Data>()
            .persistent(TransformationPlayerAttachment.Data.CODEC)
            .initializer { TransformationPlayerAttachment.Data() }
            .buildAndRegister(TransformationPlayerAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val VAMPIRE_HUNT_LEVEL_DATA_ATTACHMENT: AttachmentType<VampireChildrenHuntLevelAttachment.Data> =
        AttachmentRegistry.builder<VampireChildrenHuntLevelAttachment.Data>()
            .persistent(VampireChildrenHuntLevelAttachment.Data.CODEC)
            .initializer { VampireChildrenHuntLevelAttachment.Data() }
            .buildAndRegister(VampireChildrenHuntLevelAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val BARK_BELT_PLAYER_DATA_ATTACHMENT: AttachmentType<BarkBeltPlayerAttachment.Data> =
        AttachmentRegistry.builder<BarkBeltPlayerAttachment.Data>()
            .persistent(BarkBeltPlayerAttachment.Data.CODEC)
            .initializer { BarkBeltPlayerAttachment.Data() }
            .buildAndRegister(BarkBeltPlayerAttachment.Data.ID)

    fun init() {

    }
}