package dev.sterner.witchery.data_gen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.data.AltarAugmentReloadListener
import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.core.registry.WitcheryTags
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.JsonCodecProvider
import java.util.Optional
import java.util.concurrent.CompletableFuture

class WitcheryAltarAugmentProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper
) : JsonCodecProvider<AltarAugmentReloadListener.AltarAugment>(
    output,
    PackOutput.Target.DATA_PACK,
    "altar_augments",
    PackType.SERVER_DATA,
    AltarAugmentReloadListener.AltarAugment.CODEC,
    lookupProvider,
    Witchery.MODID,
    existingFileHelper
) {

    override fun getName(): String {
        return "Altar Augments"
    }

    override fun gather() {
        lightAugment("censer", WitcheryBlocks.CENSER.get(), 2.5)
        lightAugmentTag("candelabra", WitcheryTags.CANDELABRAS.location(), true, 2.0)
        lightAugment("soul_torch", Blocks.SOUL_TORCH, 1.5)
        lightAugment("torch", Blocks.TORCH, 1.0)
        lightAugmentCandleTag("candles_4", BlockTags.CANDLES.location(), true, 4, 1.0)
        lightAugmentCandleTag("candles_3", BlockTags.CANDLES.location(), true, 3, 0.75)
        lightAugmentCandleTag("candles_2", BlockTags.CANDLES.location(), true, 2, 0.5)
        lightAugmentCandleTag("candles_1", BlockTags.CANDLES.location(), true, 1, 0.25)
        lightAugmentTag("candle_cakes", BlockTags.CANDLE_CAKES.location(), true, 0.25)

        headAugment("player_head", Blocks.PLAYER_HEAD, 3.0)
        headAugment("player_wall_head", Blocks.PLAYER_WALL_HEAD, 3.0)
        headAugment("wither_skeleton_skull", Blocks.WITHER_SKELETON_SKULL, 2.0)
        headAugment("wither_skeleton_wall_skull", Blocks.WITHER_SKELETON_WALL_SKULL, 2.0)
        headAugment("skeleton_skull", Blocks.SKELETON_SKULL, 1.0)
        headAugment("skeleton_wall_skull", Blocks.SKELETON_WALL_SKULL, 1.0)

        chaliceAugment("chalice_with_soup", WitcheryBlocks.CHALICE.get(), true, 2.0)
        chaliceAugment("chalice", WitcheryBlocks.CHALICE.get(), false, 1.0)
        chaliceAugment("crystal_ball", WitcheryBlocks.CRYSTAL_BALL.get(), false, 2.0)

        rangeAugment("arthana", WitcheryBlocks.ARTHANA.get(), 2.0)

        specialAugment("pentacle", WitcheryBlocks.PENTACLE.get(), hasPentacle = true)
        specialAugment("infinity_egg", WitcheryBlocks.INFINITY_EGG.get(), hasInfinityEgg = true)
    }

    private fun lightAugment(name: String, block: Block, bonus: Double) {
        val blockId = BuiltInRegistries.BLOCK.getKey(block)
        unconditional(
            Witchery.id(name),
            AltarAugmentReloadListener.AltarAugment(
                block = Optional.of(blockId),
                tag = Optional.empty(),
                requiresLit = false,
                requiredCandleCount = Optional.empty(),
                requiresSoup = false,
                bonus = AltarAugmentReloadListener.AugmentBonus(
                    lightBonus = bonus,
                    headBonus = 0.0,
                    chaliceBonus = 0.0,
                    rangeMultiplier = 1.0,
                    hasPentacle = false,
                    hasInfinityEgg = false
                ),
                category = AltarAugmentReloadListener.AugmentCategory.LIGHT
            )
        )
    }

    private fun lightAugmentTag(name: String, tag: ResourceLocation, requiresLit: Boolean, bonus: Double) {
        unconditional(
            Witchery.id(name),
            AltarAugmentReloadListener.AltarAugment(
                block = Optional.empty(),
                tag = Optional.of(tag),
                requiresLit = requiresLit,
                requiredCandleCount = Optional.empty(),
                requiresSoup = false,
                bonus = AltarAugmentReloadListener.AugmentBonus(
                    lightBonus = bonus,
                    headBonus = 0.0,
                    chaliceBonus = 0.0,
                    rangeMultiplier = 1.0,
                    hasPentacle = false,
                    hasInfinityEgg = false
                ),
                category = AltarAugmentReloadListener.AugmentCategory.LIGHT
            )
        )
    }

    private fun lightAugmentCandleTag(
        name: String,
        tag: ResourceLocation,
        requiresLit: Boolean,
        candleCount: Int,
        bonus: Double
    ) {
        unconditional(
            Witchery.id(name),
            AltarAugmentReloadListener.AltarAugment(
                block = Optional.empty(),
                tag = Optional.of(tag),
                requiresLit = requiresLit,
                requiredCandleCount = Optional.of(candleCount),
                requiresSoup = false,
                bonus = AltarAugmentReloadListener.AugmentBonus(
                    lightBonus = bonus,
                    headBonus = 0.0,
                    chaliceBonus = 0.0,
                    rangeMultiplier = 1.0,
                    hasPentacle = false,
                    hasInfinityEgg = false
                ),
                category = AltarAugmentReloadListener.AugmentCategory.LIGHT
            )
        )
    }

    private fun headAugment(name: String, block: Block, bonus: Double) {
        val blockId = BuiltInRegistries.BLOCK.getKey(block)
        unconditional(
            Witchery.id(name),
            AltarAugmentReloadListener.AltarAugment(
                block = Optional.of(blockId),
                tag = Optional.empty(),
                requiresLit = false,
                requiredCandleCount = Optional.empty(),
                requiresSoup = false,
                bonus = AltarAugmentReloadListener.AugmentBonus(
                    lightBonus = 0.0,
                    headBonus = bonus,
                    chaliceBonus = 0.0,
                    rangeMultiplier = 1.0,
                    hasPentacle = false,
                    hasInfinityEgg = false
                ),
                category = AltarAugmentReloadListener.AugmentCategory.HEAD
            )
        )
    }

    private fun chaliceAugment(name: String, block: Block, requiresSoup: Boolean, bonus: Double) {
        val blockId = BuiltInRegistries.BLOCK.getKey(block)
        unconditional(
            Witchery.id(name),
            AltarAugmentReloadListener.AltarAugment(
                block = Optional.of(blockId),
                tag = Optional.empty(),
                requiresLit = false,
                requiredCandleCount = Optional.empty(),
                requiresSoup = requiresSoup,
                bonus = AltarAugmentReloadListener.AugmentBonus(
                    lightBonus = 0.0,
                    headBonus = 0.0,
                    chaliceBonus = bonus,
                    rangeMultiplier = 1.0,
                    hasPentacle = false,
                    hasInfinityEgg = false
                ),
                category = AltarAugmentReloadListener.AugmentCategory.CHALICE
            )
        )
    }

    private fun rangeAugment(name: String, block: Block, multiplier: Double) {
        val blockId = BuiltInRegistries.BLOCK.getKey(block)
        unconditional(
            Witchery.id(name),
            AltarAugmentReloadListener.AltarAugment(
                block = Optional.of(blockId),
                tag = Optional.empty(),
                requiresLit = false,
                requiredCandleCount = Optional.empty(),
                requiresSoup = false,
                bonus = AltarAugmentReloadListener.AugmentBonus(
                    lightBonus = 0.0,
                    headBonus = 0.0,
                    chaliceBonus = 0.0,
                    rangeMultiplier = multiplier,
                    hasPentacle = false,
                    hasInfinityEgg = false
                ),
                category = AltarAugmentReloadListener.AugmentCategory.RANGE
            )
        )
    }

    private fun specialAugment(
        name: String,
        block: Block,
        hasPentacle: Boolean = false,
        hasInfinityEgg: Boolean = false
    ) {
        val blockId = BuiltInRegistries.BLOCK.getKey(block)
        unconditional(
            Witchery.id(name),
            AltarAugmentReloadListener.AltarAugment(
                block = Optional.of(blockId),
                tag = Optional.empty(),
                requiresLit = false,
                requiredCandleCount = Optional.empty(),
                requiresSoup = false,
                bonus = AltarAugmentReloadListener.AugmentBonus(
                    lightBonus = 0.0,
                    headBonus = 0.0,
                    chaliceBonus = 0.0,
                    rangeMultiplier = 1.0,
                    hasPentacle = hasPentacle,
                    hasInfinityEgg = hasInfinityEgg
                ),
                category = AltarAugmentReloadListener.AugmentCategory.SPECIAL
            )
        )
    }
}