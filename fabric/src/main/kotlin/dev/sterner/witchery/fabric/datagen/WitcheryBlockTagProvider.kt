package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.CompletableFuture

class WitcheryBlockTagProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider.BlockTagProvider(output, registriesFuture) {

    override fun addTags(wrapperLookup: HolderLookup.Provider) {

        getOrCreateTagBuilder(WitcheryTags.LEAVES).add(
            WitcheryBlocks.ROWAN_LEAVES.get(),
            WitcheryBlocks.ROWAN_BERRY_LEAVES.get(),
            WitcheryBlocks.ALDER_LEAVES.get(),
            WitcheryBlocks.HAWTHORN_LEAVES.get()
        )

        getOrCreateTagBuilder(WitcheryTags.LOGS)
            .addTag(WitcheryTags.ROWAN_LOGS)
            .addTag(WitcheryTags.ALDER_LOGS)
            .addTag(WitcheryTags.HAWTHORN_LOGS)

        getOrCreateTagBuilder(WitcheryTags.ROWAN_LOGS).add(
            WitcheryBlocks.ROWAN_LOG.get(),
            WitcheryBlocks.ROWAN_WOOD.get(),
            WitcheryBlocks.STRIPPED_ROWAN_LOG.get(),
            WitcheryBlocks.STRIPPED_ROWAN_WOOD.get()
        )

        getOrCreateTagBuilder(WitcheryTags.ALDER_LOGS).add(
            WitcheryBlocks.ALDER_LOG.get(),
            WitcheryBlocks.ALDER_WOOD.get(),
            WitcheryBlocks.STRIPPED_ALDER_LOG.get(),
            WitcheryBlocks.STRIPPED_ALDER_WOOD.get()
        )

        getOrCreateTagBuilder(WitcheryTags.HAWTHORN_LOGS).add(
            WitcheryBlocks.HAWTHORN_LOG.get(),
            WitcheryBlocks.HAWTHORN_WOOD.get(),
            WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get(),
            WitcheryBlocks.STRIPPED_HAWTHORN_WOOD.get()
        )

        getOrCreateTagBuilder(WitcheryTags.CANDELABRAS).add(
            WitcheryBlocks.IRON_CANDELABRA.get(),
            WitcheryBlocks.WHITE_IRON_CANDELABRA.get(),
            WitcheryBlocks.ORANGE_IRON_CANDELABRA.get(),
            WitcheryBlocks.MAGENTA_IRON_CANDELABRA.get(),
            WitcheryBlocks.LIGHT_BLUE_IRON_CANDELABRA.get(),
            WitcheryBlocks.YELLOW_IRON_CANDELABRA.get(),
            WitcheryBlocks.LIME_IRON_CANDELABRA.get(),
            WitcheryBlocks.PINK_IRON_CANDELABRA.get(),
            WitcheryBlocks.GRAY_IRON_CANDELABRA.get(),
            WitcheryBlocks.LIGHT_GRAY_IRON_CANDELABRA.get(),
            WitcheryBlocks.CYAN_IRON_CANDELABRA.get(),
            WitcheryBlocks.PURPLE_IRON_CANDELABRA.get(),
            WitcheryBlocks.BLUE_IRON_CANDELABRA.get(),
            WitcheryBlocks.BROWN_IRON_CANDELABRA.get(),
            WitcheryBlocks.GREEN_IRON_CANDELABRA.get(),
            WitcheryBlocks.RED_IRON_CANDELABRA.get(),
            WitcheryBlocks.BLACK_IRON_CANDELABRA.get()
        )

        getOrCreateTagBuilder(BlockTags.FLOWERS)
            .add(WitcheryBlocks.GLINTWEED.get())
            .add(WitcheryBlocks.EMBER_MOSS.get())

        getOrCreateTagBuilder(BlockTags.CROPS)
            .add(WitcheryBlocks.MANDRAKE_CROP.get())
            .add(WitcheryBlocks.SNOWBELL_CROP.get())
            .add(WitcheryBlocks.BELLADONNAE_CROP.get())
            .add(WitcheryBlocks.WORMWOOD_CROP.get())
            .add(WitcheryBlocks.WOLFSFBANE_CROP.get())
            .add(WitcheryBlocks.WATER_ARTICHOKE_CROP.get())
            .add(WitcheryBlocks.GARLIC_CROP.get())

        getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN)
            .addTag(WitcheryTags.ROWAN_LOGS)
            .addTag(WitcheryTags.ALDER_LOGS)
            .addTag(WitcheryTags.HAWTHORN_LOGS)

        getOrCreateTagBuilder(BlockTags.WOODEN_FENCES).add(
            WitcheryBlocks.ROWAN_FENCE.get(),
            WitcheryBlocks.ALDER_FENCE.get(),
            WitcheryBlocks.HAWTHORN_FENCE.get()
        )

        getOrCreateTagBuilder(BlockTags.FENCE_GATES).add(
            WitcheryBlocks.ROWAN_FENCE_GATE.get(),
            WitcheryBlocks.ALDER_FENCE_GATE.get(),
            WitcheryBlocks.HAWTHORN_FENCE_GATE.get()
        )

