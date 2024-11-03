package dev.sterner.witchery.fabric.registry

import dev.sterner.witchery.platform.AltarDataAttachment
import dev.sterner.witchery.platform.EntSpawnLevelAttachment
import dev.sterner.witchery.platform.MutandisDataAttachment
import dev.sterner.witchery.platform.SleepingPlayerLevelAttachment
import dev.sterner.witchery.platform.infusion.InfusionData
import dev.sterner.witchery.platform.infusion.InfusionType
import dev.sterner.witchery.platform.infusion.LightInfusionData
import dev.sterner.witchery.platform.infusion.OtherwhereInfusionData
import dev.sterner.witchery.platform.poppet.PoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetData
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType

object WitcheryFabricAttachmentRegistry {
    @Suppress("UnstableApiUsage")
    val MUTANDIS_LEVEL_DATA_TYPE: AttachmentType<MutandisDataAttachment.MutandisDataCodec> =
        AttachmentRegistry.builder<MutandisDataAttachment.MutandisDataCodec>()
            .persistent(MutandisDataAttachment.MutandisDataCodec.CODEC)
            .initializer { MutandisDataAttachment.MutandisDataCodec() }
            .buildAndRegister(MutandisDataAttachment.ID)

    @Suppress("UnstableApiUsage")
    val ALTAR_LEVEL_DATA_TYPE: AttachmentType<AltarDataAttachment.AltarDataCodec> =
        AttachmentRegistry.builder<AltarDataAttachment.AltarDataCodec>()
            .persistent(AltarDataAttachment.AltarDataCodec.CODEC)
            .initializer { AltarDataAttachment.AltarDataCodec() }
            .buildAndRegister(AltarDataAttachment.AltarDataCodec.ID)

    @Suppress("UnstableApiUsage")
    val INFUSION_PLAYER_DATA_TYPE: AttachmentType<InfusionData> =
        AttachmentRegistry.builder<InfusionData>()
            .persistent(InfusionData.CODEC)
            .initializer { InfusionData(InfusionType.NONE) }
            .buildAndRegister(InfusionData.ID)

    @Suppress("UnstableApiUsage")
    val LIGHT_INFUSION_PLAYER_DATA_TYPE: AttachmentType<LightInfusionData> =
        AttachmentRegistry.builder<LightInfusionData>()
            .persistent(LightInfusionData.CODEC)
            .initializer { LightInfusionData(false, 0) }
            .buildAndRegister(LightInfusionData.ID)

    @Suppress("UnstableApiUsage")
    val OTHERWHERE_INFUSION_PLAYER_DATA_TYPE: AttachmentType<OtherwhereInfusionData> =
        AttachmentRegistry.builder<OtherwhereInfusionData>()
            .persistent(OtherwhereInfusionData.CODEC)
            .initializer { OtherwhereInfusionData(0, 0) }
            .buildAndRegister(OtherwhereInfusionData.ID)

    @Suppress("UnstableApiUsage")
    val VOODOO_POPPET_DATA_TYPE: AttachmentType<VoodooPoppetData> =
        AttachmentRegistry.builder<VoodooPoppetData>()
            .persistent(VoodooPoppetData.CODEC)
            .initializer { VoodooPoppetData(false) }
            .buildAndRegister(VoodooPoppetData.ID)

    @Suppress("UnstableApiUsage")
    val POPPET_DATA_TYPE: AttachmentType<PoppetData> =
        AttachmentRegistry.builder<PoppetData>()
            .persistent(PoppetData.CODEC)
            .initializer { PoppetData(mutableListOf()) }
            .buildAndRegister(PoppetData.ID)

    @Suppress("UnstableApiUsage")
    val ENT_DATA_TYPE: AttachmentType<EntSpawnLevelAttachment.Data> =
        AttachmentRegistry.builder<EntSpawnLevelAttachment.Data>()
            .persistent(EntSpawnLevelAttachment.Data.DATA_CODEC)
            .initializer { EntSpawnLevelAttachment.Data() }
            .buildAndRegister(EntSpawnLevelAttachment.Data.ID)

    @Suppress("UnstableApiUsage")
    val SLEEPING_PLAYER_DATA_TYPE: AttachmentType<SleepingPlayerLevelAttachment.Data> =
        AttachmentRegistry.builder<SleepingPlayerLevelAttachment.Data>()
            .persistent(SleepingPlayerLevelAttachment.Data.CODEC)
            .initializer { SleepingPlayerLevelAttachment.Data() }
            .buildAndRegister(SleepingPlayerLevelAttachment.Data.ID)

    fun init(){

    }
}