package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data.NaturePowerBlockData
import dev.sterner.witchery.registry.WitcheryBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class WitcheryNatureBlockProvider(
    dataOutput: FabricDataOutput?,
    registriesFuture: CompletableFuture<HolderLookup.Provider>?
) : FabricCodecDataProvider<NaturePowerBlockData>(dataOutput, registriesFuture, PackOutput.Target.DATA_PACK, DIRECTORY, NaturePowerBlockData.CODEC) {

    companion object {
        val DIRECTORY: String = "nature"
    }

    override fun getName(): String {
        return DIRECTORY
    }

    override fun configure(provider: BiConsumer<ResourceLocation, NaturePowerBlockData>, lookup: HolderLookup.Provider?) {
        makeBlock(provider, Blocks.GRASS_BLOCK, 2, 80)
        makeBlock(provider, Blocks.PODZOL, 1, 80)
        makeBlock(provider, Blocks.DIRT, 1, 80)
        makeBlock(provider, Blocks.FARMLAND, 1, 100)
        makeBlock(provider, Blocks.MYCELIUM, 1, 80)
        makeBlock(provider, Blocks.WATER, 1, 50)
        makeBlock(provider, Blocks.SUGAR_CANE, 3, 50)
        makeBlock(provider, Blocks.DANDELION, 4, 30)
        makeBlock(provider, Blocks.POPPY, 4, 30)
        makeBlock(provider, Blocks.BLUE_ORCHID, 4, 30)
        makeBlock(provider, Blocks.ALLIUM, 4, 30)
        makeBlock(provider, Blocks.AZURE_BLUET, 4, 30)
        makeBlock(provider, Blocks.RED_TULIP, 4, 30)
        makeBlock(provider, Blocks.ORANGE_TULIP, 4, 30)
        makeBlock(provider, Blocks.WHITE_TULIP, 4, 30)
        makeBlock(provider, Blocks.PINK_TULIP, 4, 30)
        makeBlock(provider, Blocks.OXEYE_DAISY, 4, 30)
        makeBlock(provider, Blocks.CACTUS, 3, 50)
        makeBlock(provider, Blocks.PUMPKIN, 4, 20)
        makeBlock(provider, Blocks.MELON, 4, 20)
        makeBlock(provider, Blocks.VINE, 2, 50)
        makeBlock(provider, Blocks.WHEAT, 4, 20)
        makeBlock(provider, Blocks.POTATOES, 4, 20)
        makeBlock(provider, Blocks.CARROTS, 4, 20)
        makeBlock(provider, Blocks.PUMPKIN_STEM, 3, 20)
        makeBlock(provider, Blocks.MELON_STEM, 3, 20)
        makeBlock(provider, Blocks.COCOA, 3, 20)
        makeBlock(provider, Blocks.DRAGON_EGG, 250, 1)
        makeBlock(provider, WitcheryBlocks.BELLADONNAE_CROP.get(), 4, 20)
        makeBlock(provider, WitcheryBlocks.MANDRAKE_CROP.get(), 4, 20)
        makeBlock(provider, WitcheryBlocks.SNOWBELL_CROP.get(), 4, 20)
        makeBlock(provider, WitcheryBlocks.WATER_ARTICHOKE_CROP.get(), 4, 20)
        makeBlock(provider, WitcheryBlocks.GLINTWEED.get(), 2, 20)
        makeBlock(provider, WitcheryBlocks.EMBER_MOSS.get(), 4, 20)
        makeBlock(provider, WitcheryBlocks.SPANISH_MOSS.get(), 3, 20)
        //makeBlock(provider, WitcheryBlocks.CRITTER_SNARE.get(), 2, 10)
        //makeBlock(provider, WitcheryBlocks.GRASSPER.get(), 2, 10)
        makeBlock(provider, WitcheryBlocks.BLOOD_POPPY.get(), 2, 10)
        //makeBlock(provider, WitcheryBlocks.WISPY_COTTON.get(), 3, 20)
        makeBlock(provider, WitcheryBlocks.DEMON_HEART.get(), 100, 2)
        makeBlock(provider, WitcheryBlocks.INFINITY_EGG.get(), 6000, 1)

        //1.21
        makeBlock(provider, WitcheryBlocks.GARLIC_CROP.get(), 4, 20);
        makeBlock(provider, WitcheryBlocks.WOLFSFBANE_CROP.get(), 4, 20);
        makeBlock(provider, WitcheryBlocks.WORMWOOD_CROP.get(), 4, 20);
        makeBlock(provider, Blocks.BEETROOTS, 3, 20);
        makeBlock(provider, Blocks.NETHER_WART, 4, 20);
        makeBlock(provider, Blocks.SWEET_BERRY_BUSH, 3, 20);
        makeBlock(provider, Blocks.AMETHYST_BLOCK, 4, 20);
        makeBlock(provider, Blocks.ROOTED_DIRT, 2, 80);
        makeBlock(provider, Blocks.CRIMSON_NYLIUM, 3, 80);
        makeBlock(provider, Blocks.WARPED_NYLIUM, 3, 80);
        makeBlock(provider, Blocks.MANGROVE_ROOTS, 1, 60);
        makeBlock(provider, Blocks.MUDDY_MANGROVE_ROOTS, 2, 40);
        makeBlock(provider, Blocks.MUSHROOM_STEM, 3, 20);
        makeBlock(provider, Blocks.SHROOMLIGHT, 3, 20);
        makeBlock(provider, Blocks.CORNFLOWER, 4, 30);
        makeBlock(provider, Blocks.LILY_OF_THE_VALLEY, 4, 30);
        makeBlock(provider, Blocks.WITHER_ROSE, 3, 30);
        makeBlock(provider, Blocks.PINK_PETALS, 2, 50);
        makeBlock(provider, Blocks.SPORE_BLOSSOM, 5, 20);
        makeBlock(provider, Blocks.WEEPING_VINES, 3, 50);
        makeBlock(provider, Blocks.TWISTING_VINES, 3, 50);
        makeBlock(provider, Blocks.SUNFLOWER, 2, 60);
        makeBlock(provider, Blocks.LILAC, 2, 60);
        makeBlock(provider, Blocks.ROSE_BUSH, 2, 60);
        makeBlock(provider, Blocks.PEONY, 2, 60);
        makeBlock(provider, Blocks.SMALL_DRIPLEAF, 2, 60);
        makeBlock(provider, Blocks.CHORUS_PLANT, 2, 30);
        makeBlock(provider, Blocks.CHORUS_FLOWER, 2, 20);
        makeBlock(provider, Blocks.GLOW_LICHEN, 1, 30);
        makeBlock(provider, Blocks.LILY_PAD, 1, 40);
        makeBlock(provider, Blocks.SEA_PICKLE, 2, 30);
        makeBlock(provider, Blocks.SCULK_CATALYST, 4, 10);
        makeBlock(provider, Blocks.SCULK_SHRIEKER, 4, 10);
        makeBlock(provider, Blocks.SCULK_SENSOR, 3, 30);
    }

    private fun makeBlock(provider: BiConsumer<ResourceLocation, NaturePowerBlockData>, block: Block, power: Int, limit: Int) {
        val id = BuiltInRegistries.BLOCK.getKey(block)
        provider.accept(Witchery.id(id.path), NaturePowerBlockData(id, power, limit))
    }
}