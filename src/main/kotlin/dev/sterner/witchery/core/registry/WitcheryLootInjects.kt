package dev.sterner.witchery.core.registry

import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition
import net.neoforged.neoforge.event.LootTableLoadEvent

object WitcheryLootInjects {

    fun onLootTableLoad(event: LootTableLoadEvent) {
        val name = event.name

        if (name == EntityType.WITCH.defaultLootTable) {
            val pool = LootPool.lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.WITCHES_HAND.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.5f))
                )
                .build()
            event.table.addPool(pool)
        }

        if (name == EntityType.WOLF.defaultLootTable) {
            val pool = LootPool.lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.TONGUE_OF_DOG.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                )
                .build()
            event.table.addPool(pool)
        }

        if (name == EntityType.FROG.defaultLootTable) {
            val pool = LootPool.lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.TOE_OF_FROG.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                )
                .build()
            event.table.addPool(pool)
        }

        if (name == EntityType.BAT.defaultLootTable) {
            val pool = LootPool.lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.WOOL_OF_BAT.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                )
                .build()
            event.table.addPool(pool)
        }

        if (name == Blocks.SHORT_GRASS.lootTable || name == Blocks.TALL_GRASS.lootTable) {
            val belladonnaPool = LootPool.lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.BELLADONNA_SEEDS.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.05f))
                )
                .build()
            event.table.addPool(belladonnaPool)

            val waterArtichokePool = LootPool.lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.WATER_ARTICHOKE_SEEDS.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.05f))
                )
                .build()
            event.table.addPool(waterArtichokePool)

            val mandrakePool = LootPool.lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.MANDRAKE_SEEDS.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.05f))
                )
                .build()
            event.table.addPool(mandrakePool)

            val snowbellPool = LootPool.lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.SNOWBELL_SEEDS.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.05f))
                )
                .build()
            event.table.addPool(snowbellPool)

            val wolfsbane = LootPool.lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.WOLFSBANE_SEEDS.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.03f))
                )
                .build()
            event.table.addPool(wolfsbane)
        }
    }
}