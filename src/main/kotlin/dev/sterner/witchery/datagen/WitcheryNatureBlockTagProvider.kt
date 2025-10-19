package dev.sterner.witchery.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.data.NaturePowerReloadListener
import dev.sterner.witchery.core.registry.WitcheryTags
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.JsonCodecProvider
import java.util.concurrent.CompletableFuture

class WitcheryNatureBlockTagProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper
) : JsonCodecProvider<NaturePowerReloadListener.Data>(
    output,
    PackOutput.Target.DATA_PACK,
    "nature/tag",
    PackType.SERVER_DATA,
    NaturePowerReloadListener.Data.CODEC,
    lookupProvider,
    Witchery.MODID,
    existingFileHelper
) {


    override fun getName(): String {
        return "nature/tag"
    }

    override fun gather() {
        makeTag(BlockTags.SAPLINGS, 4, 20)
        makeTag(WitcheryTags.BROWN_MUSHROOM, 3, 20)
        makeTag(WitcheryTags.RED_MUSHROOM, 3, 20)
        makeTag(WitcheryTags.LOGS, 3, 50)
        makeTag(WitcheryTags.LEAVES, 4, 50)
        makeTag(BlockTags.LOGS, 2, 50)
        makeTag(BlockTags.LEAVES, 3, 100)

        //1.21
        makeTag(BlockTags.CAVE_VINES, 3, 20)
        makeTag(WitcheryTags.TORCHFLOWER, 4, 30)  // Torchflower crop and block
        makeTag(WitcheryTags.PITCHER, 3, 60)      // Pitcher crop and plant
        makeTag(WitcheryTags.BAMBOO, 2, 40)       // Bamboo and bamboo sapling
        makeTag(WitcheryTags.MOSSY_BLOCKS, 1, 50) // Mossy cobble and stone bricks
        makeTag(TagKey.create(Registries.BLOCK, ResourceLocation.parse("c:budding_blocks")), 5, 10)
        makeTag(TagKey.create(Registries.BLOCK, ResourceLocation.parse("c:clusters")), 4, 20)
        makeTag(TagKey.create(Registries.BLOCK, ResourceLocation.parse("c:buds")), 2, 20)
        makeTag(WitcheryTags.MOSS, 3, 20)         // Moss blocks and carpet
        makeTag(WitcheryTags.CRIMSON_FUNGUS, 3, 40) // Crimson fungus + nether wart block
        makeTag(WitcheryTags.WARPED_FUNGUS, 3, 40)  // Warped fungus + warped wart block
        makeTag(WitcheryTags.NETHER_FOLIAGE, 4, 40) // Crimson roots, warped roots, nether sprouts
        makeTag(WitcheryTags.OVERWORLD_FOLIAGE, 3, 50)
        makeTag(WitcheryTags.BIG_DRIPLEAF, 3, 60)
        makeTag(WitcheryTags.KELP, 1, 80)
        makeTag(BlockTags.CORAL_BLOCKS, 4, 40)
        makeTag(BlockTags.CORALS, 2, 80)
        makeTag(BlockTags.BEEHIVES, 3, 20)
        makeTag(WitcheryTags.SCULK, 2, 80)
    }

    private fun makeTag(
        tag: TagKey<Block>,
        power: Int,
        limit: Int
    ) {
        unconditional(tag.location, NaturePowerReloadListener.Data(tag.location, power, limit))
    }
}