package dev.sterner.witchery.neoforge

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.screen.AltarScreen
import dev.sterner.witchery.client.screen.DistilleryScreen
import dev.sterner.witchery.client.screen.OvenScreen
import dev.sterner.witchery.client.screen.SpinningWheelScreen
import dev.sterner.witchery.neoforge.event.WitcheryNeoForgeClientEvent
import dev.sterner.witchery.registry.*
import net.minecraft.client.Minecraft
import net.minecraft.core.NonNullList
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ResolvableProfile
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.registries.DataPackRegistryEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist


@Mod(Witchery.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object WitcheryNeoForge {

    private val DATA_SERIALIZER_REGISTER: DeferredRegister<EntityDataSerializer<*>> =
        DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Witchery.MODID)

    private inline fun <reified T : EntityDataSerializer<*>> DeferredRegister<EntityDataSerializer<*>>.registerSerializer(
        name: String,
        noinline supplier: () -> T
    ): DeferredHolder<EntityDataSerializer<*>, T> {
        return register(name, supplier)
    }

    val INVENTORY_SERIALIZER: DeferredHolder<EntityDataSerializer<*>, EntityDataSerializer<NonNullList<ItemStack>>> =
        DATA_SERIALIZER_REGISTER.registerSerializer("inventory") { WitcheryEntityDataSerializers.INVENTORY }

    val RESOLVABLE_SERIALIZER: DeferredHolder<EntityDataSerializer<*>, EntityDataSerializer<ResolvableProfile>> =
        DATA_SERIALIZER_REGISTER.registerSerializer("resolvable") { WitcheryEntityDataSerializers.RESOLVABLE }

    init {
        WitcheryNeoForgeAttachmentRegistry.ATTACHMENT_TYPES.register(MOD_BUS)
        Witchery.init()

        DATA_SERIALIZER_REGISTER.register(MOD_BUS)

        runForDist(
            clientTarget = {
                MOD_BUS.addListener(::onClientSetup)
                MOD_BUS.register(WitcheryNeoForgeClientEvent)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(::onServerSetup)
                MOD_BUS.addListener(::onLoadComplete)
                "test"
            }
        )
    }

    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {

    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        Witchery.initClient()
    }

    private fun onLoadComplete(event: FMLLoadCompleteEvent) {
        WitcheryFlammability.register()
    }

    @SubscribeEvent
    private fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(WitcheryMenuTypes.OVEN_MENU_TYPE.get(), ::OvenScreen)
        event.register(WitcheryMenuTypes.ALTAR_MENU_TYPE.get(), ::AltarScreen)
        event.register(WitcheryMenuTypes.DISTILLERY_MENU_TYPE.get(), ::DistilleryScreen)
        event.register(WitcheryMenuTypes.SPINNING_WHEEL_MENU_TYPE.get(), ::SpinningWheelScreen)
    }

    @SubscribeEvent
    fun createDataPackRegistries(event: DataPackRegistryEvent.NewRegistry) {
        event.dataPackRegistry(
            WitcheryRitualRegistry.RITUAL_KEY,
            WitcheryRitualRegistry.CODEC,
            WitcheryRitualRegistry.CODEC
        )
    }

    @SubscribeEvent
    fun modifyExistingTabs(event: BuildCreativeModeTabContentsEvent) {
        WitcheryCreativeModeTabs.modifyExistingTabs(event.tab, event)
    }
}