        getOrCreateTagBuilder(BlockTags.WOODEN_PRESSURE_PLATES).add(
            WitcheryBlocks.ROWAN_PRESSURE_PLATE.get(),
            WitcheryBlocks.ALDER_PRESSURE_PLATE.get(),
            WitcheryBlocks.HAWTHORN_PRESSURE_PLATE.get()
        )

        getOrCreateTagBuilder(BlockTags.WOODEN_SLABS).add(
            WitcheryBlocks.ROWAN_SLAB.get(),
            WitcheryBlocks.ALDER_SLAB.get(),
            WitcheryBlocks.HAWTHORN_SLAB.get()
        )

        getOrCreateTagBuilder(BlockTags.WOODEN_STAIRS).add(
            WitcheryBlocks.ROWAN_STAIRS.get(),
            WitcheryBlocks.ALDER_STAIRS.get(),
            WitcheryBlocks.HAWTHORN_STAIRS.get()
        )

        getOrCreateTagBuilder(BlockTags.WOODEN_BUTTONS).add(
            WitcheryBlocks.ROWAN_BUTTON.get(),
            WitcheryBlocks.ALDER_BUTTON.get(),
            WitcheryBlocks.HAWTHORN_BUTTON.get()
        )

        getOrCreateTagBuilder(BlockTags.PLANKS).add(
            WitcheryBlocks.ROWAN_PLANKS.get(),
            WitcheryBlocks.ALDER_PLANKS.get(),
            WitcheryBlocks.HAWTHORN_PLANKS.get()
        )

        getOrCreateTagBuilder(BlockTags.LEAVES)
            .addTag(WitcheryTags.LEAVES)

        getOrCreateTagBuilder(BlockTags.WOODEN_DOORS).add(
            WitcheryBlocks.ROWAN_DOOR.get(),
            WitcheryBlocks.ALDER_DOOR.get(),
            WitcheryBlocks.HAWTHORN_DOOR.get()
        )

        getOrCreateTagBuilder(BlockTags.WOODEN_TRAPDOORS).add(
            WitcheryBlocks.ROWAN_TRAPDOOR.get(),
            WitcheryBlocks.ALDER_TRAPDOOR.get(),
            WitcheryBlocks.HAWTHORN_TRAPDOOR.get()
        )

        getOrCreateTagBuilder(BlockTags.SAPLINGS).add(
            WitcheryBlocks.ROWAN_SAPLING.get(),
            WitcheryBlocks.ALDER_SAPLING.get(),
            WitcheryBlocks.HAWTHORN_SAPLING.get()
        )

        getOrCreateTagBuilder(BlockTags.FLOWER_POTS).add(
            WitcheryBlocks.POTTED_ROWAN_SAPLING.get(),
            WitcheryBlocks.POTTED_ALDER_SAPLING.get(),
            WitcheryBlocks.POTTED_HAWTHORN_SAPLING.get()
        )

        getOrCreateTagBuilder(BlockTags.STANDING_SIGNS).add(
            WitcheryBlocks.ROWAN_SIGN.get(),
            WitcheryBlocks.ALDER_SIGN.get(),
            WitcheryBlocks.HAWTHORN_SIGN.get()
        )

        getOrCreateTagBuilder(BlockTags.WALL_SIGNS).add(
            WitcheryBlocks.ROWAN_WALL_SIGN.get(),
            WitcheryBlocks.ALDER_WALL_SIGN.get(),
            WitcheryBlocks.HAWTHORN_WALL_SIGN.get()
        )

        getOrCreateTagBuilder(BlockTags.CEILING_HANGING_SIGNS).add(
            WitcheryBlocks.ROWAN_HANGING_SIGN.get(),
            WitcheryBlocks.ALDER_HANGING_SIGN.get(),
            WitcheryBlocks.HAWTHORN_HANGING_SIGN.get()
        )

        getOrCreateTagBuilder(BlockTags.WALL_HANGING_SIGNS).add(
            WitcheryBlocks.ROWAN_WALL_HANGING_SIGN.get(),
            WitcheryBlocks.ALDER_WALL_HANGING_SIGN.get(),
            WitcheryBlocks.HAWTHORN_WALL_HANGING_SIGN.get()
        )

        getOrCreateTagBuilder(BlockTags.BEE_GROWABLES)
            .add(WitcheryBlocks.MANDRAKE_CROP.get())
            .add(WitcheryBlocks.SNOWBELL_CROP.get())
            .add(WitcheryBlocks.BELLADONNAE_CROP.get())
            .add(WitcheryBlocks.WORMWOOD_CROP.get())
            .add(WitcheryBlocks.WOLFSFBANE_CROP.get())
            .add(WitcheryBlocks.WATER_ARTICHOKE_CROP.get())
            .add(WitcheryBlocks.GARLIC_CROP.get())


        // Common Tags


