package dev.sterner.witchery.fabric.datagen.bootstrap

import com.google.common.collect.ImmutableList
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.ALDER_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.ALDER_PLACED_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.HAWTHORN_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.HAWTHORN_PLACED_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.ROWAN_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.ROWAN_PLACED_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.WISPY_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.WISPY_PLACED_KEY
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.data.worldgen.placement.VegetationPlacements
import net.minecraft.world.level.levelgen.placement.*

object WitcheryPlacedFeatureBootstrap {
    fun bootstrap(context: BootstrapContext<PlacedFeature>) {
        val configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE)

        context.register(ROWAN_PLACED_KEY, PlacedFeature(configuredFeatures.getOrThrow(ROWAN_KEY),
            VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1f, 2),
                WitcheryBlocks.ROWAN_SAPLING.get()).toList()
        ))

        context.register(ALDER_PLACED_KEY, PlacedFeature(configuredFeatures.getOrThrow(ALDER_KEY),
            VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1f, 2),
                WitcheryBlocks.ALDER_SAPLING.get()).toList()
        ))

        context.register(HAWTHORN_PLACED_KEY, PlacedFeature(configuredFeatures.getOrThrow(HAWTHORN_KEY),
            VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1f, 2),
                WitcheryBlocks.HAWTHORN_SAPLING.get()).toList()
        ))

        context.register(WISPY_PLACED_KEY, PlacedFeature(configuredFeatures.getOrThrow(WISPY_KEY),
            bushPlacement().build().toList()
        ))
    }


    private fun bushPlacement(): ImmutableList.Builder<PlacementModifier> {
        return ImmutableList.builder<PlacementModifier>()
            .add(RarityFilter.onAverageOnceEvery(6))
            .add(InSquarePlacement.spread())
            .add(NoiseThresholdCountPlacement.of(-0.8, 15, 4))
            .add(PlacementUtils.HEIGHTMAP)
            .add(BiomeFilter.biome())
    }
}