package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data.ErosionHandler
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

class WitcheryErosionProvider(
    dataOutput: FabricDataOutput?,
    registriesFuture: CompletableFuture<HolderLookup.Provider>?
) : FabricCodecDataProvider<ErosionHandler.ErosionData>(
    dataOutput,
    registriesFuture,
    PackOutput.Target.DATA_PACK,
    DIRECTORY,
    ErosionHandler.ErosionData.CODEC
) {

    companion object {
        val DIRECTORY: String = "erosion"
    }

    override fun getName(): String {
        return DIRECTORY
    }

    override fun configure(
        provider: BiConsumer<ResourceLocation, ErosionHandler.ErosionData>,
        lookup: HolderLookup.Provider?
    ) {


        makeBlock(provider, Blocks.SMOOTH_STONE, Blocks.STONE)
        makeBlock(provider, Blocks.STONE_STAIRS, Blocks.COBBLESTONE_STAIRS)
        makeBlock(provider, Blocks.SMOOTH_BASALT, Blocks.BASALT)
        makeBlock(provider, Blocks.SMOOTH_QUARTZ, Blocks.QUARTZ_BLOCK)
        makeBlock(provider, Blocks.SMOOTH_STONE_SLAB, Blocks.STONE_SLAB)
        makeBlock(provider, Blocks.STONE_SLAB, Blocks.COBBLESTONE_SLAB)
        makeBlock(provider, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SANDSTONE_SLAB)
        makeBlock(provider, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE_SLAB)
        makeBlock(provider, Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.SANDSTONE_STAIRS)
        makeBlock(provider, Blocks.SMOOTH_RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE_STAIRS)

        makeBlock(provider, Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE)
        makeBlock(provider, Blocks.COBBLED_DEEPSLATE, Blocks.GRAVEL)
        makeBlock(provider, Blocks.STONE, Blocks.COBBLESTONE)
        makeBlock(provider, Blocks.COBBLESTONE, Blocks.GRAVEL)
        makeBlock(provider, Blocks.GRAVEL, Blocks.SAND)
        makeBlock(provider, Blocks.DIRT_PATH, Blocks.DIRT)
        makeBlock(provider, Blocks.PODZOL, Blocks.COARSE_DIRT)
        makeBlock(provider, Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT)
        makeBlock(provider, Blocks.COARSE_DIRT, Blocks.DIRT)
        makeBlock(provider, Blocks.SANDSTONE, Blocks.SAND)
        makeBlock(provider, Blocks.SMOOTH_SANDSTONE, Blocks.SANDSTONE)
        makeBlock(provider, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE)
        makeBlock(provider, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE)

        makeBlock(provider, Blocks.RED_SANDSTONE, Blocks.RED_SAND)
        makeBlock(provider, Blocks.SMOOTH_RED_SANDSTONE, Blocks.RED_SANDSTONE)
        makeBlock(provider, Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE)
        makeBlock(provider, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE)

        makeBlock(provider, Blocks.MUD, Blocks.CLAY)
    }

    private fun makeBlock(
        provider: BiConsumer<ResourceLocation, ErosionHandler.ErosionData>,
        from: Block,
        to: Block
    ) {
        val fromId = BuiltInRegistries.BLOCK.getKey(from)
        val toId = BuiltInRegistries.BLOCK.getKey(to)
        provider.accept(Witchery.id(fromId.path), ErosionHandler.ErosionData(fromId, toId))
    }
}