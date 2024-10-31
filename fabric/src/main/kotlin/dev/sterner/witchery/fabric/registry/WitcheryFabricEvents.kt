package dev.sterner.witchery.fabric.registry

import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.fabric.api.loot.v3.LootTableSource
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator

object WitcheryFabricEvents {

    fun addEntityDrops(resourceKey: ResourceKey<LootTable>?, builder: LootTable.Builder, lootTableSource: LootTableSource, provider: HolderLookup.Provider) {
        if (lootTableSource.isBuiltin && EntityType.WOLF.defaultLootTable.equals(resourceKey)) {
            val pool = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.TONGUE_OF_DOG.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                )
                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0.0F, 1.0F)))
                .build()
            builder.pool(pool)
        }

        if (lootTableSource.isBuiltin && EntityType.FROG.defaultLootTable.equals(resourceKey)) {
            val pool = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.TOE_OF_FROG.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                )
                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0.0F, 1.0F)))
                .build()
            builder.pool(pool)
        }

        if (lootTableSource.isBuiltin && EntityType.BAT.defaultLootTable.equals(resourceKey)) {
            val pool = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.WOOL_OF_BAT.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                )
                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0.0F, 1.0F)))
                .build()
            builder.pool(pool)
        }
    }
}