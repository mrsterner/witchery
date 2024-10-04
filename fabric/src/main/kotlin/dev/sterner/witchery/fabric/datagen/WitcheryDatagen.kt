package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.fabric.datagen.bootstrap.WitcheryConfiguredFeatureBootstrap
import dev.sterner.witchery.fabric.datagen.bootstrap.WitcheryPlacedFeatureBootstrap
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries

class WitcheryDatagen : DataGeneratorEntrypoint {

    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack = generator.createPack()

        pack.addProvider{output, reg ->
            WitcheryBlockTagProvider(output, reg)
        }
        pack.addProvider{output, reg ->
            WitcheryItemTagProvider(output, reg)
        }
        pack.addProvider{output, _ ->
            WitcheryModelProvider(output)
        }
        pack.addProvider{output, reg ->
            WitcheryLangProvider(output, reg)
        }
        pack.addProvider{output, reg ->
            WitcheryRecipeProvider(output, reg)
        }
        pack.addProvider{output, reg ->
            WitcheryAdvancementProvider(output, reg)
        }
        pack.addProvider{output, reg ->
            WitcheryBlockLootProvider(output, reg)
        }
        pack.addProvider{output, reg ->
            WitcheryEntityLootProvider(output, reg)
        }
        pack.addProvider{output, reg ->
            WitcheryEntityTypeTagProvider(output, reg)
        }

        pack.addProvider { out, teg ->
            WitcheryBookProvider(out, teg) { a, s ->
                WitcheryLangProvider(out, teg)
            }
        }
    }

    override fun buildRegistry(registryBuilder: RegistrySetBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE) { context ->
            WitcheryConfiguredFeatureBootstrap.bootstrap(context)
        }
        registryBuilder.add(Registries.PLACED_FEATURE) { context ->
            WitcheryPlacedFeatureBootstrap.bootstrap(context)
        }
    }
}