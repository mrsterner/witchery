package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.block.MandrakeCropBlock
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.core.HolderLookup
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CropBlock
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
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MandrakeCropBlock.AGE, 4))
        val builder2: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.BELLADONNAE_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MandrakeCropBlock.AGE, 4))

        createCropDrops(WitcheryBlocks.MANDRAKE_CROP.get(),WitcheryItems.MANDRAKE_ROOT.get(), WitcheryItems.MANDRAKE_SEEDS.get(), builder)
        createCropDrops(WitcheryBlocks.BELLADONNAE_CROP.get(),WitcheryItems.BELLADONNA_FLOWER.get(), WitcheryItems.BELLADONNA_SEEDS.get(), builder2)
    }
}