package dev.sterner.witchery.fabric.datagen.bootstrap

import dev.sterner.witchery.block.CottonBlock
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryFeatures
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.ALDER_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.HAWTHORN_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.ROWAN_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.WISPY_KEY
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys.WITCH_CIRCLE_KEY
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.util.random.SimpleWeightedRandomList
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer
import java.util.*

object WitcheryConfiguredFeatureBootstrap {

    fun bootstrap(bootstrapContext: BootstrapContext<ConfiguredFeature<*, *>>) {
        bootstrapContext.register(
            ROWAN_KEY, ConfiguredFeature(
                Feature.TREE, TreeConfiguration.TreeConfigurationBuilder(
                    BlockStateProvider.simple(WitcheryBlocks.ROWAN_LOG.get()),
                    StraightTrunkPlacer(5, 2, 0),
                    WeightedStateProvider(
                        SimpleWeightedRandomList.builder<BlockState>()
                            .add(WitcheryBlocks.ROWAN_LEAVES.get().defaultBlockState())
                            .add(WitcheryBlocks.ROWAN_LEAVES.get().defaultBlockState())
                            .add(WitcheryBlocks.ROWAN_BERRY_LEAVES.get().defaultBlockState()).build()
                    ),
                    BlobFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), 3),
                    TwoLayersFeatureSize(1, 0, 1)
                ).build()
            )
        )

        bootstrapContext.register(
            ALDER_KEY, ConfiguredFeature(
                Feature.TREE, TreeConfiguration.TreeConfigurationBuilder(
                    BlockStateProvider.simple(WitcheryBlocks.ALDER_LOG.get()),
                    FancyTrunkPlacer(3, 5, 0),
                    BlockStateProvider.simple(WitcheryBlocks.ALDER_LEAVES.get()),
                    FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                    TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
                ).build()
            )
        )

        bootstrapContext.register(
            HAWTHORN_KEY, ConfiguredFeature(
                Feature.TREE, TreeConfiguration.TreeConfigurationBuilder(
                    BlockStateProvider.simple(WitcheryBlocks.HAWTHORN_LOG.get()),
                    FancyTrunkPlacer(3, 9, 0),
                    BlockStateProvider.simple(WitcheryBlocks.HAWTHORN_LEAVES.get()),
                    FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                    TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
                ).build()
            )
        )

        bootstrapContext.register(
            WISPY_KEY, ConfiguredFeature(
                Feature.RANDOM_PATCH,
                FeatureUtils.simpleRandomPatchConfiguration(
                    24,
                    PlacementUtils.onlyWhenEmpty(
                        Feature.SIMPLE_BLOCK,
                        SimpleBlockConfiguration(
                            BlockStateProvider.simple(
                                WitcheryBlocks.WISPY_COTTON.get().defaultBlockState()
                                    .setValue(CottonBlock.SPIRITED, true)
                            )
                        )
                    )
                )
            )
        )

        bootstrapContext.register(
            WITCH_CIRCLE_KEY, ConfiguredFeature(
                WitcheryFeatures.MUSHROOM_CIRCLE.get(),
                FeatureUtils.simpleRandomPatchConfiguration(
                    8,
                    PlacementUtils.onlyWhenEmpty(
                        Feature.SIMPLE_BLOCK,
                        SimpleBlockConfiguration(
                            BlockStateProvider.simple(
                                Blocks.RED_MUSHROOM.defaultBlockState()
                            )
                        )
                    )
                )
            )
        )
    }
}