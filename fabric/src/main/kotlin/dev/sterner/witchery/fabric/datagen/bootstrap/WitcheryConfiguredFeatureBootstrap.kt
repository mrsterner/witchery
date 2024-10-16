package dev.sterner.witchery.fabric.datagen.bootstrap

import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.ROWAN_KEY
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.util.random.SimpleWeightedRandomList
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer

object WitcheryConfiguredFeatureBootstrap {
    fun bootstrap(bootstrapContext: BootstrapContext<ConfiguredFeature<*, *>>) {
        bootstrapContext.register(ROWAN_KEY, ConfiguredFeature(
            Feature.TREE, TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(WitcheryBlocks.ROWAN_LOG.get()),
                StraightTrunkPlacer(5, 4, 3),
                WeightedStateProvider(SimpleWeightedRandomList.builder<BlockState>()
                    .add(WitcheryBlocks.ROWAN_LEAVES.get().defaultBlockState())
                    .add(WitcheryBlocks.ROWAN_BERRY_LEAVES.get().defaultBlockState()).build()),
                BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 4),
                TwoLayersFeatureSize(1, 0, 2)).build()))
    }
}