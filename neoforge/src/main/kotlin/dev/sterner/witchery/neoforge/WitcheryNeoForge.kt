package dev.sterner.witchery.neoforge

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.screen.AltarScreen
import dev.sterner.witchery.client.screen.DistilleryScreen
import dev.sterner.witchery.client.screen.OvenScreen
import dev.sterner.witchery.client.screen.SpinningWheelScreen
import dev.sterner.witchery.neoforge.event.WitcheryNeoForgeClientEvents
import dev.sterner.witchery.neoforge.event.WitcheryNeoForgeEvents
import dev.sterner.witchery.platform.AltarDataAttachment
import dev.sterner.witchery.platform.MutandisDataAttachment
import dev.sterner.witchery.platform.infusion.InfusionData
import dev.sterner.witchery.platform.infusion.LightInfusionData
import dev.sterner.witchery.registry.*
import net.minecraft.client.Minecraft
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.registries.DataPackRegistryEvent
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist
import java.util.function.Supplier


@Mod(Witchery.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object WitcheryNeoForge {

    val ATTACHMENT_TYPES: DeferredRegister<AttachmentType<*>> =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Witchery.MODID)

    @JvmStatic
    val MUTANDIS_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<MutandisDataAttachment.MutandisDataCodec>> = ATTACHMENT_TYPES.register(
        "mutandis_level_data",
        Supplier {
            AttachmentType.builder(Supplier { MutandisDataAttachment.MutandisDataCodec() })
                .serialize(MutandisDataAttachment.MutandisDataCodec.CODEC)
                .build()
        }
    )

    @JvmStatic
    val ALTAR_LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<AltarDataAttachment.AltarDataCodec>> = ATTACHMENT_TYPES.register(
        "altar_level_data",
        Supplier {
            AttachmentType.builder(Supplier { AltarDataAttachment.AltarDataCodec() })
                .serialize(AltarDataAttachment.AltarDataCodec.CODEC)
                .build()
        }
    )

    @JvmStatic
    val INFUSION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<InfusionData>> = ATTACHMENT_TYPES.register(
        "infusion_player_data",
        Supplier {
            AttachmentType.builder(Supplier { InfusionData() })
                .serialize(InfusionData.CODEC)
                .build()
        }
    )

    @JvmStatic
    val LIGHT_INFUSION_PLAYER_DATA_ATTACHMENT: Supplier<AttachmentType<LightInfusionData>> = ATTACHMENT_TYPES.register(
        "light_infusion_player_data",
        Supplier {
            AttachmentType.builder(Supplier { LightInfusionData() })
                .serialize(LightInfusionData.CODEC)
                .build()
        }
    )


    init {
        ATTACHMENT_TYPES.register(MOD_BUS)
        Witchery.init()

        runForDist(
            clientTarget = {
                MOD_BUS.addListener(::onClientSetup)
                MOD_BUS.addListener(WitcheryNeoForgeClientEvents::onEntityRendererRegistry)
                MOD_BUS.addListener(WitcheryNeoForgeClientEvents::registerClientExtensions)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(::onServerSetup)
                MOD_BUS.addListener(::onLoadComplete)
                FORGE_BUS.addListener(WitcheryNeoForgeEvents::modifyLootTable)
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
        event.dataPackRegistry(WitcheryRitualRegistry.RITUAL_KEY, WitcheryRitualRegistry.CODEC, WitcheryRitualRegistry.CODEC)
    }
}
