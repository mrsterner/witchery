package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.CompletableFuture

class WitcheryBlockTagProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider.BlockTagProvider(output, registriesFuture) {

    override fun addTags(wrapperLookup: HolderLookup.Provider) {

        getOrCreateTagBuilder(WitcheryTags.LEAVES).add(
            WitcheryBlocks.ROWAN_LEAVES.get(),
            WitcheryBlocks.ROWAN_BERRY_LEAVES.get()
        )

        getOrCreateTagBuilder(WitcheryTags.ROWAN_LOGS).add(
            WitcheryBlocks.ROWAN_LOG.get(),
            WitcheryBlocks.ROWAN_WOOD.get(),
            WitcheryBlocks.STRIPPED_ROWAN_LOG.get(),
            WitcheryBlocks.STRIPPED_ROWAN_WOOD.get()
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

        getOrCreateTagBuilder(BlockTags.WOODEN_FENCES)
            .add(WitcheryBlocks.ROWAN_FENCE.get())

        getOrCreateTagBuilder(BlockTags.FENCE_GATES)
            .add(WitcheryBlocks.ROWAN_FENCE_GATE.get())

        getOrCreateTagBuilder(BlockTags.WOODEN_PRESSURE_PLATES)
            .add(WitcheryBlocks.ROWAN_PRESSURE_PLATE.get())

        getOrCreateTagBuilder(BlockTags.WOODEN_SLABS)
            .add(WitcheryBlocks.ROWAN_SLAB.get())

        getOrCreateTagBuilder(BlockTags.WOODEN_STAIRS)
            .add(WitcheryBlocks.ROWAN_STAIRS.get())

        getOrCreateTagBuilder(BlockTags.WOODEN_BUTTONS)
            .add(WitcheryBlocks.ROWAN_BUTTON.get())

        getOrCreateTagBuilder(BlockTags.PLANKS)
            .add(WitcheryBlocks.ROWAN_PLANKS.get())

        getOrCreateTagBuilder(BlockTags.LEAVES)
            .addTag(WitcheryTags.LEAVES)

        getOrCreateTagBuilder(BlockTags.WOODEN_DOORS)
            .add(WitcheryBlocks.ROWAN_DOOR.get())

        getOrCreateTagBuilder(BlockTags.WOODEN_TRAPDOORS)
            .add(WitcheryBlocks.ROWAN_TRAPDOOR.get())

        getOrCreateTagBuilder(BlockTags.SAPLINGS)
            .add(WitcheryBlocks.ROWAN_SAPLING.get())

        getOrCreateTagBuilder(BlockTags.FLOWER_POTS)
            .add(WitcheryBlocks.POTTED_ROWAN_SAPLING.get())

        //TODO: Signs & Hanging Signs (4 separate tags)


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