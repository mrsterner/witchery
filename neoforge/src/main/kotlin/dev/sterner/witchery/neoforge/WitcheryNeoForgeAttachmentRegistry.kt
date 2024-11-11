package dev.sterner.witchery.neoforge

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.*
import dev.sterner.witchery.platform.infusion.InfernalInfusionData
import dev.sterner.witchery.platform.infusion.InfusionData
import dev.sterner.witchery.platform.infusion.LightInfusionData
import dev.sterner.witchery.platform.infusion.OtherwhereInfusionData
import dev.sterner.witchery.platform.poppet.PoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetData
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.function.Supplier

object WitcheryNeoForgeAttachmentRegistry {

    val ATTACHMENT_TYPES: DeferredRegister<AttachmentType<*>> =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Witchery.MODID)

    @JvmStatic
    val MUTANDIS_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<MutandisDataAttachment.MutandisDataCodec>> =
        ATTACHMENT_TYPES.register(
            "mutandis_level_data",
            Supplier {
                AttachmentType.builder(Supplier { MutandisDataAttachment.MutandisDataCodec() })
                    .serialize(MutandisDataAttachment.MutandisDataCodec.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val ALTAR_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<AltarDataAttachment.AltarDataCodec>> =
        ATTACHMENT_TYPES.register(
            "altar_level_data",
            Supplier {
                AttachmentType.builder(Supplier { AltarDataAttachment.AltarDataCodec() })
                    .serialize(AltarDataAttachment.AltarDataCodec.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val INFUSION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<InfusionData>> = ATTACHMENT_TYPES.register(
        "infusion_player_data",
        Supplier {
            AttachmentType.builder(Supplier { InfusionData() })
                .serialize(InfusionData.CODEC)
                .build()
        }
    )

    @JvmStatic
    val LIGHT_INFUSION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<LightInfusionData>> = ATTACHMENT_TYPES.register(
        "light_infusion_player_data",
        Supplier {
            AttachmentType.builder(Supplier { LightInfusionData() })
                .serialize(LightInfusionData.CODEC)
                .build()
        }
    )

    @JvmStatic
    val OTHERWHERE_INFUSION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<OtherwhereInfusionData>> =
        ATTACHMENT_TYPES.register(
            "otherwhere_infusion_player_data",
            Supplier {
                AttachmentType.builder(Supplier { OtherwhereInfusionData() })
                    .serialize(OtherwhereInfusionData.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val VOODOO_POPPET_DATA_ATTACHMENT: Supplier<AttachmentType<VoodooPoppetData>> = ATTACHMENT_TYPES.register(
        "voodoo_poppet_data",
        Supplier {
            AttachmentType.builder(Supplier { VoodooPoppetData(false) })
                .serialize(VoodooPoppetData.CODEC)
                .build()
        }
    )

    @JvmStatic
    val POPPET_DATA_ATTACHMENT: Supplier<AttachmentType<PoppetData>> = ATTACHMENT_TYPES.register(
        "poppet_data",
        Supplier {
            AttachmentType.builder(Supplier { PoppetData(mutableListOf()) })
                .serialize(PoppetData.CODEC)
                .build()
        }
    )

    @JvmStatic
    val ENT_DATA_ATTACHMENT: Supplier<AttachmentType<EntSpawnLevelAttachment.Data>> = ATTACHMENT_TYPES.register(
        "ent_data",
        Supplier {
            AttachmentType.builder(Supplier { EntSpawnLevelAttachment.Data() })
                .serialize(EntSpawnLevelAttachment.Data.DATA_CODEC)
                .build()
        }
    )

    @JvmStatic
    val SLEEPING_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<SleepingPlayerLevelAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "sleeping_player",
            Supplier {
                AttachmentType.builder(Supplier { SleepingPlayerLevelAttachment.Data() })
                    .serialize(SleepingPlayerLevelAttachment.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val TELEPORT_QUEUE_DATA_ATTACHMENT: Supplier<AttachmentType<TeleportQueueLevelAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "teleport_queue",
            Supplier {
                AttachmentType.builder(Supplier { TeleportQueueLevelAttachment.Data() })
                    .serialize(TeleportQueueLevelAttachment.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val MISC_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<PlayerMiscDataAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "misc_player",
            Supplier {
                AttachmentType.builder(Supplier { PlayerMiscDataAttachment.Data() })
                    .serialize(PlayerMiscDataAttachment.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val MANIFESTATION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<PlayerManifestationDataAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "manifestation_player",
            Supplier {
                AttachmentType.builder(Supplier { PlayerManifestationDataAttachment.Data() })
                    .serialize(PlayerManifestationDataAttachment.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<InfernalInfusionData>> = ATTACHMENT_TYPES.register(
        "infernal_infusion_player_data",
        Supplier {
            AttachmentType.builder(Supplier { InfernalInfusionData() })
                .serialize(InfernalInfusionData.CODEC)
                .build()
        }
    )

    @JvmStatic
    val DEATH_QUEUE_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<DeathQueueLevelAttachment.Data>> = ATTACHMENT_TYPES.register(
        "death_queue_level_data",
        Supplier {
            AttachmentType.builder(Supplier { DeathQueueLevelAttachment.Data() })
                .serialize(DeathQueueLevelAttachment.Data.CODEC)
                .build()
        }
    )

    @JvmStatic
    val FAMILIAR_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<FamiliarLevelAttachment.Data>> = ATTACHMENT_TYPES.register(
        "familiar_level_data",
        Supplier {
            AttachmentType.builder(Supplier { FamiliarLevelAttachment.Data() })
                .serialize(FamiliarLevelAttachment.Data.CODEC)
                .build()
        }
    )
}