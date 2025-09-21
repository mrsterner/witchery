package dev.sterner.witchery

import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryKeyMappings
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent

@EventBusSubscriber(modid = Witchery.MODID, bus = EventBusSubscriber.Bus.MOD)
object WitcheryModEvents {

    @SubscribeEvent
    fun onRegisterCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            WitcheryBlockEntityTypes.CAULDRON.get()
        ) { be, _ ->
            be.fluidTank
        }
    }

    @SubscribeEvent
    fun onRegisterKey(event: RegisterKeyMappingsEvent) {
        event.register(WitcheryKeyMappings.BROOM_DISMOUNT_KEYMAPPING)
        event.register(WitcheryKeyMappings.OPEN_ABILITY_SELECTION)
    }
}