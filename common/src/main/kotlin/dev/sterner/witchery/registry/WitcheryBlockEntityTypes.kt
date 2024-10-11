package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.block.altar.AltarBlockEntity
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType

object WitcheryBlockEntityTypes {

    val BLOCK_ENTITY_TYPES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Witchery.MODID, Registries.BLOCK_ENTITY_TYPE)

    val MULTI_BLOCK_COMPONENT: RegistrySupplier<BlockEntityType<MultiBlockComponentBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("multi_block_component") {
            BlockEntityType.Builder.of(
                { pos, state -> MultiBlockComponentBlockEntity(pos, state) },
                WitcheryBlocks.COMPONENT.get(),
                WitcheryBlocks.ALTAR_COMPONENT.get()
            )
                .build(null)
        }

    val ALTAR: RegistrySupplier<BlockEntityType<AltarBlockEntity>> = BLOCK_ENTITY_TYPES.register("altar") {
        BlockEntityType.Builder.of(
            { pos, state -> AltarBlockEntity(pos, state) },
            WitcheryBlocks.ALTAR.get(),
        )
            .build(null)
    }
}