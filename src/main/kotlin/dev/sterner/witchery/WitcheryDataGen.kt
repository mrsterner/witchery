package dev.sterner.witchery

import dev.sterner.witchery.datagen.*
import dev.sterner.witchery.datagen.bootstrap.WitcheryRegistryDataGen
import net.minecraft.data.PackOutput
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.data.event.GatherDataEvent


@EventBusSubscriber(modid = Witchery.MODID)
object WitcheryDataGen {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput: PackOutput = generator.packOutput

        generator.addProvider(
            true,
            WitcheryAdvancementProvider(packOutput, event.lookupProvider, event.existingFileHelper)
        )

        generator.addProvider(true, WitcheryLootProvider(packOutput, event.lookupProvider))
        val blockTag = WitcheryBlockTagProvider(packOutput, event.lookupProvider, event.existingFileHelper)
        generator.addProvider(true, blockTag)
        generator.addProvider(true, WitcheryBloodProvider(packOutput, event.lookupProvider, event.existingFileHelper))

        val v = WitcheryBookProvider(packOutput, event.lookupProvider) { a, s ->
            WitcheryLangProvider(packOutput, Witchery.MODID, "en_us")
        }
        generator.addProvider(true, v)
        generator.addProvider(
            true,
            WitcheryDamageTypeTagProvider(packOutput, event.lookupProvider, event.existingFileHelper)
        )
        generator.addProvider(
            true,
            WitcheryEntityTypeTagProvider(packOutput, event.lookupProvider, event.existingFileHelper)
        )
        generator.addProvider(true, WitcheryErosionProvider(packOutput, event.lookupProvider, event.existingFileHelper))
        generator.addProvider(true, WitcheryFetishProvider(packOutput, event.lookupProvider, event.existingFileHelper))
        generator.addProvider(
            true,
            WitcheryInfiniteCenserProvider(packOutput, event.lookupProvider, event.existingFileHelper)
        )
        generator.addProvider(
            true,
            WitcheryItemTagProvider(
                packOutput,
                event.lookupProvider,
                blockTag.contentsGetter(),
                event.existingFileHelper
            )
        )
        generator.addProvider(true, WitcheryLangProvider(packOutput, Witchery.MODID, "en_us"))

        generator.addProvider(
            true,
            WitcheryNatureBlockProvider(packOutput, event.lookupProvider, event.existingFileHelper)
        )
        generator.addProvider(
            true,
            WitcheryNatureBlockTagProvider(packOutput, event.lookupProvider, event.existingFileHelper)
        )
        generator.addProvider(true, WitcheryPotionProvider(packOutput, event.lookupProvider, event.existingFileHelper))
        generator.addProvider(true, WitcheryRecipeProvider(packOutput, event.lookupProvider))

        val datapackProvider: DatapackBuiltinEntriesProvider = WitcheryRegistryDataGen(packOutput, event.lookupProvider)

        generator.addProvider(true, datapackProvider)
    }
}