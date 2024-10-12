package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.block.WitcheryCropBlock
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.core.HolderLookup
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import java.util.concurrent.CompletableFuture

class WitcheryBlockLootProvider(
    dataOutput: FabricDataOutput,
    registryLookup: CompletableFuture<HolderLookup.Provider>
) : FabricBlockLootTableProvider(dataOutput, registryLookup) {

    override fun generate() {
        dropSelf(WitcheryBlocks.CAULDRON.get())

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



        createCropDrops(WitcheryBlocks.MANDRAKE_CROP.get(),WitcheryItems.MANDRAKE_ROOT.get(), WitcheryItems.MANDRAKE_SEEDS.get(), builder)
        createCropDrops(WitcheryBlocks.BELLADONNAE_CROP.get(),WitcheryItems.BELLADONNA_FLOWER.get(), WitcheryItems.BELLADONNA_SEEDS.get(), builder2)
        createCropDrops(WitcheryBlocks.WATER_ARTICHOKE_CROP.get(),WitcheryItems.WATER_ARTICHOKE_GLOBE.get(), WitcheryItems.WATER_ARTICHOKE_SEEDS.get(), builder4)
        createCropDrops(WitcheryBlocks.SNOWBELL_CROP.get(),WitcheryItems.ICY_NEEDLE.get(), WitcheryItems.SNOWBELL_SEEDS.get(), builder3)

        createCropDrops(WitcheryBlocks.WOLFSFBANE_CROP.get(), WitcheryItems.WOLFSBANE.get(), WitcheryItems.WOLFSBANE_SEEDS.get(), builder5)
        createCropDrops(WitcheryBlocks.WORMWOOD_CROP.get(), WitcheryItems.WORMWOOD.get(), WitcheryItems.WORMWOOD_SEEDS.get(), builder6)
        createCropDrops(WitcheryBlocks.GARLIC_CROP.get(), WitcheryItems.GARLIC.get(), WitcheryItems.GARLIC.get(), builder7)

    }
}