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
        builder.add(WitcheryItems.GUIDEBOOK.get(), "Guidebook")
    }
}