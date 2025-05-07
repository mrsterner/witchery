package dev.sterner.witchery.neoforge

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.*
import dev.sterner.witchery.platform.infusion.InfernalInfusionData
import dev.sterner.witchery.platform.infusion.InfusionData
import dev.sterner.witchery.platform.infusion.LightInfusionData
import dev.sterner.witchery.platform.infusion.OtherwhereInfusionData
import dev.sterner.witchery.platform.poppet.PoppetLevelAttachment
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment
import dev.sterner.witchery.platform.teleport.TeleportQueueLevelAttachment
import dev.sterner.witchery.platform.transformation.*
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.function.Supplier

object WitcheryNeoForgeAttachmentRegistry {

    val ATTACHMENT_TYPES: DeferredRegister<AttachmentType<*>> =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Witchery.MODID)

    @JvmStatic
    val MUTANDIS_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<MutandisLevelAttachment.MutandisDataCodec>> =
        ATTACHMENT_TYPES.register(
            "mutandis_level_data",
            Supplier {
                AttachmentType.builder(Supplier { MutandisLevelAttachment.MutandisDataCodec() })
                    .serialize(MutandisLevelAttachment.MutandisDataCodec.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val ALTAR_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<AltarLevelAttachment.AltarDataCodec>> =
        ATTACHMENT_TYPES.register(
            "altar_level_data",
            Supplier {
                AttachmentType.builder(Supplier { AltarLevelAttachment.AltarDataCodec() })
                    .serialize(AltarLevelAttachment.AltarDataCodec.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val ETHEREAL_DATA_ATTACHMENT: Supplier<AttachmentType<EtherealEntityAttachment.Data>> = ATTACHMENT_TYPES.register(
        "ethereal",
        Supplier {
            AttachmentType.builder(Supplier { EtherealEntityAttachment.Data() })
                .serialize(EtherealEntityAttachment.Data.CODEC)
                .copyOnDeath()
                .build()
        }
    )

    @JvmStatic
    val INFUSION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<InfusionData>> = ATTACHMENT_TYPES.register(
        "infusion_player_data",
        Supplier {
            AttachmentType.builder(Supplier { InfusionData() })
                .serialize(InfusionData.CODEC)
                .copyOnDeath()
                .build()
        }
    )

    @JvmStatic
    val LIGHT_INFUSION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<LightInfusionData>> = ATTACHMENT_TYPES.register(
        "light_infusion_player_data",
        Supplier {
            AttachmentType.builder(Supplier { LightInfusionData() })
                .serialize(LightInfusionData.CODEC)
                .copyOnDeath()
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
                    .copyOnDeath()
                    .build()
            }
        )

    @JvmStatic
    val VOODOO_POPPET_DATA_ATTACHMENT: Supplier<AttachmentType<VoodooPoppetLivingEntityAttachment.VoodooPoppetData>> = ATTACHMENT_TYPES.register(
        "voodoo_poppet_data",
        Supplier {
            AttachmentType.builder(Supplier { VoodooPoppetLivingEntityAttachment.VoodooPoppetData(false) })
                .serialize(VoodooPoppetLivingEntityAttachment.VoodooPoppetData.CODEC)
                .build()
        }
    )

    @JvmStatic
    val POPPET_DATA_ATTACHMENT: Supplier<AttachmentType<PoppetLevelAttachment.PoppetData>> = ATTACHMENT_TYPES.register(
        "poppet_data",
        Supplier {
            AttachmentType.builder(Supplier { PoppetLevelAttachment.PoppetData(mutableListOf()) })
                .serialize(PoppetLevelAttachment.PoppetData.CODEC)
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
    val SLEEPING_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<SleepingLevelAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "sleeping_player",
            Supplier {
                AttachmentType.builder(Supplier { SleepingLevelAttachment.Data() })
                    .serialize(SleepingLevelAttachment.Data.CODEC)
                    .copyOnDeath()
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
    val NECRO_DATA_ATTACHMENT: Supplier<AttachmentType<NecromancerLevelAttachment.NecroList>> =
        ATTACHMENT_TYPES.register(
            "necro",
            Supplier {
                AttachmentType.builder(Supplier { NecromancerLevelAttachment.NecroList() })
                    .serialize(NecromancerLevelAttachment.NecroList.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val MISC_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<MiscPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "misc_player",
            Supplier {
                AttachmentType.builder(Supplier { MiscPlayerAttachment.Data() })
                    .serialize(MiscPlayerAttachment.Data.CODEC)
                    .copyOnDeath()
                    .build()
            }
        )

    @JvmStatic
    val MANIFESTATION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<ManifestationPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "manifestation_player",
            Supplier {
                AttachmentType.builder(Supplier { ManifestationPlayerAttachment.Data() })
                    .serialize(ManifestationPlayerAttachment.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<InfernalInfusionData>> = ATTACHMENT_TYPES.register(
        "infernal_infusion_player_data",
        Supplier {
            AttachmentType.builder(Supplier { InfernalInfusionData() })
                .serialize(InfernalInfusionData.CODEC)
                .copyOnDeath()
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

    @JvmStatic
    val CURSE_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<CursePlayerAttachment.Data>> = ATTACHMENT_TYPES.register(
        "curse_player_data",
        Supplier {
            AttachmentType.builder(Supplier { CursePlayerAttachment.Data() })
                .serialize(CursePlayerAttachment.Data.CODEC)
                .copyOnDeath()
                .build()
        }
    )

    @JvmStatic
    val NIGHTMARE_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<NightmarePlayerAttachment.Data>> = ATTACHMENT_TYPES.register(
        "nightmare_player_data",
        Supplier {
            AttachmentType.builder(Supplier { NightmarePlayerAttachment.Data() })
                .serialize(NightmarePlayerAttachment.Data.CODEC)
                .build()
        }
    )

    @JvmStatic
    val VAMPIRE_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<VampirePlayerAttachment.Data>> = ATTACHMENT_TYPES.register(
        "vampire_player_data",
        Supplier {
            AttachmentType.builder(Supplier { VampirePlayerAttachment.Data() })
                .serialize(VampirePlayerAttachment.Data.CODEC)
                .copyOnDeath()
                .build()
        }
    )

    @JvmStatic
    val WEREWOLF_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<WerewolfPlayerAttachment.Data>> = ATTACHMENT_TYPES.register(
        "werewolf_player_data",
        Supplier {
            AttachmentType.builder(Supplier { WerewolfPlayerAttachment.Data() })
                .serialize(WerewolfPlayerAttachment.Data.CODEC)
                .copyOnDeath()
                .build()
        }
    )

    @JvmStatic
    val COVEN_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<CovenPlayerAttachment.Data>> = ATTACHMENT_TYPES.register(
        "coven_player_data",
        Supplier {
            AttachmentType.builder(Supplier { CovenPlayerAttachment.Data() })
                .serialize(CovenPlayerAttachment.Data.CODEC)
                .copyOnDeath()
                .build()
        }
    )


    @JvmStatic
    val BLOOD_LIVING_ENTITY_DATA_ATTACHMENT: Supplier<AttachmentType<BloodPoolLivingEntityAttachment.Data>> = ATTACHMENT_TYPES.register(
        "blood_living_data",
        Supplier {
            AttachmentType.builder(Supplier { BloodPoolLivingEntityAttachment.Data() })
                .serialize(BloodPoolLivingEntityAttachment.Data.CODEC)
                .build()
        }
    )

    @JvmStatic
    val TRANSFORMATION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<TransformationPlayerAttachment.Data>> = ATTACHMENT_TYPES.register(
        "transformation_data",
        Supplier {
            AttachmentType.builder(Supplier { TransformationPlayerAttachment.Data() })
                .serialize(TransformationPlayerAttachment.Data.CODEC)
                .build()
        }
    )

    @JvmStatic
    val VAMPIRE_HUNT_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<VampireChildrenHuntLevelAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "vampire_hunt_level",
            Supplier {
                AttachmentType.builder(Supplier { VampireChildrenHuntLevelAttachment.Data() })
                    .serialize(VampireChildrenHuntLevelAttachment.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val BARK_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<BarkBeltPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "bark_player",
            Supplier {
                AttachmentType.builder(Supplier { BarkBeltPlayerAttachment.Data() })
                    .serialize(BarkBeltPlayerAttachment.Data.CODEC)
                    .build()
            }
        )
}