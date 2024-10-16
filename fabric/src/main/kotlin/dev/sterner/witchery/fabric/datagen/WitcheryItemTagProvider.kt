package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import java.util.concurrent.CompletableFuture

class WitcheryItemTagProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider.ItemTagProvider(output, registriesFuture) {

    override fun addTags(wrapperLookup: HolderLookup.Provider) {
        getOrCreateTagBuilder(ItemTags.LOGS_THAT_BURN).add(
            WitcheryItems.ROWAN_LOG.get(),
            WitcheryItems.ROWAN_WOOD.get(),
            WitcheryItems.STRIPPED_ROWAN_LOG.get(),
            WitcheryItems.STRIPPED_ROWAN_WOOD.get()
        )

        getOrCreateTagBuilder(ItemTags.WOODEN_FENCES)
            .add(WitcheryItems.ROWAN_FENCE.get())

        getOrCreateTagBuilder(ItemTags.FENCE_GATES)
            .add(WitcheryItems.ROWAN_FENCE_GATE.get())

        getOrCreateTagBuilder(ItemTags.WOODEN_PRESSURE_PLATES)
            .add(WitcheryItems.ROWAN_PRESSURE_PLATE.get())

        getOrCreateTagBuilder(ItemTags.WOODEN_SLABS)
            .add(WitcheryItems.ROWAN_SLAB.get())

        getOrCreateTagBuilder(ItemTags.WOODEN_STAIRS)
            .add(WitcheryItems.ROWAN_STAIRS.get())

        getOrCreateTagBuilder(ItemTags.WOODEN_BUTTONS)
            .add(WitcheryItems.ROWAN_BUTTON.get())

        getOrCreateTagBuilder(ItemTags.PLANKS)
            .add(WitcheryItems.ROWAN_PLANKS.get())

        getOrCreateTagBuilder(ItemTags.LEAVES).add(
            WitcheryItems.ROWAN_LEAVES.get(),
            WitcheryItems.ROWAN_BERRY_LEAVES.get()
        )

        //TODO: Signs (4 separate tags), Doors (Wooden), Trapdoors (Wooden?), Boats, Chest Boats, Saplings, Potted Saplings

        getOrCreateTagBuilder(ItemTags.VILLAGER_PLANTABLE_SEEDS).add(
            WitcheryItems.MANDRAKE_SEEDS.get(),
            WitcheryItems.BELLADONNA_SEEDS.get(),
            WitcheryItems.SNOWBELL_SEEDS.get(),
            WitcheryItems.WATER_ARTICHOKE_SEEDS.get(),
            WitcheryItems.GARLIC.get(),
            WitcheryItems.WOLFSBANE_SEEDS.get(),
            WitcheryItems.WORMWOOD_SEEDS.get()
        )

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "crops"))).add(
            WitcheryItems.MANDRAKE_ROOT.get(),
            WitcheryItems.BELLADONNA_FLOWER.get(),
            WitcheryItems.WATER_ARTICHOKE_GLOBE.get(),
            WitcheryItems.GARLIC.get(),
            WitcheryItems.WOLFSBANE.get(),
            WitcheryItems.WORMWOOD.get()
        )

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts"))).add(
            WitcheryItems.WOOD_ASH.get()
        )

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools"))).add(
            WitcheryItems.BONE_NEEDLE.get(),
            WitcheryItems.WAYSTONE.get(),
            WitcheryItems.TAGLOCK.get(),
            WitcheryItems.GOLDEN_CHALK.get(),
            WitcheryItems.RITUAL_CHALK.get(),
            WitcheryItems.INFERNAL_CHALK.get(),
            WitcheryItems.OTHERWHERE_CHALK.get()
        )

        getOrCreateTagBuilder(ItemTags.BOOKSHELF_BOOKS).add(
            WitcheryItems.GUIDEBOOK.get()
        )

        getOrCreateTagBuilder(ItemTags.LECTERN_BOOKS).add(
            WitcheryItems.GUIDEBOOK.get()
        )

        getOrCreateTagBuilder(ItemTags.PARROT_FOOD).add(
            WitcheryItems.MANDRAKE_SEEDS.get(),
            WitcheryItems.BELLADONNA_SEEDS.get(),
            WitcheryItems.SNOWBELL_SEEDS.get(),
            WitcheryItems.WATER_ARTICHOKE_SEEDS.get(),
            WitcheryItems.GARLIC.get(),
            WitcheryItems.WOLFSBANE_SEEDS.get(),
            WitcheryItems.WORMWOOD_SEEDS.get()
        )

        getOrCreateTagBuilder(ItemTags.PIGLIN_LOVED).add(
            WitcheryItems.GOLDEN_CHALK.get()
        )
    }
}