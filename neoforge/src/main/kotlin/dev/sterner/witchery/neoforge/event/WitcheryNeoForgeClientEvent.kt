package dev.sterner.witchery.neoforge.event

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.client.BloodPoolComponent
import dev.sterner.witchery.client.model.*
import dev.sterner.witchery.client.model.poppet.ArmorPoppetModel
import dev.sterner.witchery.client.model.poppet.HungerPoppetModel
import dev.sterner.witchery.client.model.poppet.VampiricPoppetModel
import dev.sterner.witchery.client.model.poppet.VoodooPoppetModel
import dev.sterner.witchery.client.particle.BloodSplashParticle
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.client.particle.SneezeParticle
import dev.sterner.witchery.client.particle.ZzzParticle
import dev.sterner.witchery.client.renderer.*
import dev.sterner.witchery.neoforge.client.*
import dev.sterner.witchery.neoforge.item.HunterArmorItemNeoForge
import dev.sterner.witchery.neoforge.item.VampireArmorItemNeoForge
import dev.sterner.witchery.neoforge.item.WitchesRobesItemNeoForge
import dev.sterner.witchery.registry.*
import dev.sterner.witchery.registry.WitcheryItems.BABA_YAGAS_HAT
import dev.sterner.witchery.registry.WitcheryItems.DRESS_COAT
import dev.sterner.witchery.registry.WitcheryItems.HUNTER_BOOTS
import dev.sterner.witchery.registry.WitcheryItems.HUNTER_CHESTPLATE
import dev.sterner.witchery.registry.WitcheryItems.HUNTER_HELMET
import dev.sterner.witchery.registry.WitcheryItems.HUNTER_LEGGINGS
import dev.sterner.witchery.registry.WitcheryItems.OXFORD_BOOTS
import dev.sterner.witchery.registry.WitcheryItems.TOP_HAT
import dev.sterner.witchery.registry.WitcheryItems.TROUSERS
import dev.sterner.witchery.registry.WitcheryItems.WITCHES_HAT
import dev.sterner.witchery.registry.WitcheryItems.WITCHES_ROBES
import dev.sterner.witchery.registry.WitcheryItems.WITCHES_SLIPPERS
import net.minecraft.client.model.BoatModel
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.entity.BoatRenderer
import net.minecraft.client.renderer.entity.ThrownItemRenderer
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.*
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import org.jetbrains.annotations.NotNull


object WitcheryNeoForgeClientEvent {

    @SubscribeEvent
    fun addEnchantGlint(event: RegisterRenderBuffersEvent) {
        //TODO event.registerRenderBuffer(WitcheryRenderTypes.GLINT.apply(Witchery.id("textures/misc/all_black.png")))
        //event.registerRenderBuffer(WitcheryRenderTypes.GLINT_DIRECT.apply(Witchery.id("textures/misc/all_black.png")))
    }

