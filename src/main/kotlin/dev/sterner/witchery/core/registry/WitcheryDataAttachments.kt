package dev.sterner.witchery.core.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.data_attachment.*
import dev.sterner.witchery.core.data_attachment.teleport.TeleportQueueLevelAttachment
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.affliction.TransformationPlayerAttachment
import dev.sterner.witchery.features.affliction.vampire.VampireChildrenHuntLevelAttachment
import dev.sterner.witchery.features.coven.CovenPlayerAttachment
import dev.sterner.witchery.features.curse.CursePlayerAttachment
import dev.sterner.witchery.features.infusion.InfernalInfusionPlayerAttachment
import dev.sterner.witchery.features.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.features.infusion.LightInfusionPlayerAttachment
import dev.sterner.witchery.features.infusion.OtherwhereInfusionPlayerAttachment
import dev.sterner.witchery.features.nightmare.NightmarePlayerAttachment
import dev.sterner.witchery.features.poppet.CorruptPoppetPlayerAttachment
import dev.sterner.witchery.features.poppet.PoppetLevelAttachment
import dev.sterner.witchery.features.poppet.VoodooPoppetLivingEntityAttachment
import dev.sterner.witchery.features.possession.EntityAiToggle
import dev.sterner.witchery.features.possession.PossessedDataAttachment
import dev.sterner.witchery.features.possession.PossessionComponentAttachment
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.function.Supplier

