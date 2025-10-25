package dev.sterner.witchery.core.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.entity.*
import dev.sterner.witchery.content.entity.player_shell.SleepingPlayerEntity
import dev.sterner.witchery.content.entity.player_shell.SoulShellPlayerEntity
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.Level
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier


object WitcheryEntityTypes {

    val ENTITY_TYPES: DeferredRegister<EntityType<*>> = DeferredRegister.create(Registries.ENTITY_TYPE, Witchery.MODID)

    val LANG_HELPER = mutableListOf<String>()

    fun <T : EntityType<*>> register(name: String, item: Supplier<T>): DeferredHolder<EntityType<*>, T> {
        if (true) {
            LANG_HELPER.add(name)
        }
        return ENTITY_TYPES.register(name, item)
    }

    val MANDRAKE = register("mandrake", Supplier {
        EntityType.Builder.of(
            { _: EntityType<MandrakeEntity>, level: Level ->
                MandrakeEntity(level)
            }, MobCategory.CREATURE
        ).sized(0.5f, 0.5f).build(Witchery.id("mandrake").toString())
    })

    val IMP = register("imp", Supplier {
        EntityType.Builder.of(
            { _: EntityType<ImpEntity>, level: Level ->
                ImpEntity(level)
            }, MobCategory.MONSTER
        ).sized(0.35F, 0.6F).eyeHeight(0.36F).build(Witchery.id("imp").toString())
    })

    val INSANITY = register("insanity", Supplier {
        EntityType.Builder.of(
            { _: EntityType<InsanityEntity>, level: Level ->
                InsanityEntity(level)
            }, MobCategory.MONSTER
        ).sized(0.6f, 1.8f).build(Witchery.id("insanity").toString())
    })

    val AREA_EFFECT_CLOUD =
        register("area_effect_cloud") {
            EntityType.Builder.of(
                { _: EntityType<WitcheryAreaEffectCloud>, level: Level ->
                    WitcheryAreaEffectCloud(level)
                }, MobCategory.MISC
            ).sized(6F, 0.5F).clientTrackingRange(10).fireImmune().updateInterval(Int.Companion.MAX_VALUE)
                .build(Witchery.id("imp").toString())
        }