        //getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed")))
        //    .addTag(WitcheryTags.CANDELABRAS)

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/white")))
            .add(WitcheryBlocks.WHITE_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/orange")))
            .add(WitcheryBlocks.ORANGE_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/magenta")))
            .add(WitcheryBlocks.MAGENTA_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/light_blue")))
            .add(WitcheryBlocks.LIGHT_BLUE_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/yellow")))
            .add(WitcheryBlocks.YELLOW_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/lime")))
            .add(WitcheryBlocks.LIME_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/pink")))
            .add(WitcheryBlocks.PINK_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/gray")))
            .add(WitcheryBlocks.GRAY_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/light_gray")))
            .add(WitcheryBlocks.LIGHT_GRAY_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/cyan")))
            .add(WitcheryBlocks.CYAN_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/purple")))
            .add(WitcheryBlocks.PURPLE_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/blue")))
            .add(WitcheryBlocks.BLUE_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/brown")))
            .add(WitcheryBlocks.BROWN_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/green")))
            .add(WitcheryBlocks.GREEN_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/red")))
            .add(WitcheryBlocks.RED_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "dyed/black")))
            .add(WitcheryBlocks.BLACK_IRON_CANDELABRA.get())


        // Nature Grouping Tags


        getOrCreateTagBuilder(WitcheryTags.BAMBOO)
            .add(Blocks.BAMBOO_BLOCK)
            .add(Blocks.BAMBOO_SAPLING)

        getOrCreateTagBuilder(WitcheryTags.BIG_DRIPLEAF)
            .add(Blocks.BIG_DRIPLEAF)
            .add(Blocks.BIG_DRIPLEAF_STEM)

        getOrCreateTagBuilder(WitcheryTags.BROWN_MUCHROOM)
            .add(Blocks.BROWN_MUSHROOM_BLOCK)
            .add(Blocks.BROWN_MUSHROOM)
            .add(Blocks.POTTED_BROWN_MUSHROOM)

        getOrCreateTagBuilder(WitcheryTags.CRIMSON_FUNGUS)
            .add(Blocks.CRIMSON_FUNGUS)
            .add(Blocks.NETHER_WART_BLOCK)
            .add(Blocks.NETHER_WART)

        getOrCreateTagBuilder(WitcheryTags.KELP)
            .add(Blocks.KELP)
            .add(Blocks.KELP_PLANT)

        getOrCreateTagBuilder(WitcheryTags.MOSS)
            .add(Blocks.MOSS_BLOCK)
            .add(Blocks.MOSS_CARPET)

        getOrCreateTagBuilder(WitcheryTags.MOSSY_BLOCKS)
            .add(Blocks.MOSSY_COBBLESTONE)
            .add(Blocks.MOSSY_COBBLESTONE_STAIRS)
            .add(Blocks.MOSSY_COBBLESTONE_SLAB)
            .add(Blocks.MOSSY_COBBLESTONE_WALL)
            .add(Blocks.MOSSY_STONE_BRICKS)
            .add(Blocks.MOSSY_STONE_BRICK_STAIRS)
            .add(Blocks.MOSSY_STONE_BRICK_SLAB)
            .add(Blocks.MOSSY_STONE_BRICK_WALL)

        getOrCreateTagBuilder(WitcheryTags.NETHER_FOLIAGE)
            .add(Blocks.CRIMSON_ROOTS)
            .add(Blocks.POTTED_CRIMSON_ROOTS)
            .add(Blocks.WARPED_ROOTS)
            .add(Blocks.POTTED_WARPED_ROOTS)
            .add(Blocks.NETHER_SPROUTS)

        getOrCreateTagBuilder(WitcheryTags.OVERWORLD_FOLIAGE)
            .add(Blocks.SHORT_GRASS)
            .add(Blocks.FERN)
            .add(Blocks.LARGE_FERN)
            .add(Blocks.TALL_GRASS)
            .add(Blocks.SEAGRASS)
            .add(Blocks.TALL_SEAGRASS)

        getOrCreateTagBuilder(WitcheryTags.PITCHER)
            .add(Blocks.PITCHER_CROP)
            .add(Blocks.PITCHER_PLANT)

        getOrCreateTagBuilder(WitcheryTags.RED_MUSHROOM)
            .add(Blocks.RED_MUSHROOM)
            .add(Blocks.RED_MUSHROOM_BLOCK)
            .add(Blocks.POTTED_RED_MUSHROOM)

        getOrCreateTagBuilder(WitcheryTags.SCULK)
            .add(Blocks.SCULK)
            .add(Blocks.SCULK_VEIN)

        getOrCreateTagBuilder(WitcheryTags.TORCHFLOWER)
            .add(Blocks.TORCHFLOWER)
            .add(Blocks.TORCHFLOWER_CROP)

        getOrCreateTagBuilder(WitcheryTags.WARPED_FUNGUS)
            .add(Blocks.WARPED_FUNGUS)
            .add(Blocks.POTTED_WARPED_FUNGUS)
            .add(Blocks.WARPED_WART_BLOCK)

        getOrCreateTagBuilder(WitcheryTags.VINES)
            .addOptionalTag(BlockTags.CAVE_VINES)
            .add(WitcheryBlocks.SPANISH_MOSS.get())
            .add(Blocks.VINE)
            .add(Blocks.WEEPING_VINES)
            .add(Blocks.TWISTING_VINES)

    }
}