object WitcheryDataAttachments {
    val ATTACHMENT_TYPES: DeferredRegister<AttachmentType<*>> =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Witchery.MODID)


    @JvmStatic
    val BINDING_CURSE: Supplier<AttachmentType<BindingCurseAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "binding_data",
            Supplier {
                AttachmentType.builder(Supplier { BindingCurseAttachment.Data() })
                    .serialize(BindingCurseAttachment.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val POSSESSED_DATA: Supplier<AttachmentType<PossessedDataAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "possessed_data",
            Supplier {
                AttachmentType.builder(Supplier { PossessedDataAttachment.Data() })
                    .serialize(PossessedDataAttachment.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val PLAYER_POSSESSION: Supplier<AttachmentType<PossessionComponentAttachment.PossessionData>> =
        ATTACHMENT_TYPES.register(
            "player_possession",
            Supplier {
                AttachmentType.builder(Supplier { PossessionComponentAttachment.PossessionData() })
                    .serialize(PossessionComponentAttachment.PossessionData.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val MUTANDIS_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<MutandisLevelAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "mutandis_level_data",
            Supplier {
                AttachmentType.builder(Supplier { MutandisLevelAttachment.Data() })
                    .serialize(MutandisLevelAttachment.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val ALTAR_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<AltarLevelAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "altar_level_data",
            Supplier {
                AttachmentType.builder(Supplier { AltarLevelAttachment.Data() })
                    .serialize(AltarLevelAttachment.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val PHYLACTERY_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<PhylacteryLevelDataAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "phylactery_level_data",
            Supplier {
                AttachmentType.builder(Supplier { PhylacteryLevelDataAttachment.Data() })
                    .serialize(PhylacteryLevelDataAttachment.Data.CODEC)
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
    val INFUSION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<InfusionPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "infusion_player_data",
            Supplier {
                AttachmentType.builder(Supplier { InfusionPlayerAttachment.Data() })
                    .serialize(InfusionPlayerAttachment.Data.CODEC)
                    .copyOnDeath()
                    .build()
            }
        )

    @JvmStatic
    val ARCANA_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<TarotPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "arcana_player_data",
            Supplier {
                AttachmentType.builder(Supplier { TarotPlayerAttachment.Data() })
                    .serialize(TarotPlayerAttachment.Data.CODEC)
                    .copyOnDeath()
                    .build()
            }
        )

    @JvmStatic
    val UNDER_WATER_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<UnderWaterBreathPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "underwater_player_data",
            Supplier {
                AttachmentType.builder(Supplier { UnderWaterBreathPlayerAttachment.Data() })
                    .serialize(UnderWaterBreathPlayerAttachment.Data.CODEC)
                    .copyOnDeath()
                    .build()
            }
        )


    @JvmStatic
    val LIGHT_INFUSION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<LightInfusionPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "light_infusion_player_data",
            Supplier {
                AttachmentType.builder(Supplier { LightInfusionPlayerAttachment.Data() })
                    .serialize(LightInfusionPlayerAttachment.Data.CODEC)
                    .copyOnDeath()
                    .build()
            }
        )

    @JvmStatic
    val OTHERWHERE_INFUSION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<OtherwhereInfusionPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "otherwhere_infusion_player_data",
            Supplier {
                AttachmentType.builder(Supplier { OtherwhereInfusionPlayerAttachment.Data() })
                    .serialize(OtherwhereInfusionPlayerAttachment.Data.CODEC)
                    .copyOnDeath()
                    .build()
            }
        )

    @JvmStatic
    val VOODOO_POPPET_DATA_ATTACHMENT: Supplier<AttachmentType<VoodooPoppetLivingEntityAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "voodoo_poppet_data",
            Supplier {
                AttachmentType.builder(Supplier { VoodooPoppetLivingEntityAttachment.Data(false) })
                    .serialize(VoodooPoppetLivingEntityAttachment.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val CORRUPT_POPPET_DATA_ATTACHMENT: Supplier<AttachmentType<CorruptPoppetPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "corrupt_poppet_data",
            Supplier {
                AttachmentType.builder(Supplier { CorruptPoppetPlayerAttachment.Data() })
                    .serialize(CorruptPoppetPlayerAttachment.Data.CODEC)
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
    val NECRO_DATA_ATTACHMENT: Supplier<AttachmentType<NecromancerLevelAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "necro",
            Supplier {
                AttachmentType.builder(Supplier { NecromancerLevelAttachment.Data() })
                    .serialize(NecromancerLevelAttachment.Data.CODEC)
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
    val INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<InfernalInfusionPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "infernal_infusion_player_data",
            Supplier {
                AttachmentType.builder(Supplier { InfernalInfusionPlayerAttachment.Data() })
                    .serialize(InfernalInfusionPlayerAttachment.Data.CODEC)
                    .copyOnDeath()
                    .build()
            }
        )

    @JvmStatic
    val DEATH_QUEUE_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<DeathQueueLevelAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "death_queue_level_data",
            Supplier {
                AttachmentType.builder(Supplier { DeathQueueLevelAttachment.Data() })
                    .serialize(DeathQueueLevelAttachment.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val FAMILIAR_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<FamiliarLevelAttachment.Data>> =
        ATTACHMENT_TYPES.register(
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
    val NIGHTMARE_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<NightmarePlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "nightmare_player_data",
            Supplier {
                AttachmentType.builder(Supplier { NightmarePlayerAttachment.Data() })
                    .serialize(NightmarePlayerAttachment.Data.CODEC)
                    .build()
            }
        )


    @JvmStatic
    val AFFLICTION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<AfflictionPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "affliction_player_data",
            Supplier {
                AttachmentType.builder(Supplier { AfflictionPlayerAttachment.Data() })
                    .serialize(AfflictionPlayerAttachment.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val COVEN_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<CovenPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "coven_player_data",
            Supplier {
                AttachmentType.builder(Supplier { CovenPlayerAttachment.Data() })
                    .serialize(CovenPlayerAttachment.Data.CODEC)
                    .copyOnDeath()
                    .build()
            }
        )


    @JvmStatic
    val BLOOD_LIVING_ENTITY_DATA_ATTACHMENT: Supplier<AttachmentType<BloodPoolLivingEntityAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "blood_living_data",
            Supplier {
                AttachmentType.builder(Supplier { BloodPoolLivingEntityAttachment.Data() })
                    .serialize(BloodPoolLivingEntityAttachment.Data.CODEC)
                    .build()
            }
        )



    @JvmStatic
    val ENTITY_TOGGLE_DATA_ATTACHMENT: Supplier<AttachmentType<EntityAiToggle.Data>> =
        ATTACHMENT_TYPES.register(
            "ai_toggle",
            Supplier {
                AttachmentType.builder(Supplier { EntityAiToggle.Data() })
                    .serialize(EntityAiToggle.Data.CODEC)
                    .build()
            }
        )

    @JvmStatic
    val SOUL_POOL_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<SoulPoolPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "soul_living_data",
            Supplier {
                AttachmentType.builder(Supplier { SoulPoolPlayerAttachment.Data() })
                    .serialize(SoulPoolPlayerAttachment.Data.CODEC)
                    .copyOnDeath()
                    .build()
            }
        )

    @JvmStatic
    val TRANSFORMATION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<TransformationPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
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

    @JvmStatic
    val INVENTORY_KEEPER_DATA_ATTACHMENT: Supplier<AttachmentType<InventoryLockPlayerAttachment.Data>> =
        ATTACHMENT_TYPES.register(
            "inverntory_keeper",
            Supplier {
                AttachmentType.builder(Supplier { InventoryLockPlayerAttachment.Data() })
                    .serialize(InventoryLockPlayerAttachment.Data.CODEC)
                    .build()
            }
        )
}