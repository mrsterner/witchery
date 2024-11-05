package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.EntityTypeTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.tags.EntityTypeTags
import java.util.concurrent.CompletableFuture

class WitcheryEntityTypeTagProvider(
    output: FabricDataOutput?,
    completableFuture: CompletableFuture<HolderLookup.Provider>?
) : EntityTypeTagProvider(output, completableFuture) {

    override fun addTags(wrapperLookup: HolderLookup.Provider) {
        getOrCreateTagBuilder(EntityTypeTags.DISMOUNTS_UNDERWATER).add(
            WitcheryEntityTypes.CUSTOM_BOAT.get(),
            WitcheryEntityTypes.CUSTOM_CHEST_BOAT.get()
        )
    }
}