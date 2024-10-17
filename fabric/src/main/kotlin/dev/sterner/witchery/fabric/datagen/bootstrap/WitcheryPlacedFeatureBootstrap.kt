package dev.sterner.witchery.fabric.datagen.bootstrap

import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.ALDER_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.ALDER_PLACED_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.HAWTHORN_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.HAWTHORN_PLACED_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.ROWAN_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.ROWAN_PLACED_KEY
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.data.worldgen.placement.VegetationPlacements
import net.minecraft.world.level.levelgen.placement.PlacedFeature

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
    }
}