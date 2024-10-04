package dev.sterner.witchery.neoforge

import dev.sterner.witchery.Witchery
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry
import org.jetbrains.annotations.NotNull
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist


@Mod(Witchery.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object WitcheryNeoForge {

    init {
        Witchery.init()

        runForDist(
            clientTarget = {
                MOD_BUS.addListener(::onClientSetup)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(::onServerSetup)
                "test"
            }
        )
    }

    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {

    }

    @SubscribeEvent
    fun regParticle(event: RegisterParticleProvidersEvent){

    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        Witchery.initClient()
    }

    @SubscribeEvent
    private fun initializeClient(event: RegisterClientExtensionsEvent) {

    }

    @SubscribeEvent
    private fun registerLayerDefinitions(event: EntityRenderersEvent.RegisterLayerDefinitions) {

    }

    @SubscribeEvent
    fun createDataPackRegistries(event: NewRegistry) {
    }
}
