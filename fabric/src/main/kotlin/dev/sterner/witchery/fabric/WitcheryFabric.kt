package dev.sterner.witchery.fabric

import dev.sterner.witchery.Witchery
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer


class WitcheryFabric : ModInitializer, ClientModInitializer {

    override fun onInitialize() {
        Witchery.init()
    }

    override fun onInitializeClient() {
        Witchery.initClient()
    }
}


