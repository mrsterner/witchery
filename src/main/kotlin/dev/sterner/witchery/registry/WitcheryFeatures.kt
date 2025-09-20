package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.worldgen.patch.MushroomCircleFeature
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object WitcheryFeatures {

    val FEATURES: DeferredRegister<Feature<*>> =
        DeferredRegister.create(Registries.FEATURE, Witchery.MODID)

    val MUSHROOM_CIRCLE = FEATURES.register("mushroom_circle", Supplier {
        MushroomCircleFeature(RandomPatchConfiguration.CODEC)
    })
}