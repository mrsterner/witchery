package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType

object WitcheryBlockEntityTypes {

    val BLOCK_ENTITY_TYPES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Witchery.MODID, Registries.BLOCK_ENTITY_TYPE)
}