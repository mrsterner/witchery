package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.EntityTypeTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.tags.EntityTypeTags
import net.minecraft.world.entity.EntityType
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

        getOrCreateTagBuilder(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
            .add(WitcheryEntityTypes.NIGHTMARE.get())
            .add(WitcheryEntityTypes.VAMPIRE.get())
            .add(WitcheryEntityTypes.BANSHEE.get())
            .add(WitcheryEntityTypes.SPECTRAL_PIG.get())

        getOrCreateTagBuilder(WitcheryTags.NECROMANCER_SUMMONABLE)
            .add(EntityType.ZOMBIE)
            .add(EntityType.ZOMBIE_HORSE)
            .add(EntityType.ZOMBIE_VILLAGER)
            .add(EntityType.ZOMBIFIED_PIGLIN)
            .add(EntityType.SKELETON)
            .add(EntityType.SKELETON_HORSE)
            .add(EntityType.WITHER_SKELETON)
    }
}