    @SubscribeEvent
    private fun registerTooltip(event: RegisterClientTooltipComponentFactoriesEvent) {
        event.register(BloodPoolComponent::class.java, BloodPoolComponent::getClientTooltipComponent)
    }

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
            WitcheryEntityTypes.BANSHEE.get(),
            ::BansheeEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.SPECTRE.get(),
            ::SpectreEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.SLEEPING_PLAYER.get(),
            ::SleepingPlayerEntityRenderer
        )

        event.registerEntityRenderer(
            WitcheryEntityTypes.VAMPIRE.get(),
            ::VampireEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.WEREWOLF.get(),
            ::WerewolfEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.NIGHTMARE.get(),
            ::NightmareEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.LILITH.get(),
            ::LilithEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.ELLE.get(),
            ::ElleEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.PARASITIC_LOUSE.get(),
            ::ParasiticLouseEntityRenderer
        )
        event.registerEntityRenderer(
            WitcheryEntityTypes.INSANITY.get(),
            ::InsanityEntityRenderer
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
        event.registerLayerDefinition(
            SpiritPortalBlockEntityModel.LAYER_LOCATION,
            SpiritPortalBlockEntityModel::createBodyLayer
        )
        event.registerLayerDefinition(SpiritPortalPortalModel.LAYER_LOCATION, SpiritPortalPortalModel::createBodyLayer)
        event.registerLayerDefinition(WerewolfAltarModel.LAYER_LOCATION, WerewolfAltarModel::createBodyLayer)
        event.registerLayerDefinition(CoffinModel.LAYER_LOCATION, CoffinModel::createBodyLayer)
        event.registerLayerDefinition(BearTrapModel.LAYER_LOCATION, BearTrapModel::createBodyLayer)
        event.registerLayerDefinition(ChainModel.LAYER_LOCATION, ChainModel::createBodyLayer)
        event.registerLayerDefinition(
            AltarClothBlockEntityModel.LAYER_LOCATION,
            AltarClothBlockEntityModel::createBodyLayer
        )
        event.registerLayerDefinition(JarModel.LAYER_LOCATION, JarModel::createBodyLayer)
        event.registerLayerDefinition(ArmorPoppetModel.LAYER_LOCATION, ArmorPoppetModel::createBodyLayer)
        event.registerLayerDefinition(HungerPoppetModel.LAYER_LOCATION, HungerPoppetModel::createBodyLayer)
        event.registerLayerDefinition(VampiricPoppetModel.LAYER_LOCATION, VampiricPoppetModel::createBodyLayer)
        event.registerLayerDefinition(VoodooPoppetModel.LAYER_LOCATION, VoodooPoppetModel::createBodyLayer)
        event.registerLayerDefinition(WitchesRobesModel.LAYER_LOCATION, WitchesRobesModel::createBodyLayer)
        event.registerLayerDefinition(
            SpinningWheelWheelBlockEntityModel.LAYER_LOCATION,
            SpinningWheelWheelBlockEntityModel::createBodyLayer
        )
        event.registerLayerDefinition(
            SpinningWheelBlockEntityModel.LAYER_LOCATION,
            SpinningWheelBlockEntityModel::createBodyLayer
        )
        event.registerLayerDefinition(DistilleryGemModel.LAYER_LOCATION, DistilleryGemModel::createBodyLayer)
        event.registerLayerDefinition(GlassContainerModel.LAYER_LOCATION, GlassContainerModel::createBodyLayer)
        event.registerLayerDefinition(MandrakeEntityModel.LAYER_LOCATION, MandrakeEntityModel::createBodyLayer)
        event.registerLayerDefinition(ImpEntityModel.LAYER_LOCATION, ImpEntityModel::createBodyLayer)
        event.registerLayerDefinition(OwlEntityModel.LAYER_LOCATION, OwlEntityModel::createBodyLayer)
        event.registerLayerDefinition(BroomEntityModel.LAYER_LOCATION, BroomEntityModel::createBodyLayer)
        event.registerLayerDefinition(HunterArmorModel.LAYER_LOCATION, HunterArmorModel::createBodyLayer)
        event.registerLayerDefinition(
            DreamWeaverBlockEntityModel.LAYER_LOCATION,
            DreamWeaverBlockEntityModel::createBodyLayer
        )
        event.registerLayerDefinition(DemonEntityModel.LAYER_LOCATION, DemonEntityModel::createBodyLayer)
        event.registerLayerDefinition(EntEntityModel.LAYER_LOCATION, EntEntityModel::createBodyLayer)
        event.registerLayerDefinition(BansheeEntityModel.LAYER_LOCATION, BansheeEntityModel::createBodyLayer)
        event.registerLayerDefinition(SpectreEntityModel.LAYER_LOCATION, SpectreEntityModel::createBodyLayer)

        event.registerLayerDefinition(VampireEntityModel.LAYER_LOCATION, VampireEntityModel::createBodyLayer)
        event.registerLayerDefinition(WerewolfEntityModel.LAYER_LOCATION, WerewolfEntityModel::createBodyLayer)
        event.registerLayerDefinition(NightmareEntityModel.LAYER_LOCATION, NightmareEntityModel::createBodyLayer)
        event.registerLayerDefinition(LilithEntityModel.LAYER_LOCATION, LilithEntityModel::createBodyLayer)
        event.registerLayerDefinition(
            ParasiticLouseEntityModel.LAYER_LOCATION,
            ParasiticLouseEntityModel::createBodyLayer
        )
    }

    @SubscribeEvent
    fun registerParticle(event: RegisterParticleProvidersEvent) {
        event.registerSpriteSet(WitcheryParticleTypes.COLOR_BUBBLE.get()) { o ->
            ColorBubbleParticle.Provider(o)
        }
        event.registerSpriteSet(WitcheryParticleTypes.ZZZ.get()) { o ->
            ZzzParticle.Provider(o)
        }
        event.registerSpriteSet(WitcheryParticleTypes.SNEEZE.get()) { o ->
            SneezeParticle.SneezeProvider(o)
        }
        event.registerSpriteSet(WitcheryParticleTypes.SPLASHING_BLOOD.get()) { o ->
            BloodSplashParticle.ParticleFactory(o)
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
            VampireArmorItemNeoForge.ArmorRender.INSTANCE,
            TOP_HAT.get(),
            DRESS_COAT.get(),
            OXFORD_BOOTS.get(),
            TROUSERS.get()
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
            WitcheryBlockEntityWithoutLevelRendererInstance(WerewolfAltarBlockEntityWithoutLevelRenderer()),
            WitcheryItems.WEREWOLF_ALTAR.get()
        )
        event.registerItem(
            WitcheryBlockEntityWithoutLevelRendererInstance(BearTrapBlockEntityWithoutLevelRenderer()),
            WitcheryItems.BEAR_TRAP.get()
        )
        event.registerItem(
            WitcheryBlockEntityWithoutLevelRendererInstance(CoffinBlockEntityWithoutLevelRenderer()),
            WitcheryItems.COFFIN.get()
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

    @SubscribeEvent
    fun registerShader(event: RegisterShadersEvent) {
        event.registerShader(
            ShaderInstance(event.resourceProvider, Witchery.id("spirit_portal"), DefaultVertexFormat.NEW_ENTITY)
        ) { shaderInstance ->
            WitcheryShaders.spiritPortal = shaderInstance
        }
        event.registerShader(
            ShaderInstance(event.resourceProvider, Witchery.id("spirit_chain"), DefaultVertexFormat.NEW_ENTITY)
        ) { shaderInstance ->
            WitcheryShaders.spirit_chain = shaderInstance
        }
        event.registerShader(
            ShaderInstance(event.resourceProvider, Witchery.id("soul_chain"), DefaultVertexFormat.NEW_ENTITY)
        ) { shaderInstance ->
            WitcheryShaders.soul_chain = shaderInstance
        }
        event.registerShader(
            ShaderInstance(event.resourceProvider, Witchery.id("ghost"), DefaultVertexFormat.NEW_ENTITY)
        ) { shaderInstance ->
            WitcheryShaders.ghost = shaderInstance
        }
    }
}