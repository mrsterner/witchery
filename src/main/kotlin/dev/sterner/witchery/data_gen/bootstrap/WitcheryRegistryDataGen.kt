package dev.sterner.witchery.data_gen.bootstrap

import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import java.util.concurrent.CompletableFuture

class WitcheryRegistryDataGen(
    output: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider>,
) : DatapackBuiltinEntriesProvider(
    output,
    registries,
    RegistrySetBuilder().apply {
        add(Registries.PLACED_FEATURE, WitcheryPlacedFeatureBootstrap::bootstrap)
        add(Registries.CONFIGURED_FEATURE, WitcheryConfiguredFeatureBootstrap::bootstrap)
    },
    mutableSetOf("witchery")
)
