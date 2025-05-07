package dev.sterner.witchery.registry

import com.google.common.base.Supplier
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.*
import dev.sterner.witchery.entity.sleeping_player.SleepingPlayerEntity
import dev.sterner.witchery.registry.WitcheryItems.ITEMS
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

object WitcheryEntityTypes {

    val ENTITY_TYPES: DeferredRegister<EntityType<*>> = DeferredRegister.create(Witchery.MODID, Registries.ENTITY_TYPE)

    val LANG_HELPER = mutableListOf<String>()

    fun <T : EntityType<*>> register(name: String, addLang: Boolean = true, item: Supplier<T>): RegistrySupplier<T> {
        if (addLang) {
            LANG_HELPER.add(name)
        }
        return ENTITY_TYPES.register(name, item)
    }
    
    val MANDRAKE: RegistrySupplier<EntityType<MandrakeEntity>> =register("mandrake") {
        EntityType.Builder.of(
            { _: EntityType<MandrakeEntity>, level: Level ->
                MandrakeEntity(level)
            }, MobCategory.CREATURE
        ).sized(0.5f, 0.5f).build(Witchery.id("mandrake").toString())
    }

    val IMP: RegistrySupplier<EntityType<ImpEntity>> =register("imp") {
        EntityType.Builder.of(
            { _: EntityType<ImpEntity>, level: Level ->
                ImpEntity(level)
            }, MobCategory.MONSTER
        ).sized(0.35F, 0.6F).eyeHeight(0.36F).build(Witchery.id("imp").toString())
    }


    val AREA_EFFECT_CLOUD: RegistrySupplier<EntityType<WitcheryAreaEffectCloud>> =
       register("area_effect_cloud") {
            EntityType.Builder.of(
                { _: EntityType<WitcheryAreaEffectCloud>, level: Level ->
                    WitcheryAreaEffectCloud(level)
                }, MobCategory.MISC
            ).sized(6F, 0.5F).clientTrackingRange(10).fireImmune().updateInterval(Int.Companion.MAX_VALUE)
                .build(Witchery.id("imp").toString())
        }

