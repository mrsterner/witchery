package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlock
import dev.sterner.witchery.block.altar.AltarBlock
import dev.sterner.witchery.block.altar.AltarBlockComponent
import dev.sterner.witchery.block.cauldron.CauldronBlock
import dev.sterner.witchery.block.cauldron.CauldronBlockComponent
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour


object WitcheryBlocks {

    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(Witchery.MODID, Registries.BLOCK)

    val COMPONENT: RegistrySupplier<MultiBlockComponentBlock> = BLOCKS.register("component") {
        MultiBlockComponentBlock(BlockBehaviour.Properties.of())
    }

    val ALTAR: RegistrySupplier<AltarBlock> = BLOCKS.register("altar") {
        AltarBlock(BlockBehaviour.Properties.of())
    }

    val ALTAR_COMPONENT: RegistrySupplier<AltarBlockComponent> = BLOCKS.register("altar_component") {
        AltarBlockComponent(BlockBehaviour.Properties.of())
    }

    val CAULDRON: RegistrySupplier<CauldronBlock> = BLOCKS.register("cauldron") {
        CauldronBlock(BlockBehaviour.Properties.of())
    }

    val CAULDRON_COMPONENT: RegistrySupplier<CauldronBlockComponent> = BLOCKS.register("cauldron_component") {
        CauldronBlockComponent(BlockBehaviour.Properties.of())
    }
}