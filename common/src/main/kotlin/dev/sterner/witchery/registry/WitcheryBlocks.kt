package dev.sterner.witchery.registry

import dev.architectury.core.block.ArchitecturyLiquidBlock
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockBehaviour


object WitcheryBlocks {

    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(Witchery.MODID, Registries.BLOCK)


}