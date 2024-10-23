package dev.sterner.witchery.neoforge.event

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.*
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.client.renderer.*
import dev.sterner.witchery.neoforge.client.BroomBlockEntityWithoutLevelRenderer
import dev.sterner.witchery.neoforge.client.SpinningWheelBlockEntityWithoutLevelRenderer
import dev.sterner.witchery.neoforge.client.WitcheryBlockEntityWithoutLevelRendererInstance
import dev.sterner.witchery.neoforge.item.WitchesRobesItemNeoForge
import dev.sterner.witchery.registry.WitcheryCreativeModeTabs
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryItems.BABA_YAGAS_HAT
import dev.sterner.witchery.registry.WitcheryItems.WITCHES_HAT
import dev.sterner.witchery.registry.WitcheryItems.WITCHES_ROBES
import dev.sterner.witchery.registry.WitcheryItems.WITCHES_SLIPPERS
import dev.sterner.witchery.registry.WitcheryParticleTypes
import net.minecraft.client.model.BoatModel
import net.minecraft.client.renderer.entity.BoatRenderer
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.LootTableLoadEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks

@EventBusSubscriber(value = arrayOf(Dist.CLIENT), bus = EventBusSubscriber.Bus.MOD)
object WitcheryNeoForgeClientEvents {

    fun onEntityRendererRegistry(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(WitcheryEntityTypes.CUSTOM_BOAT.get()) { context -> BoatRenderer(context, false) }
        event.registerEntityRenderer(WitcheryEntityTypes.CUSTOM_CHEST_BOAT.get()) { context -> BoatRenderer(context, true) }

        event.registerEntityRenderer(
            WitcheryEntityTypes.BROOM.get(),
            ::BroomEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.IMP.get(),
            ::ImpEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.MANDRAKE.get(),
            ::MandrakeEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.OWL.get(),
            ::OwlEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.FLOATING_ITEM.get(),
            ::FloatingItemEntityRenderer
        )
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
            MandrakeEntityModel.LAYER_LOCATION) { MandrakeEntityModel.createBodyLayer() }


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

    @SubscribeEvent
    fun registerParticle(event: RegisterParticleProvidersEvent){
        event.registerSpriteSet(WitcheryParticleTypes.COLOR_BUBBLE.get()){ o ->
            ColorBubbleParticle.Provider(o)
        }
    }


    fun registerClientExtensions(event: RegisterClientExtensionsEvent) {
        event.registerItem(WitchesRobesItemNeoForge.ArmorRender.INSTANCE, WITCHES_ROBES.get(), WITCHES_HAT.get(), WITCHES_SLIPPERS.get(), BABA_YAGAS_HAT.get())

        event.registerItem(WitcheryBlockEntityWithoutLevelRendererInstance(SpinningWheelBlockEntityWithoutLevelRenderer()), WitcheryItems.SPINNING_WHEEL.get())
        event.registerItem(WitcheryBlockEntityWithoutLevelRendererInstance(BroomBlockEntityWithoutLevelRenderer()), WitcheryItems.BROOM.get())

    }
}