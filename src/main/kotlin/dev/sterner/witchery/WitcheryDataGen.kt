package dev.sterner.witchery

import dev.sterner.witchery.datagen.WitcheryLangProvider
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

@EventBusSubscriber(modid = Witchery.MODID)
object WitcheryDataGen {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput

        generator.addProvider(true, WitcheryLangProvider(packOutput, Witchery.MODID, "en_us"))
    }
}