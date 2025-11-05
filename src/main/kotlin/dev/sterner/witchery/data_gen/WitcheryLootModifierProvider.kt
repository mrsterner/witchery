package dev.sterner.witchery.data_gen

import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.registry.WitcheryLootInjects.AddItemModifier
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider
import net.neoforged.neoforge.common.loot.IGlobalLootModifier
import net.neoforged.neoforge.common.loot.LootTableIdCondition
import java.util.concurrent.CompletableFuture


class WitcheryLootModifierProvider(output: PackOutput,
                                   registries: CompletableFuture<HolderLookup.Provider>
) : GlobalLootModifierProvider(output, registries, "witchery") {

    override fun start() {

        this.add<IGlobalLootModifier?>(
            "belladonna_seeds_to_short_grass",
            AddItemModifier(
                arrayOf(
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SHORT_GRASS).build(),
                    LootItemRandomChanceCondition.randomChance(0.05f).build()
                ), WitcheryItems.BELLADONNA_SEEDS.get()
            )
        )

        this.add<IGlobalLootModifier?>(
            "belladonna_seeds_to_tall_grass",
            AddItemModifier(
                arrayOf(
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TALL_GRASS).build(),
                    LootItemRandomChanceCondition.randomChance(0.05f).build()
                ), WitcheryItems.BELLADONNA_SEEDS.get()
            )
        )

        this.add<IGlobalLootModifier?>(
            "mandrake_seeds_to_short_grass",
            AddItemModifier(
                arrayOf(
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SHORT_GRASS).build(),
                    LootItemRandomChanceCondition.randomChance(0.05f).build()
                ), WitcheryItems.MANDRAKE_SEEDS.get()
            )
        )

        this.add<IGlobalLootModifier?>(
            "mandrake_seeds_to_tall_grass",
            AddItemModifier(
                arrayOf(
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TALL_GRASS).build(),
                    LootItemRandomChanceCondition.randomChance(0.05f).build()
                ), WitcheryItems.MANDRAKE_SEEDS.get()
            )
        )

        this.add<IGlobalLootModifier?>(
            "snowbell_seeds_to_short_grass",
            AddItemModifier(
                arrayOf(
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SHORT_GRASS).build(),
                    LootItemRandomChanceCondition.randomChance(0.05f).build()
                ), WitcheryItems.SNOWBELL_SEEDS.get()
            )
        )

        this.add<IGlobalLootModifier?>(
            "snowbell_seeds_to_tall_grass",
            AddItemModifier(
                arrayOf(
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TALL_GRASS).build(),
                    LootItemRandomChanceCondition.randomChance(0.05f).build()
                ), WitcheryItems.SNOWBELL_SEEDS.get()
            )
        )

        this.add<IGlobalLootModifier?>(
            "water_artichoke_seeds_to_short_grass",
            AddItemModifier(
                arrayOf(
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SHORT_GRASS).build(),
                    LootItemRandomChanceCondition.randomChance(0.05f).build()
                ), WitcheryItems.WATER_ARTICHOKE_SEEDS.get()
            )
        )

        this.add<IGlobalLootModifier?>(
            "water_artichoke_seeds_to_tall_grass",
            AddItemModifier(
                arrayOf(
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TALL_GRASS).build(),
                    LootItemRandomChanceCondition.randomChance(0.05f).build()
                ), WitcheryItems.WATER_ARTICHOKE_SEEDS.get()
            )
        )

        this.add<IGlobalLootModifier?>(
            "wolfsbane_seeds_to_short_grass",
            AddItemModifier(
                arrayOf(
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SHORT_GRASS).build(),
                    LootItemRandomChanceCondition.randomChance(0.03f).build()
                ), WitcheryItems.WOLFSBANE_SEEDS.get()
            )
        )

        this.add<IGlobalLootModifier?>(
            "wolfsbane_seeds_to_tall_grass",
            AddItemModifier(
                arrayOf(
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TALL_GRASS).build(),
                    LootItemRandomChanceCondition.randomChance(0.03f).build()
                ), WitcheryItems.WOLFSBANE_SEEDS.get()
            )
        )


        this.add(
            "toe_from_frog",
            AddItemModifier(
                arrayOf(
                    LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/frog")).build(),
                    LootItemRandomChanceCondition.randomChance(0.25f).build()
                ), WitcheryItems.TOE_OF_FROG.get()
            )
        )

        this.add(
            "tongue_from_wolf",
            AddItemModifier(
                arrayOf(
                    LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wolf")).build(),
                    LootItemRandomChanceCondition.randomChance(0.25f).build()
                ), WitcheryItems.TONGUE_OF_DOG.get()
            )
        )

        this.add(
            "wool_from_bat",
            AddItemModifier(
                arrayOf(
                    LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/bat")).build(),
                    LootItemRandomChanceCondition.randomChance(0.25f).build()
                ), WitcheryItems.WOOL_OF_BAT.get()
            )
        )

        this.add(
            "hand_from_witch",
            AddItemModifier(
                arrayOf(
                    LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/witch")).build(),
                    LootItemRandomChanceCondition.randomChance(0.5f).build()
                ), WitcheryItems.WITCHES_HAND.get()
            )
        )
    }
}