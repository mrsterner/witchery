package dev.sterner.witchery.neoforge.event

import dev.sterner.witchery.client.model.*
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.client.particle.ZzzParticle
import dev.sterner.witchery.client.renderer.*
import dev.sterner.witchery.neoforge.client.BroomBlockEntityWithoutLevelRenderer
import dev.sterner.witchery.neoforge.client.DreamWeaverBlockEntityWithoutLevelRenderer
import dev.sterner.witchery.neoforge.client.SpinningWheelBlockEntityWithoutLevelRenderer
import dev.sterner.witchery.neoforge.client.WitcheryBlockEntityWithoutLevelRendererInstance
import dev.sterner.witchery.neoforge.item.HunterArmorItemNeoForge
import dev.sterner.witchery.neoforge.item.WitchesRobesItemNeoForge
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryFluids
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryItems.BABA_YAGAS_HAT
import dev.sterner.witchery.registry.WitcheryItems.HUNTER_BOOTS
import dev.sterner.witchery.registry.WitcheryItems.HUNTER_CHESTPLATE
import dev.sterner.witchery.registry.WitcheryItems.HUNTER_HELMET
import dev.sterner.witchery.registry.WitcheryItems.HUNTER_LEGGINGS
import dev.sterner.witchery.registry.WitcheryItems.WITCHES_HAT
import dev.sterner.witchery.registry.WitcheryItems.WITCHES_ROBES
import dev.sterner.witchery.registry.WitcheryItems.WITCHES_SLIPPERS
import dev.sterner.witchery.registry.WitcheryParticleTypes
import net.minecraft.client.model.BoatModel
import net.minecraft.client.renderer.entity.BoatRenderer
import net.minecraft.client.renderer.entity.ThrownItemRenderer
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import org.jetbrains.annotations.NotNull

object WitcheryNeoForgeClientEvent {