    val DEMON = register("demon", Supplier {
        EntityType.Builder.of(
            { _: EntityType<DemonEntity>, level: Level ->
                DemonEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.25F, 3.2F).eyeHeight(2.6F).build(Witchery.id("demon").toString())
    })

    val ENT = register("ent") {
        EntityType.Builder.of(
            { _: EntityType<EntEntity>, level: Level ->
                EntEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.25F, 3.2F).eyeHeight(2.6F).build(Witchery.id("ent").toString())
    }

    val OWL = register("owl", Supplier {
        EntityType.Builder.of(
            { _: EntityType<OwlEntity>, level: Level ->
                OwlEntity(level)
            }, MobCategory.CREATURE
        ).sized(0.35F, 0.6F).eyeHeight(0.36F).build(Witchery.id("owl").toString())
    })

    val BROOM = register("broom") {
        EntityType.Builder.of(
            { _: EntityType<BroomEntity>, level: Level ->
                BroomEntity(level)
            }, MobCategory.MISC
        ).sized(1.0F, 0.6F).clientTrackingRange(10).build(Witchery.id("broom").toString())
    }

    val SCYTHE_THROWN = register("scythe_thrown") {
        EntityType.Builder.of(
            { _: EntityType<ScytheThrownEntity>, level: Level ->
                ScytheThrownEntity(level)
            }, MobCategory.MISC
        ).sized(1.0F, 0.6F).clientTrackingRange(10).build(Witchery.id("scythe_thrown").toString())
    }

    val CHAIN = register("chain", Supplier {
        EntityType.Builder.of(
            { _: EntityType<ChainEntity>, level: Level ->
                ChainEntity(level)
            }, MobCategory.MISC
        ).sized(0.5F, 0.5F).clientTrackingRange(10).build(Witchery.id("chain").toString())
    })

    val SLEEPING_PLAYER = register("sleeping_player") {
        EntityType.Builder.of(
            { _: EntityType<SleepingPlayerEntity>, level: Level ->
                SleepingPlayerEntity(level)
            }, MobCategory.MISC
        ).sized(1.25F, 0.6F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .build(Witchery.id("sleeping_player").toString())
    }

    val SOUL_SHELL_PLAYER = register("soul_shell_player") {
        EntityType.Builder.of(
            { _: EntityType<SoulShellPlayerEntity>, level: Level ->
                SoulShellPlayerEntity(level)
            }, MobCategory.MISC
        ).sized(1.05F, 1.85F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .build(Witchery.id("soul_shell_player").toString())
    }

    val FLOATING_ITEM =
        register(
            "floating_item", Supplier {
                EntityType.Builder.of(
                    { _: EntityType<FloatingItemEntity>, w: Level ->
                        FloatingItemEntity(
                            w
                        )
                    }, MobCategory.MISC
                ).sized(0.5f, 0.75f).clientTrackingRange(10)
                    .build(Witchery.id("floating_item").toString())
            })

    val CUSTOM_BOAT = register("custom_boat") {
        EntityType.Builder.of(::CustomBoat, MobCategory.MISC)
            .sized(1.375f, 0.5625f).build("custom_boat")
    }

    val CUSTOM_CHEST_BOAT = register("custom_chest_boat", Supplier {
        EntityType.Builder.of(::CustomChestBoat, MobCategory.MISC)
            .sized(1.375f, 0.5625f).build("custom_chest_boat")
    })

    val THROWN_BREW =
        register(
            "thrown_brew"
        ) {
            EntityType.Builder.of(
                { _: EntityType<ThrownBrewEntity>, w: Level ->
                    ThrownBrewEntity(
                        w
                    )
                }, MobCategory.MISC
            ).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10)
                .build(Witchery.id("thrown_brew").toString())
        }

    val THROWN_POTION =
        register(
            "thrown_potion", Supplier {
                EntityType.Builder.of(
                    { _: EntityType<WitcheryThrownPotion>, w: Level ->
                        WitcheryThrownPotion(
                            w
                        )
                    }, MobCategory.MISC
                ).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10)
                    .build(Witchery.id("thrown_potion").toString())
            })

    val BANSHEE = register("banshee") {
        EntityType.Builder.of(
            { _: EntityType<BansheeEntity>, level: Level ->
                BansheeEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.15F, 1.8F).build(Witchery.id("banshee").toString())
    }

    val SPECTRE = register("spectre", Supplier {
        EntityType.Builder.of(
            { _: EntityType<SpectreEntity>, level: Level ->
                SpectreEntity(level)
            }, MobCategory.MONSTER
        ).sized(0.6f, 1.8f).build(Witchery.id("spectre").toString())
    })

    var COVEN_WITCH = register("coven_witch") {
        EntityType.Builder.of(
            { _: EntityType<CovenWitchEntity>, level: Level ->
                CovenWitchEntity(level)
            }, MobCategory.MISC
        ).sized(0.6f, 1.8f).build(Witchery.id("coven_witch").toString())
    }

    var DEATH = register("death", Supplier {
        EntityType.Builder.of(
            { _: EntityType<DeathEntity>, level: Level ->
                DeathEntity(level)
            }, MobCategory.MONSTER
        ).sized(0.6f, 1.8f).build(Witchery.id("death").toString())
    })

    var HORNED_HUNTSMAN = register("horned_huntsman") {
        EntityType.Builder.of(
            { _: EntityType<HornedHuntsmanEntity>, level: Level ->
                HornedHuntsmanEntity(level)
            }, MobCategory.MONSTER
        ).sized(0.6f, 2.8f).build(Witchery.id("horned_huntsman").toString())
    }

    var HUNTSMAN_SPEAR = register("huntsman_spear", Supplier {
        EntityType.Builder.of(
            { _: EntityType<HuntsmanSpearEntity>, level: Level ->
                HuntsmanSpearEntity(level)
            }, MobCategory.MISC
        ).sized(0.5f, 0.5f)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build(Witchery.id("huntsman_spear").toString())
    })

    val SPECTRAL_PIG = register("spectral_pig") {
        EntityType.Builder.of(
            { _: EntityType<SpectralPigEntity>, level: Level ->
                SpectralPigEntity(level)
            }, MobCategory.CREATURE
        ).sized(0.9f, 0.9f).build(Witchery.id("spectral_pig").toString())
    }

    val NIGHTMARE = register("nightmare", Supplier {
        EntityType.Builder.of(
            { _: EntityType<NightmareEntity>, level: Level ->
                NightmareEntity(level)
            }, MobCategory.MONSTER
        ).sized(0.85F, 2.2F).build(Witchery.id("nightmare").toString())
    })

    val VAMPIRE = register("vampire") {
        EntityType.Builder.of(
            { _: EntityType<VampireEntity>, level: Level ->
                VampireEntity(level)
            }, MobCategory.MONSTER
        ).sized(0.6f, 1.8f).build(Witchery.id("vampire").toString())
    }

    val BABA_YAGA = register("baba_yaga", Supplier {
        EntityType.Builder.of(
            { _: EntityType<BabaYagaEntity>, level: Level ->
                BabaYagaEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.0F, 1.8F).build(Witchery.id("baba_yaga").toString())
    })

    val WEREWOLF = register("werewolf") {
        EntityType.Builder.of(
            { _: EntityType<WerewolfEntity>, level: Level ->
                WerewolfEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.15F, 1.8F).build(Witchery.id("werewolf").toString())
    }

    val LILITH = register("lilith", Supplier {
        EntityType.Builder.of(
            { _: EntityType<LilithEntity>, level: Level ->
                LilithEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.15F, 2.6F).build(Witchery.id("lilith").toString())
    })

    val ELLE = register("elle") {
        EntityType.Builder.of(
            { _: EntityType<ElleEntity>, level: Level ->
                ElleEntity(level)
            }, MobCategory.MONSTER
        ).sized(0.6f, 1.8f).build(Witchery.id("elle").toString())
    }

    val PARASITIC_LOUSE = register("parasitic_louse", Supplier {
        EntityType.Builder.of(
            { _: EntityType<ParasiticLouseEntity>, level: Level ->
                ParasiticLouseEntity(level)
            }, MobCategory.MONSTER
        ).sized(0.35F, 0.3F).eyeHeight(0.36F).build(Witchery.id("parasitic_louse").toString())
    })
}