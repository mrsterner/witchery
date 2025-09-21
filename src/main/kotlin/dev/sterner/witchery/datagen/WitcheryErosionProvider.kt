package dev.sterner.witchery.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data.BloodPoolReloadListener
import dev.sterner.witchery.data.ErosionReloadListener
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.JsonCodecProvider
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class WitcheryErosionProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper
) : JsonCodecProvider<ErosionReloadListener.ErosionData>(
    output,
    PackOutput.Target.DATA_PACK,
    "erosion",
    PackType.SERVER_DATA,
    ErosionReloadListener.ErosionData.CODEC,
    lookupProvider,
    Witchery.MODID,
    existingFileHelper
) {

    override fun getName(): String {
        return "erosion"
    }

    override fun gather() {
        makeBlock(Blocks.SMOOTH_STONE, Blocks.STONE)
        makeBlock(Blocks.STONE_STAIRS, Blocks.COBBLESTONE_STAIRS)
        makeBlock(Blocks.SMOOTH_BASALT, Blocks.BASALT)
        makeBlock(Blocks.SMOOTH_QUARTZ, Blocks.QUARTZ_BLOCK)
        makeBlock(Blocks.SMOOTH_STONE_SLAB, Blocks.STONE_SLAB)
        makeBlock(Blocks.STONE_SLAB, Blocks.COBBLESTONE_SLAB)
        makeBlock(Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SANDSTONE_SLAB)
        makeBlock(Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE_SLAB)
        makeBlock(Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.SANDSTONE_STAIRS)
        makeBlock(Blocks.SMOOTH_RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE_STAIRS)

        makeBlock(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE)
        makeBlock(Blocks.COBBLED_DEEPSLATE, Blocks.GRAVEL)
        makeBlock(Blocks.STONE, Blocks.COBBLESTONE)
        makeBlock(Blocks.COBBLESTONE, Blocks.GRAVEL)
        makeBlock(Blocks.GRAVEL, Blocks.SAND)
        makeBlock(Blocks.DIRT_PATH, Blocks.DIRT)
        makeBlock(Blocks.PODZOL, Blocks.COARSE_DIRT)
        makeBlock(Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT)
        makeBlock(Blocks.COARSE_DIRT, Blocks.DIRT)
        makeBlock(Blocks.SANDSTONE, Blocks.SAND)
        makeBlock(Blocks.SMOOTH_SANDSTONE, Blocks.SANDSTONE)
        makeBlock(Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE)
        makeBlock(Blocks.CUT_SANDSTONE, Blocks.SANDSTONE)

        makeBlock(Blocks.RED_SANDSTONE, Blocks.RED_SAND)
        makeBlock(Blocks.SMOOTH_RED_SANDSTONE, Blocks.RED_SANDSTONE)
        makeBlock(Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE)
        makeBlock(Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE)

        makeBlock(Blocks.MUD, Blocks.CLAY)
    }

    private fun makeBlock(
        from: Block,
        to: Block
    ) {
        val fromId = BuiltInRegistries.BLOCK.getKey(from)
        val toId = BuiltInRegistries.BLOCK.getKey(to)
        unconditional(Witchery.id(fromId.path), ErosionReloadListener.ErosionData(fromId, toId))
    }
}