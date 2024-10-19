package dev.sterner.witchery.neoforge.event

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.LootTableLoadEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks

object WitcheryNeoForgeEvents {

    @SubscribeEvent
    fun modifyLootTable(event: LootTableLoadEvent){
        val registries: HolderLookup.Provider = if (ServerLifecycleHooks.getCurrentServer() != null) ServerLifecycleHooks.getCurrentServer()!!.registryAccess() else RegistryAccess.EMPTY

        if (event.name.equals(EntityType.BAT.defaultLootTable)) {
            val pool = LootPool.lootPool().add(
                LootItem.lootTableItem(WitcheryItems.WOOL_OF_BAT.get())
                    .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                    .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F))))
                .name("witchery_inject").build()

            event.table.addPool(pool)
        }

        if (event.name.equals(EntityType.WOLF.defaultLootTable)) {
            val pool = LootPool.lootPool().add(
                LootItem.lootTableItem(WitcheryItems.TONGUE_OF_DOG.get())
                    .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                    .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F))))
                .name("witchery_inject").build()

            event.table.addPool(pool)
        }

        if (event.name.equals(EntityType.FROG.defaultLootTable)) {
            val pool = LootPool.lootPool().add(
                LootItem.lootTableItem(WitcheryItems.TOE_OF_FROG.get())
                    .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                    .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F))))
                .name("witchery_inject").build()

            event.table.addPool(pool)
        }
    }
}