    @SubscribeEvent
    fun onEntityRendererRegistry(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(WitcheryEntityTypes.CUSTOM_BOAT.get()) { context -> BoatRenderer(context, false) }
        event.registerEntityRenderer(WitcheryEntityTypes.CUSTOM_CHEST_BOAT.get()) { context ->
            BoatRenderer(
                context,
                true
            )
        }

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
        event.registerEntityRenderer(
            WitcheryEntityTypes.THROWN_BREW.get(),
            ::ThrownItemRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.DEMON.get(),
            ::DemonEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.ENT.get(),
            ::EntEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.SLEEPING_PLAYER.get(),
            ::SleepingPlayerEntityRenderer
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
        event.registerLayerDefinition(AltarBlockEntityModel.LAYER_LOCATION, AltarBlockEntityModel::createBodyLayer)
        event.registerLayerDefinition(AltarClothBlockEntityModel.LAYER_LOCATION, AltarClothBlockEntityModel::createBodyLayer)
        event.registerLayerDefinition(JarModel.LAYER_LOCATION, JarModel::createBodyLayer)
        event.registerLayerDefinition(PoppetModel.LAYER_LOCATION, PoppetModel::createBodyLayer)
        event.registerLayerDefinition(WitchesRobesModel.LAYER_LOCATION, WitchesRobesModel::createBodyLayer)
        event.registerLayerDefinition(SpinningWheelWheelBlockEntityModel.LAYER_LOCATION, SpinningWheelWheelBlockEntityModel::createBodyLayer)
        event.registerLayerDefinition(SpinningWheelBlockEntityModel.LAYER_LOCATION, SpinningWheelBlockEntityModel::createBodyLayer)
        event.registerLayerDefinition(DistilleryGemModel.LAYER_LOCATION, DistilleryGemModel::createBodyLayer)
        event.registerLayerDefinition(MandrakeEntityModel.LAYER_LOCATION, MandrakeEntityModel::createBodyLayer)
        event.registerLayerDefinition(ImpEntityModel.LAYER_LOCATION, ImpEntityModel::createBodyLayer)
        event.registerLayerDefinition(OwlEntityModel.LAYER_LOCATION, OwlEntityModel::createBodyLayer)
        event.registerLayerDefinition(BroomEntityModel.LAYER_LOCATION, BroomEntityModel::createBodyLayer)
        event.registerLayerDefinition(HunterArmorModel.LAYER_LOCATION, HunterArmorModel::createBodyLayer)
        event.registerLayerDefinition(DreamWeaverBlockEntityModel.LAYER_LOCATION, DreamWeaverBlockEntityModel::createBodyLayer)
        event.registerLayerDefinition(DemonEntityModel.LAYER_LOCATION, DemonEntityModel::createBodyLayer)
        event.registerLayerDefinition(EntEntityModel.LAYER_LOCATION, EntEntityModel::createBodyLayer)
    }

    @SubscribeEvent
    fun registerParticle(event: RegisterParticleProvidersEvent) {
        event.registerSpriteSet(WitcheryParticleTypes.COLOR_BUBBLE.get()) { o ->
            ColorBubbleParticle.Provider(o)
        }
        event.registerSpriteSet(WitcheryParticleTypes.ZZZ.get()) { o ->
            ZzzParticle.Provider(o)
        }
    }

    @SubscribeEvent
    fun registerClientExtensions(event: RegisterClientExtensionsEvent) {
        event.registerItem(
            WitchesRobesItemNeoForge.ArmorRender.INSTANCE,
            WITCHES_ROBES.get(),
            WITCHES_HAT.get(),
            WITCHES_SLIPPERS.get(),
            BABA_YAGAS_HAT.get()
        )
        event.registerItem(
            HunterArmorItemNeoForge.ArmorRender.INSTANCE,
            HUNTER_HELMET.get(),
            HUNTER_CHESTPLATE.get(),
            HUNTER_LEGGINGS.get(),
            HUNTER_BOOTS.get()
        )

        event.registerItem(
            WitcheryBlockEntityWithoutLevelRendererInstance(SpinningWheelBlockEntityWithoutLevelRenderer()),
            WitcheryItems.SPINNING_WHEEL.get()
        )
        event.registerItem(
            WitcheryBlockEntityWithoutLevelRendererInstance(BroomBlockEntityWithoutLevelRenderer()),
            WitcheryItems.BROOM.get()
        )
        event.registerItem(
            WitcheryBlockEntityWithoutLevelRendererInstance(DreamWeaverBlockEntityWithoutLevelRenderer()),
            WitcheryItems.DREAM_WEAVER_OF_FLEET_FOOT.get()
        )
        event.registerItem(
            WitcheryBlockEntityWithoutLevelRendererInstance(DreamWeaverBlockEntityWithoutLevelRenderer()),
            WitcheryItems.DREAM_WEAVER_OF_FASTING.get()
        )
        event.registerItem(
            WitcheryBlockEntityWithoutLevelRendererInstance(DreamWeaverBlockEntityWithoutLevelRenderer()),
            WitcheryItems.DREAM_WEAVER_OF_INTENSITY.get()
        )
        event.registerItem(
            WitcheryBlockEntityWithoutLevelRendererInstance(DreamWeaverBlockEntityWithoutLevelRenderer()),
            WitcheryItems.DREAM_WEAVER_OF_NIGHTMARES.get()
        )
        event.registerItem(
            WitcheryBlockEntityWithoutLevelRendererInstance(DreamWeaverBlockEntityWithoutLevelRenderer()),
            WitcheryItems.DREAM_WEAVER_OF_IRON_ARM.get()
        )
        event.registerItem(
            WitcheryBlockEntityWithoutLevelRendererInstance(DreamWeaverBlockEntityWithoutLevelRenderer()),
            WitcheryItems.DREAM_WEAVER.get()
        )

        @Suppress("UNRESOLVED_REFERENCE")
        WitcheryFluids.FLUIDS_INFOS.forEach { attributes ->
            event.registerFluidType(object : IClientFluidTypeExtensions {
                @NotNull
                override fun getStillTexture(): ResourceLocation {
                    return attributes.sourceTexture
                }

                @NotNull
                override fun getFlowingTexture(): ResourceLocation {
                    return attributes.flowingTexture
                }
            }, attributes.flowingFluid.getFluidType())
        }
    }
}