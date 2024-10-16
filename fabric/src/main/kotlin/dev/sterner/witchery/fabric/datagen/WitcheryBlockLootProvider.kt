package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.block.WitcheryCropBlock
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.core.HolderLookup
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import java.util.concurrent.CompletableFuture

class WitcheryBlockLootProvider(
    dataOutput: FabricDataOutput,
    registryLookup: CompletableFuture<HolderLookup.Provider>
) : FabricBlockLootTableProvider(dataOutput, registryLookup) {

    override fun generate() {
        dropSelf(WitcheryBlocks.CAULDRON.get())
        dropSelf(WitcheryBlocks.IRON_WITCHES_OVEN.get())
        dropSelf(WitcheryBlocks.COPPER_WITCHES_OVEN.get())

        dropSelf(WitcheryBlocks.ROWAN_LOG.get())
        dropSelf(WitcheryBlocks.ROWAN_WOOD.get())
        dropSelf(WitcheryBlocks.STRIPPED_ROWAN_LOG.get())
        dropSelf(WitcheryBlocks.STRIPPED_ROWAN_WOOD.get())
        dropSelf(WitcheryBlocks.ROWAN_PLANKS.get())
        dropSelf(WitcheryBlocks.ROWAN_STAIRS.get())
        dropSelf(WitcheryBlocks.ROWAN_SLAB.get())
        dropSelf(WitcheryBlocks.ROWAN_FENCE.get())
        dropSelf(WitcheryBlocks.ROWAN_FENCE_GATE.get())
        dropSelf(WitcheryBlocks.ROWAN_PRESSURE_PLATE.get())
        dropSelf(WitcheryBlocks.ROWAN_BUTTON.get())

        // TODO: Sapling, Leaves, Potted Sapling

        this.add(
            WitcheryBlocks.EMBER_MOSS.get()
        ) { itemLike: Block ->
            createShearsOnlyDrop(
                itemLike
            )
        }

        this.add(
            WitcheryBlocks.GLINTWEED.get()
        ) { itemLike: Block ->
            createShearsOnlyDrop(
                itemLike
            )
        }

        this.add(
            WitcheryBlocks.SPANISH_MOSS.get()
        ) { itemLike: Block ->
            createShearsOnlyDrop(
                itemLike
            )
        }

        val builder: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.MANDRAKE_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WitcheryCropBlock.AGE, 4))
        val builder2: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.BELLADONNAE_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WitcheryCropBlock.AGE, 4))
        val builder3: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.SNOWBELL_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WitcheryCropBlock.AGE, 4))
        val builder4: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.WATER_ARTICHOKE_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WitcheryCropBlock.AGE, 4))
        val builder5: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.WOLFSFBANE_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WitcheryCropBlock.AGE, 4))
        val builder6: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.WORMWOOD_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WitcheryCropBlock.AGE, 4))
        val builder7: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.GARLIC_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WitcheryCropBlock.AGE, 4))



        add(WitcheryBlocks.MANDRAKE_CROP.get(), createCropDrops(WitcheryBlocks.MANDRAKE_CROP.get(),WitcheryItems.MANDRAKE_ROOT.get(), WitcheryItems.MANDRAKE_SEEDS.get(), builder))
        add(WitcheryBlocks.BELLADONNAE_CROP.get(), createCropDrops(WitcheryBlocks.BELLADONNAE_CROP.get(),WitcheryItems.BELLADONNA_FLOWER.get(), WitcheryItems.BELLADONNA_SEEDS.get(), builder2))
        add(WitcheryBlocks.WATER_ARTICHOKE_CROP.get(),createCropDrops(WitcheryBlocks.WATER_ARTICHOKE_CROP.get(),WitcheryItems.WATER_ARTICHOKE_GLOBE.get(), WitcheryItems.WATER_ARTICHOKE_SEEDS.get(), builder4))
        add(WitcheryBlocks.SNOWBELL_CROP.get(),createCropDrops(WitcheryBlocks.SNOWBELL_CROP.get(),WitcheryItems.ICY_NEEDLE.get(), WitcheryItems.SNOWBELL_SEEDS.get(), builder3))

        add(WitcheryBlocks.WOLFSFBANE_CROP.get(),createCropDrops(WitcheryBlocks.WOLFSFBANE_CROP.get(), WitcheryItems.WOLFSBANE.get(), WitcheryItems.WOLFSBANE_SEEDS.get(), builder5))
        add(WitcheryBlocks.WORMWOOD_CROP.get(), createCropDrops(WitcheryBlocks.WORMWOOD_CROP.get(), WitcheryItems.WORMWOOD.get(), WitcheryItems.WORMWOOD_SEEDS.get(), builder6))
        add(WitcheryBlocks.GARLIC_CROP.get(),createCropDrops(WitcheryBlocks.GARLIC_CROP.get(), WitcheryItems.GARLIC.get(), WitcheryItems.GARLIC.get(), builder7))

    }
}