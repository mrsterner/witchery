package dev.sterner.witchery.neoforge

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.screen.AltarScreen
import dev.sterner.witchery.client.screen.DistilleryScreen
import dev.sterner.witchery.client.screen.OvenScreen
import dev.sterner.witchery.client.screen.SpinningWheelScreen
import dev.sterner.witchery.integration.jei.WitcheryJeiPlugin
import dev.sterner.witchery.neoforge.event.WitcheryNeoForgeClientEvent
import dev.sterner.witchery.neoforge.event.WitcheryNeoForgeEvent
import dev.sterner.witchery.platform.neoforge.WitcheryAttributesImpl
import dev.sterner.witchery.platform.neoforge.WitcheryFluidHandlerNeoForge
import dev.sterner.witchery.platform.neoforge.WitcheryPehkuiImpl
import dev.sterner.witchery.registry.*
import net.minecraft.client.Minecraft
import net.minecraft.core.NonNullList
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ResolvableProfile
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist
import virtuoel.pehkui.api.ScaleData
import virtuoel.pehkui.api.ScaleEventCallback


@Mod(Witchery.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object WitcheryNeoForge {

    val MOB_EFFECTS: DeferredRegister<MobEffect> = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Witchery.MODID)

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

        kotlin.jvm.internal.Reflection.typeOf(WitcheryJeiPlugin::class.java)

        DATA_SERIALIZER_REGISTER.register(MOD_BUS)
        MOB_EFFECTS.register(MOD_BUS)
        WitcheryAttributesImpl.attributes.register()

        NeoForge.EVENT_BUS.register(WitcheryNeoForgeEvent)

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
        WitcheryPehkuiImpl.getGrowing().scaleChangedEvent.add(ScaleEventCallback { ev: ScaleData ->
            val e: Entity? = ev.entity
            val g: Boolean = e?.onGround() ?: false
            e?.refreshDimensions()
            e?.setOnGround(g)
            WitcheryPehkuiImpl.getGrowing().getScaleData(e).markForSync(true)
        })

        WitcheryPehkuiImpl.getShrinking().scaleChangedEvent.add(ScaleEventCallback { ev: ScaleData ->
            val e: Entity? = ev.entity
            val g: Boolean = e?.onGround() ?: false
            e?.refreshDimensions()
            e?.setOnGround(g)
            WitcheryPehkuiImpl.getShrinking().getScaleData(e).markForSync(true)
        })
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
    fun onRegisterCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            WitcheryBlockEntityTypes.CAULDRON.get()
        ) { be, _ ->
            WitcheryFluidHandlerNeoForge(be.fluidTank)
        }
    }

    @SubscribeEvent
    fun modifyAttributes(event: EntityAttributeModificationEvent) {
        event.add(EntityType.PLAYER, WitcheryAttributesImpl.VAMPIRE_BAT_FORM_DURATION)
        event.add(EntityType.PLAYER, WitcheryAttributesImpl.VAMPIRE_SUN_RESISTANCE)
        event.add(EntityType.PLAYER, WitcheryAttributesImpl.VAMPIRE_DRINK_SPEED)
    }
}


