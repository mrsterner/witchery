package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class WitcheryEntityLootProvider(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<HolderLookup.Provider>,
) : SimpleFabricLootTableProvider(output, registryLookup, LootContextParamSets.ENTITY) {

    override fun generate(output: BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) {

        output.accept(
            OWL, LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(WitcheryItems.OWLETS_WING.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                        )
                )
        )

        output.accept(
            DEMON, LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(WitcheryItems.DEMON_HEART.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                        )
                )
        )

        output.accept(
            ENT, LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(WitcheryItems.ENT_TWIG.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                        )
                )
        )
    }

    companion object {
        val OWL: ResourceKey<LootTable> =
            ResourceKey.create(Registries.LOOT_TABLE, Witchery.id("entities/owl"))
        val DEMON: ResourceKey<LootTable> =
            ResourceKey.create(Registries.LOOT_TABLE, Witchery.id("entities/demon"))
        val ENT: ResourceKey<LootTable> =
            ResourceKey.create(Registries.LOOT_TABLE, Witchery.id("entities/ent"))

    }
}