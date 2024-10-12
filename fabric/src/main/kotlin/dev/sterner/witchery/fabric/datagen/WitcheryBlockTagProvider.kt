package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.registry.WitcheryBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.tags.BlockTags
import java.util.concurrent.CompletableFuture

class WitcheryBlockTagProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider.BlockTagProvider(output, registriesFuture) {

    override fun addTags(wrapperLookup: HolderLookup.Provider) {
        getOrCreateTagBuilder(BlockTags.CROPS)
            .add(WitcheryBlocks.MANDRAKE_CROP.get())
            .add(WitcheryBlocks.SNOWBELL_CROP.get())
            .add(WitcheryBlocks.BELLADONNAE_CROP.get())
            .add(WitcheryBlocks.WORMWOOD_CROP.get())
            .add(WitcheryBlocks.WOLFSFBANE_CROP.get())
            .add(WitcheryBlocks.WATER_ARTICHOKE_CROP.get())
            .add(WitcheryBlocks.GARLIC_CROP.get())

    }
}