package dev.sterner.witchery.features.petrification


import dev.sterner.witchery.Witchery
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.event.level.LevelEvent

@EventBusSubscriber(modid = Witchery.MODID, value = [Dist.CLIENT])
object PetrificationClientHandler {

    private var tickCounter = 0
    private const val CLEANUP_INTERVAL = 6000

    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post) {
        tickCounter++

        if (tickCounter >= CLEANUP_INTERVAL) {
            tickCounter = 0
            PetrificationTextureManager.clearOldTextures()
        }
    }

    @SubscribeEvent
    fun onWorldUnload(event: LevelEvent.Unload) {
        if (event.level.isClientSide) {
            PetrificationTextureManager.clearCache()
        }
    }
}
