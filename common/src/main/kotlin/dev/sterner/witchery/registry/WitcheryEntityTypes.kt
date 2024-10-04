package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType

object WitcheryEntityTypes {

    val ENTITY_TYPES: DeferredRegister<EntityType<*>> = DeferredRegister.create(Witchery.MODID, Registries.ENTITY_TYPE)


}