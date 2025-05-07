package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.data.NaturePowerReloadListener
import dev.sterner.witchery.registry.WitcheryTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class WitcheryNatureBlockTagProvider(
    dataOutput: FabricDataOutput?,
    registriesFuture: CompletableFuture<HolderLookup.Provider>?
) : FabricCodecDataProvider<NaturePowerReloadListener.Data>(
    dataOutput,
    registriesFuture,
    PackOutput.Target.DATA_PACK,
    DIRECTORY,
    NaturePowerReloadListener.Data.TAG_CODEC
) {

    companion object {
        val DIRECTORY: String = "nature/tag"
    }

    override fun getName(): String {
        return DIRECTORY
    }

    override fun configure(
        provider: BiConsumer<ResourceLocation, NaturePowerReloadListener.Data>,
        lookup: HolderLookup.Provider?
    ) {
        makeTag(provider, BlockTags.SAPLINGS, 4, 20)
        makeTag(provider, WitcheryTags.BROWN_MUSHROOM, 3, 20)
        makeTag(provider, WitcheryTags.RED_MUSHROOM, 3, 20)
        makeTag(provider, WitcheryTags.LOGS, 3, 50)
        makeTag(provider, WitcheryTags.LEAVES, 4, 50)
        makeTag(provider, BlockTags.LOGS, 2, 50)
        makeTag(provider, BlockTags.LEAVES, 3, 100)

        //1.21
        makeTag(provider, BlockTags.CAVE_VINES, 3, 20)
        makeTag(provider, WitcheryTags.TORCHFLOWER, 4, 30)  // Torchflower crop and block
        makeTag(provider, WitcheryTags.PITCHER, 3, 60)      // Pitcher crop and plant
        makeTag(provider, WitcheryTags.BAMBOO, 2, 40)       // Bamboo and bamboo sapling
        makeTag(provider, WitcheryTags.MOSSY_BLOCKS, 1, 50) // Mossy cobble and stone bricks
        makeTag(provider, TagKey.create(Registries.BLOCK, ResourceLocation.parse("c:budding_blocks")), 5, 10)
        makeTag(provider, TagKey.create(Registries.BLOCK, ResourceLocation.parse("c:clusters")), 4, 20)
        makeTag(provider, TagKey.create(Registries.BLOCK, ResourceLocation.parse("c:buds")), 2, 20)
        makeTag(provider, WitcheryTags.MOSS, 3, 20)         // Moss blocks and carpet
        makeTag(provider, WitcheryTags.CRIMSON_FUNGUS, 3, 40) // Crimson fungus + nether wart block
        makeTag(provider, WitcheryTags.WARPED_FUNGUS, 3, 40)  // Warped fungus + warped wart block
        makeTag(provider, WitcheryTags.NETHER_FOLIAGE, 4, 40) // Crimson roots, warped roots, nether sprouts
        makeTag(provider, WitcheryTags.OVERWORLD_FOLIAGE, 3, 50)
        makeTag(provider, WitcheryTags.BIG_DRIPLEAF, 3, 60)
        makeTag(provider, WitcheryTags.KELP, 1, 80)
        makeTag(provider, BlockTags.CORAL_BLOCKS, 4, 40)
        makeTag(provider, BlockTags.CORALS, 2, 80)
        makeTag(provider, BlockTags.BEEHIVES, 3, 20)
        makeTag(provider, WitcheryTags.SCULK, 2, 80)
    }

    private fun makeTag(
        provider: BiConsumer<ResourceLocation, NaturePowerReloadListener.Data>,
        tag: TagKey<Block>,
        power: Int,
        limit: Int
    ) {
        provider.accept(tag.location, NaturePowerReloadListener.Data(tag.location, power, limit))
    }
}