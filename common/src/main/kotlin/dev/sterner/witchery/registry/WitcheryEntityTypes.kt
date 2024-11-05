package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.*
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.Level

object WitcheryEntityTypes {

    val ENTITY_TYPES: DeferredRegister<EntityType<*>> = DeferredRegister.create(Witchery.MODID, Registries.ENTITY_TYPE)

    val MANDRAKE = ENTITY_TYPES.register("mandrake") {
        EntityType.Builder.of(
            { _: EntityType<MandrakeEntity>, level: Level ->
                MandrakeEntity(level)
            }, MobCategory.CREATURE
        ).sized(0.5f, 0.5f).build(Witchery.id("mandrake").toString())
    }

    val IMP = ENTITY_TYPES.register("imp") {
        EntityType.Builder.of(
            { _: EntityType<ImpEntity>, level: Level ->
                ImpEntity(level)
            }, MobCategory.MONSTER
        ).sized(0.35F, 0.6F).eyeHeight(0.36F).build(Witchery.id("imp").toString())
    }

    val DEMON = ENTITY_TYPES.register("demon") {
        EntityType.Builder.of(
            { _: EntityType<DemonEntity>, level: Level ->
                DemonEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.25F, 3.2F).eyeHeight(2.6F).build(Witchery.id("demon").toString())
    }

    val ENT = ENTITY_TYPES.register("ent") {
        EntityType.Builder.of(
            { _: EntityType<EntEntity>, level: Level ->
                EntEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.25F, 3.2F).eyeHeight(2.6F).build(Witchery.id("ent").toString())
    }


    val OWL = ENTITY_TYPES.register("owl") {
        EntityType.Builder.of(
            { _: EntityType<OwlEntity>, level: Level ->
                OwlEntity(level)
            }, MobCategory.CREATURE
        ).sized(0.35F, 0.6F).eyeHeight(0.36F).build(Witchery.id("owl").toString())
    }

    val BROOM = ENTITY_TYPES.register("broom") {
        EntityType.Builder.of(
            { _: EntityType<BroomEntity>, level: Level ->
                BroomEntity(level)
            }, MobCategory.MISC
        ).sized(1.0F, 0.6F).clientTrackingRange(10).build(Witchery.id("broom").toString())
    }

    val SLEEPING_PLAYER = ENTITY_TYPES.register("sleeping_player") {
        EntityType.Builder.of(
            { _: EntityType<SleepingPlayerEntity>, level: Level ->
                SleepingPlayerEntity(level)
            }, MobCategory.MISC
        ).sized(1.25F, 0.6F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .build(Witchery.id("sleeping_player").toString())
    }

    val FLOATING_ITEM =
        ENTITY_TYPES.register(
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

    val CUSTOM_BOAT = ENTITY_TYPES.register("custom_boat") {
        EntityType.Builder.of(::CustomBoat, MobCategory.MISC)
            .sized(1.375f, 0.5625f).build("custom_boat")
    }

    val CUSTOM_CHEST_BOAT = ENTITY_TYPES.register("custom_chest_boat") {
        EntityType.Builder.of(::CustomChestBoat, MobCategory.MISC)
            .sized(1.375f, 0.5625f).build("custom_chest_boat")
    }

    val THROWN_BREW =
        ENTITY_TYPES.register(
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

    val BANSHEE = ENTITY_TYPES.register("banshee") {
        EntityType.Builder.of(
            { _: EntityType<BansheeEntity>, level: Level ->
                BansheeEntity(level)
            }, MobCategory.MONSTER
        ).sized(1.15F, 1.8F).build(Witchery.id("banshee").toString())
    }
}