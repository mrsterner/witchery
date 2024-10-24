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
        ).sized(1.0F, 0.6F).clientTrackingRange(10) .build(Witchery.id("broom").toString())
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
}