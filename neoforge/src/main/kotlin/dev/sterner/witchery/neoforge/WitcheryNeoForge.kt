package dev.sterner.witchery.neoforge

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry
import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.*
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.client.screen.AltarScreen
import dev.sterner.witchery.client.screen.DistilleryScreen
import dev.sterner.witchery.client.screen.OvenScreen
import dev.sterner.witchery.client.screen.SpinningWheelScreen
import dev.sterner.witchery.entity.OwlEntity
import dev.sterner.witchery.neoforge.client.SWISTER
import dev.sterner.witchery.neoforge.client.SWISTERInstance
import dev.sterner.witchery.neoforge.event.WitcheryNeoForgeEvents
import dev.sterner.witchery.neoforge.item.WitchesRobesItemNeoForge
import dev.sterner.witchery.platform.AltarDataAttachment
import dev.sterner.witchery.platform.MutandisDataAttachment
import dev.sterner.witchery.platform.infusion.InfusionData
import dev.sterner.witchery.platform.infusion.LightInfusionData
import dev.sterner.witchery.registry.*
import dev.sterner.witchery.registry.WitcheryItems.BABA_YAGAS_HAT
import dev.sterner.witchery.registry.WitcheryItems.WITCHES_HAT
import dev.sterner.witchery.registry.WitcheryItems.WITCHES_ROBES
import dev.sterner.witchery.registry.WitcheryItems.WITCHES_SLIPPERS
import net.minecraft.client.Minecraft
import net.minecraft.client.model.BoatModel
import net.minecraft.client.renderer.entity.BoatRenderer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
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
                MOD_BUS.addListener(::onEntityRendererRegistry)
                MOD_BUS.addListener(::registerClientExtensions)
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

    private fun onEntityRendererRegistry(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(WitcheryEntityTypes.CUSTOM_BOAT.get()) { context -> BoatRenderer(context, false) }
        event.registerEntityRenderer(WitcheryEntityTypes.CUSTOM_CHEST_BOAT.get()) { context -> BoatRenderer(context, true) }
    }

    @SubscribeEvent
    fun registerParticle(event: RegisterParticleProvidersEvent){
        event.registerSpriteSet(WitcheryParticleTypes.COLOR_BUBBLE.get()){ o ->
            ColorBubbleParticle.Provider(o)
        }
    }

    @SubscribeEvent
    private fun registerLayerDefinitions(event: EntityRenderersEvent.RegisterLayerDefinitions) {
        event.registerLayerDefinition(BoatModels.ROWAN_BOAT_LAYER, BoatModel::createBodyModel)
        event.registerLayerDefinition(BoatModels.ALDER_BOAT_LAYER, BoatModel::createBodyModel)
        event.registerLayerDefinition(BoatModels.HAWTHORN_BOAT_LAYER, BoatModel::createBodyModel)
        event.registerLayerDefinition(BoatModels.ROWAN_CHEST_BOAT_LAYER, BoatModel::createBodyModel)
        event.registerLayerDefinition(BoatModels.ALDER_CHEST_BOAT_LAYER, BoatModel::createBodyModel)
        event.registerLayerDefinition(BoatModels.HAWTHORN_CHEST_BOAT_LAYER, BoatModel::createBodyModel)

        event.registerLayerDefinition(
            AltarBlockEntityModel.LAYER_LOCATION,
            AltarBlockEntityModel::createBodyLayer)
        event.registerLayerDefinition(
            AltarClothBlockEntityModel.LAYER_LOCATION,
            AltarClothBlockEntityModel::createBodyLayer)
        event.registerLayerDefinition(
            JarModel.LAYER_LOCATION,
            JarModel::createBodyLayer)
        event.registerLayerDefinition(
            WitchesRobesModel.LAYER_LOCATION,
            WitchesRobesModel::createBodyLayer)
        event.registerLayerDefinition(
            SpinningWheelWheelBlockEntityModel.LAYER_LOCATION,
            SpinningWheelWheelBlockEntityModel::createBodyLayer)
        event.registerLayerDefinition(
            SpinningWheelBlockEntityModel.LAYER_LOCATION,
            SpinningWheelBlockEntityModel::createBodyLayer)
        event.registerLayerDefinition(
            DistilleryGemModel.LAYER_LOCATION,
            DistilleryGemModel::createBodyLayer)


        event.registerLayerDefinition(
            ImpEntityModel.LAYER_LOCATION,
            ImpEntityModel::createBodyLayer)
        event.registerLayerDefinition(
            OwlEntityModel.LAYER_LOCATION,
            OwlEntityModel::createBodyLayer)
        event.registerLayerDefinition(
            BroomEntityModel.LAYER_LOCATION,
            BroomEntityModel::createBodyLayer)
    }

    private fun registerClientExtensions(event: RegisterClientExtensionsEvent) {
        event.registerItem(WitchesRobesItemNeoForge.ArmorRender.INSTANCE, WITCHES_ROBES.get(), WITCHES_HAT.get(), WITCHES_SLIPPERS.get(), BABA_YAGAS_HAT.get())

        event.registerItem(SWISTERInstance(SWISTER()), WitcheryItems.SPINNING_WHEEL.get())

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
