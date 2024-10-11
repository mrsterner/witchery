package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class WitcheryLangProvider(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) :
    FabricLanguageProvider(dataOutput, registryLookup) {

    override fun generateTranslations(registryLookup: HolderLookup.Provider?, builder: TranslationBuilder) {
        builder.add("witchery.main", "Witchery")

        builder.add("emi.category.witchery.cauldron_brewing", "Cauldron Brewing")
        builder.add("emi.category.witchery.cauldron_crafting", "Cauldron Crafting")

        builder.add(WitcheryItems.GUIDEBOOK.get(), "Lesser Key of Solomon")
        builder.add(WitcheryItems.CAULDRON.get(), "Cauldron")
        builder.add(WitcheryItems.ALTAR.get(), "Altar")
        builder.add(WitcheryItems.MUTANDIS.get(), "Mutandis")
    }
}