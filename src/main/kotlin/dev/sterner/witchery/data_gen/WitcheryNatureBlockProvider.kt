package dev.sterner.witchery.data_gen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.data.NaturePowerReloadListener
import dev.sterner.witchery.core.registry.WitcheryBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.server.packs.PackType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.JsonCodecProvider
import java.util.concurrent.CompletableFuture

class WitcheryNatureBlockProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper
) : JsonCodecProvider<NaturePowerReloadListener.Data>(
    output,
    PackOutput.Target.DATA_PACK,
    "nature",
    PackType.SERVER_DATA,
    NaturePowerReloadListener.Data.CODEC,
    lookupProvider,
    Witchery.MODID,
    existingFileHelper
) {
    override fun gather() {
        makeBlock(Blocks.GRASS_BLOCK, 2, 80)
        makeBlock(Blocks.PODZOL, 1, 80)
        makeBlock(Blocks.DIRT, 1, 80)
        makeBlock(Blocks.FARMLAND, 1, 100)
        makeBlock(Blocks.MYCELIUM, 1, 80)
        makeBlock(Blocks.WATER, 1, 50)
        makeBlock(Blocks.SUGAR_CANE, 3, 50)
        makeBlock(Blocks.DANDELION, 4, 30)
        makeBlock(Blocks.POPPY, 4, 30)
        makeBlock(Blocks.BLUE_ORCHID, 4, 30)
        makeBlock(Blocks.ALLIUM, 4, 30)
        makeBlock(Blocks.AZURE_BLUET, 4, 30)
        makeBlock(Blocks.RED_TULIP, 4, 30)
        makeBlock(Blocks.ORANGE_TULIP, 4, 30)
        makeBlock(Blocks.WHITE_TULIP, 4, 30)
        makeBlock(Blocks.PINK_TULIP, 4, 30)
        makeBlock(Blocks.OXEYE_DAISY, 4, 30)
        makeBlock(Blocks.CACTUS, 3, 50)
        makeBlock(Blocks.PUMPKIN, 4, 20)
        makeBlock(Blocks.MELON, 4, 20)
        makeBlock(Blocks.VINE, 2, 50)
        makeBlock(Blocks.WHEAT, 4, 20)
        makeBlock(Blocks.POTATOES, 4, 20)
        makeBlock(Blocks.CARROTS, 4, 20)
        makeBlock(Blocks.PUMPKIN_STEM, 3, 20)
        makeBlock(Blocks.MELON_STEM, 3, 20)
        makeBlock(Blocks.COCOA, 3, 20)
        makeBlock(Blocks.DRAGON_EGG, 250, 1)
        makeBlock(WitcheryBlocks.BELLADONNA_CROP.get(), 4, 20)
        makeBlock(WitcheryBlocks.MANDRAKE_CROP.get(), 4, 20)
        makeBlock(WitcheryBlocks.SNOWBELL_CROP.get(), 4, 20)
        makeBlock(WitcheryBlocks.WATER_ARTICHOKE_CROP.get(), 4, 20)
        makeBlock(WitcheryBlocks.GLINTWEED.get(), 2, 20)
        makeBlock(WitcheryBlocks.EMBER_MOSS.get(), 4, 20)
        makeBlock(WitcheryBlocks.SPANISH_MOSS.get(), 3, 20)
        makeBlock(WitcheryBlocks.CRITTER_SNARE.get(), 2, 10)
        makeBlock(WitcheryBlocks.GRASSPER.get(), 2, 10)
        makeBlock(WitcheryBlocks.BLOOD_POPPY.get(), 2, 10)
        makeBlock(WitcheryBlocks.WISPY_COTTON.get(), 3, 20)
        makeBlock(WitcheryBlocks.DEMON_HEART.get(), 100, 2)
        makeBlock(WitcheryBlocks.INFINITY_EGG.get(), 6000, 1)

        //1.21
        makeBlock(WitcheryBlocks.GARLIC_CROP.get(), 4, 20)
        makeBlock(WitcheryBlocks.WOLFSFBANE_CROP.get(), 4, 20)
        makeBlock(WitcheryBlocks.WORMWOOD_CROP.get(), 4, 20)
        makeBlock(Blocks.BEETROOTS, 3, 20)
        makeBlock(Blocks.NETHER_WART, 4, 20)
        makeBlock(Blocks.SWEET_BERRY_BUSH, 3, 20)
        makeBlock(Blocks.AMETHYST_BLOCK, 4, 20)
        makeBlock(Blocks.ROOTED_DIRT, 2, 80)
        makeBlock(Blocks.CRIMSON_NYLIUM, 3, 80)
        makeBlock(Blocks.WARPED_NYLIUM, 3, 80)
        makeBlock(Blocks.MANGROVE_ROOTS, 1, 60)
        makeBlock(Blocks.MUDDY_MANGROVE_ROOTS, 2, 40)
        makeBlock(Blocks.MUSHROOM_STEM, 3, 20)
        makeBlock(Blocks.SHROOMLIGHT, 3, 20)
        makeBlock(Blocks.CORNFLOWER, 4, 30)
        makeBlock(Blocks.LILY_OF_THE_VALLEY, 4, 30)
        makeBlock(Blocks.WITHER_ROSE, 3, 30)
        makeBlock(Blocks.PINK_PETALS, 2, 50)
        makeBlock(Blocks.SPORE_BLOSSOM, 5, 20)
        makeBlock(Blocks.WEEPING_VINES, 3, 50)
        makeBlock(Blocks.TWISTING_VINES, 3, 50)
        makeBlock(Blocks.SUNFLOWER, 2, 60)
        makeBlock(Blocks.LILAC, 2, 60)
        makeBlock(Blocks.ROSE_BUSH, 2, 60)
        makeBlock(Blocks.PEONY, 2, 60)
        makeBlock(Blocks.SMALL_DRIPLEAF, 2, 60)
        makeBlock(Blocks.CHORUS_PLANT, 2, 30)
        makeBlock(Blocks.CHORUS_FLOWER, 2, 20)
        makeBlock(Blocks.GLOW_LICHEN, 1, 30)
        makeBlock(Blocks.LILY_PAD, 1, 40)
        makeBlock(Blocks.SEA_PICKLE, 2, 30)
        makeBlock(Blocks.SCULK_CATALYST, 4, 10)
        makeBlock(Blocks.SCULK_SHRIEKER, 4, 10)
        makeBlock(Blocks.SCULK_SENSOR, 3, 30)
    }

    companion object {
        val DIRECTORY: String = "nature"
    }

    override fun getName(): String {
        return DIRECTORY
    }


    private fun makeBlock(
        block: Block,
        power: Int,
        limit: Int
    ) {
        val id = BuiltInRegistries.BLOCK.getKey(block)
        unconditional(Witchery.id(id.path), NaturePowerReloadListener.Data(id, power, limit))
    }
}