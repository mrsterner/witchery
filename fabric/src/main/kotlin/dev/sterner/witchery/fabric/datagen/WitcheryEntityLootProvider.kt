package dev.sterner.witchery.fabric.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class WitcheryEntityLootProvider(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<HolderLookup.Provider>,
) : SimpleFabricLootTableProvider(output, registryLookup, LootContextParamSets.ENTITY) {

    override fun generate(output: BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) {

    }
}