    val DEMON: RegistrySupplier<EntityType<DemonEntity>> =register("demon") {
        EntityType.Builder.of(
            { _: EntityType<DemonEntity>, level: Level ->
                DemonEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.25F, 3.2F).eyeHeight(2.6F).build(Witchery.id("demon").toString())
    }

    val ENT: RegistrySupplier<EntityType<EntEntity>> =register("ent") {
        EntityType.Builder.of(
            { _: EntityType<EntEntity>, level: Level ->
                EntEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.25F, 3.2F).eyeHeight(2.6F).build(Witchery.id("ent").toString())
    }

    val OWL: RegistrySupplier<EntityType<OwlEntity>> =register("owl") {
        EntityType.Builder.of(
            { _: EntityType<OwlEntity>, level: Level ->
                OwlEntity(level)
            }, MobCategory.CREATURE
        ).sized(0.35F, 0.6F).eyeHeight(0.36F).build(Witchery.id("owl").toString())
    }

    val BROOM: RegistrySupplier<EntityType<BroomEntity>> =register("broom") {
        EntityType.Builder.of(
            { _: EntityType<BroomEntity>, level: Level ->
                BroomEntity(level)
            }, MobCategory.MISC
        ).sized(1.0F, 0.6F).clientTrackingRange(10).build(Witchery.id("broom").toString())
    }

    val CHAIN: RegistrySupplier<EntityType<ChainEntity>> =register("chain") {
        EntityType.Builder.of(
            { _: EntityType<ChainEntity>, level: Level ->
                ChainEntity(level)
            }, MobCategory.MISC
        ).sized(0.5F, 0.5F).clientTrackingRange(10).build(Witchery.id("chain").toString())
    }

    val SLEEPING_PLAYER: RegistrySupplier<EntityType<SleepingPlayerEntity>> =register("sleeping_player") {
        EntityType.Builder.of(
            { _: EntityType<SleepingPlayerEntity>, level: Level ->
                SleepingPlayerEntity(level)
            }, MobCategory.MISC
        ).sized(1.25F, 0.6F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .build(Witchery.id("sleeping_player").toString())
    }

    val FLOATING_ITEM: RegistrySupplier<EntityType<FloatingItemEntity>> =
       register(
            "floating_item"
        ) {
            EntityType.Builder.of(
                { _: EntityType<FloatingItemEntity>, w: Level ->
                    FloatingItemEntity(
                        w
                    )
                }, MobCategory.MISC
            ).sized(0.5f, 0.75f).clientTrackingRange(10)
                .build(Witchery.id("floating_item").toString())
        }

    val CUSTOM_BOAT: RegistrySupplier<EntityType<CustomBoat>> =register("custom_boat") {
        EntityType.Builder.of(::CustomBoat, MobCategory.MISC)
            .sized(1.375f, 0.5625f).build("custom_boat")
    }

    val CUSTOM_CHEST_BOAT: RegistrySupplier<EntityType<CustomChestBoat>> =register("custom_chest_boat") {
        EntityType.Builder.of(::CustomChestBoat, MobCategory.MISC)
            .sized(1.375f, 0.5625f).build("custom_chest_boat")
    }

    val THROWN_BREW: RegistrySupplier<EntityType<ThrownBrewEntity>> =
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

    val THROWN_POTION: RegistrySupplier<EntityType<WitcheryThrownPotion>> =
       register(
            "thrown_potion"
        ) {
            EntityType.Builder.of(
                { _: EntityType<WitcheryThrownPotion>, w: Level ->
                    WitcheryThrownPotion(
                        w
                    )
                }, MobCategory.MISC
            ).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10)
                .build(Witchery.id("thrown_potion").toString())
        }

    val BANSHEE =register("banshee") {
        EntityType.Builder.of(
            { _: EntityType<BansheeEntity>, level: Level ->
                BansheeEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.15F, 1.8F).build(Witchery.id("banshee").toString())
    }

    val SPECTRE =register("spectre") {
        EntityType.Builder.of(
            { _: EntityType<SpectreEntity>, level: Level ->
                SpectreEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.15F, 1.8F).build(Witchery.id("spectre").toString())
    }

    var COVEN_WITCH =register("coven_witch") {
        EntityType.Builder.of(
            { _: EntityType<CovenWitchEntity>, level: Level ->
                CovenWitchEntity(level)
            }, MobCategory.MISC
        ).sized(1.15F, 1.8F).build(Witchery.id("coven_witch").toString())
    }

    val SPECTRAL_PIG: RegistrySupplier<EntityType<SpectralPigEntity>> =register("spectral_pig") {
        EntityType.Builder.of(
            { _: EntityType<SpectralPigEntity>, level: Level ->
                SpectralPigEntity(level)
            }, MobCategory.CREATURE
        ).sized(0.9f, 0.9f).build(Witchery.id("spectral_pig").toString())
    }

    val NIGHTMARE: RegistrySupplier<EntityType<NightmareEntity>> =register("nightmare") {
        EntityType.Builder.of(
            { _: EntityType<NightmareEntity>, level: Level ->
                NightmareEntity(level)
            }, MobCategory.MONSTER
        ).sized(0.85F, 2.2F).build(Witchery.id("nightmare").toString())
    }

    val VAMPIRE: RegistrySupplier<EntityType<VampireEntity>> =register("vampire") {
        EntityType.Builder.of(
            { _: EntityType<VampireEntity>, level: Level ->
                VampireEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.15F, 1.8F).build(Witchery.id("vampire").toString())
    }

    val WEREWOLF: RegistrySupplier<EntityType<WerewolfEntity>> =register("werewolf") {
        EntityType.Builder.of(
            { _: EntityType<WerewolfEntity>, level: Level ->
                WerewolfEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.15F, 1.8F).build(Witchery.id("werewolf").toString())
    }

    val LILITH: RegistrySupplier<EntityType<LilithEntity>> =register("lilith") {
        EntityType.Builder.of(
            { _: EntityType<LilithEntity>, level: Level ->
                LilithEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.15F, 2.6F).build(Witchery.id("lilith").toString())
    }

    val ELLE: RegistrySupplier<EntityType<ElleEntity>> =register("elle") {
        EntityType.Builder.of(
            { _: EntityType<ElleEntity>, level: Level ->
                ElleEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.0F, 1.85F).build(Witchery.id("elle").toString())
    }

    val PARASITIC_LOUSE: RegistrySupplier<EntityType<ParasiticLouseEntity>> =register("parasitic_louse") {
        EntityType.Builder.of(
            { _: EntityType<ParasiticLouseEntity>, level: Level ->
                ParasiticLouseEntity(level)
            }, MobCategory.MONSTER
        ).sized(0.35F, 0.3F).eyeHeight(0.36F).build(Witchery.id("parasitic_louse").toString())
    }
}