package dev.sterner.witchery

import com.mojang.logging.LogUtils
import dev.sterner.witchery.datagen.WitcheryLangProvider
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import org.slf4j.Logger

@Mod(Witchery.MODID)
class Witchery(modEventBus: IEventBus, modContainer: ModContainer) {

    init {
        modEventBus.addListener(::commonSetup)
        NeoForge.EVENT_BUS.register(this)
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {

    }

    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {

    }

    companion object {
        const val MODID: String = "witchery"
        val LOGGER: Logger = LogUtils.getLogger()
    }
}

