package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.worldgen.patch.MushroomCircleFeature
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration

object WitcheryFeatures {

    val FEATURES: DeferredRegister<Feature<*>> =
        DeferredRegister.create(Witchery.MODID, Registries.FEATURE)

    val MUSHROOM_CIRCLE = FEATURES.register("mushroom_circle") {
        MushroomCircleFeature(RandomPatchConfiguration.CODEC)
    }
}