package dev.sterner.witchery

import dev.sterner.witchery.Witchery.Companion.MODID
import dev.sterner.witchery.client.model.AltarBlockEntityModel
import dev.sterner.witchery.client.model.AltarClothBlockEntityModel
import dev.sterner.witchery.client.model.BabaYagaEntityModel
import dev.sterner.witchery.client.model.BansheeEntityModel
import dev.sterner.witchery.client.model.BearTrapModel
import dev.sterner.witchery.client.model.BloodCrucibleModel
import dev.sterner.witchery.client.model.BoatModels
import dev.sterner.witchery.client.model.BroomEntityModel
import dev.sterner.witchery.client.model.ChainModel
import dev.sterner.witchery.client.model.CoffinModel
import dev.sterner.witchery.client.model.DeathEntityModel
import dev.sterner.witchery.client.model.DemonEntityModel
import dev.sterner.witchery.client.model.DistilleryGemModel
import dev.sterner.witchery.client.model.DreamWeaverBlockEntityModel
import dev.sterner.witchery.client.model.EntEntityModel
import dev.sterner.witchery.client.model.GlassContainerModel
import dev.sterner.witchery.client.model.HornedHuntsmanModel
import dev.sterner.witchery.client.model.HunterArmorModel
import dev.sterner.witchery.client.model.HuntsmanSpearModel
import dev.sterner.witchery.client.model.ImpEntityModel
import dev.sterner.witchery.client.model.JarModel
import dev.sterner.witchery.client.model.LilithEntityModel
import dev.sterner.witchery.client.model.MandrakeEntityModel
import dev.sterner.witchery.client.model.MushroomLogModel
import dev.sterner.witchery.client.model.NightmareEntityModel
import dev.sterner.witchery.client.model.OwlEntityModel
import dev.sterner.witchery.client.model.ParasiticLouseEntityModel
import dev.sterner.witchery.client.model.PhylacteryEtherCoreModel
import dev.sterner.witchery.client.model.PhylacteryEtherModel
import dev.sterner.witchery.client.model.SpectreEntityModel
import dev.sterner.witchery.client.model.SpinningWheelBlockEntityModel
import dev.sterner.witchery.client.model.SpinningWheelWheelBlockEntityModel
import dev.sterner.witchery.client.model.SpiritPortalBlockEntityModel
import dev.sterner.witchery.client.model.SpiritPortalPortalModel
import dev.sterner.witchery.client.model.VampireArmorModel
import dev.sterner.witchery.client.model.VampireEntityModel
import dev.sterner.witchery.client.model.WerewolfAltarModel
import dev.sterner.witchery.client.model.WerewolfEntityModel
import dev.sterner.witchery.client.model.WitchesRobesModel
import dev.sterner.witchery.client.model.poppet.ArmorPoppetModel
import dev.sterner.witchery.client.model.poppet.HungerPoppetModel
import dev.sterner.witchery.client.model.poppet.VampiricPoppetModel
import dev.sterner.witchery.client.model.poppet.VoodooPoppetModel
import dev.sterner.witchery.client.renderer.block.AltarBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.BearTrapBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.BloodCrucibleBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.BrazierBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.CauldronBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.CoffinBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.CritterSnareBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.DistilleryBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.DreamWeaverBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.EffigyBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.GrassperBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.MushroomLogBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.PhylacteryBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.PoppetBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.SacrificialCircleBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.SpinningWheelBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.SpiritPortalBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.SuspiciousGraveyardDirtBlockEntityRenderer
import dev.sterner.witchery.client.renderer.entity.BabaYagaEntityRenderer
import dev.sterner.witchery.client.renderer.entity.BansheeEntityRenderer
import dev.sterner.witchery.client.renderer.entity.BroomEntityRenderer
import dev.sterner.witchery.client.renderer.entity.ChainEntityRenderer
import dev.sterner.witchery.client.renderer.entity.CovenWitchEntityRenderer
import dev.sterner.witchery.client.renderer.entity.DeathEntityRenderer
import dev.sterner.witchery.client.renderer.entity.DemonEntityRenderer
import dev.sterner.witchery.client.renderer.entity.ElleEntityRenderer
import dev.sterner.witchery.client.renderer.entity.EntEntityRenderer
import dev.sterner.witchery.client.renderer.entity.FloatingItemEntityRenderer
import dev.sterner.witchery.client.renderer.entity.HornedHuntsmanEntityRenderer
import dev.sterner.witchery.client.renderer.entity.HuntsmanSpearRenderer
import dev.sterner.witchery.client.renderer.entity.ImpEntityRenderer
import dev.sterner.witchery.client.renderer.entity.InsanityEntityRenderer
import dev.sterner.witchery.client.renderer.entity.LilithEntityRenderer
import dev.sterner.witchery.client.renderer.entity.MandrakeEntityRenderer
import dev.sterner.witchery.client.renderer.entity.NightmareEntityRenderer
import dev.sterner.witchery.client.renderer.entity.OwlEntityRenderer
import dev.sterner.witchery.client.renderer.entity.ParasiticLouseEntityRenderer
import dev.sterner.witchery.client.renderer.entity.SleepingPlayerEntityRenderer
import dev.sterner.witchery.client.renderer.entity.SpectralPigRenderer
import dev.sterner.witchery.client.renderer.entity.SpectreEntityRenderer
import dev.sterner.witchery.client.renderer.entity.VampireEntityRenderer
import dev.sterner.witchery.client.renderer.entity.WerewolfEntityRenderer
import dev.sterner.witchery.client.renderer.without_level.BearTrapBlockEntityWithoutLevelRenderer
import dev.sterner.witchery.client.renderer.without_level.BloodCrucibleBlockEntityWithoutLevelRenderer
import dev.sterner.witchery.client.renderer.without_level.BroomBlockEntityWithoutLevelRenderer
import dev.sterner.witchery.client.renderer.without_level.CoffinBlockEntityWithoutLevelRenderer
import dev.sterner.witchery.client.renderer.without_level.DreamWeaverBlockEntityWithoutLevelRenderer
import dev.sterner.witchery.client.renderer.without_level.HuntsmanSpearBlockEntityWithoutLevelRenderer
import dev.sterner.witchery.client.renderer.without_level.SpinningWheelBlockEntityWithoutLevelRenderer
import dev.sterner.witchery.client.renderer.without_level.WerewolfAltarBlockEntityWithoutLevelRenderer
import dev.sterner.witchery.client.renderer.without_level.WitcheryBlockEntityWithoutLevelRendererInstance
import dev.sterner.witchery.item.WitchesRobesItem
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
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
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import dev.sterner.witchery.api.client.BloodPoolComponent
import dev.sterner.witchery.client.layer.DemonHeadFeatureRenderer
import dev.sterner.witchery.client.particle.BloodSplashParticle
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.client.particle.SneezeParticle
import dev.sterner.witchery.client.particle.ZzzParticle
import dev.sterner.witchery.client.renderer.block.SoulCageBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.WerewolfAltarBlockEntityRenderer
import dev.sterner.witchery.client.screen.AltarScreen
import dev.sterner.witchery.client.screen.DistilleryScreen
import dev.sterner.witchery.client.screen.OvenScreen
import dev.sterner.witchery.client.screen.SpinningWheelScreen
import dev.sterner.witchery.handler.BarkBeltHandler
import dev.sterner.witchery.handler.ManifestationHandler
import dev.sterner.witchery.handler.affliction.AfflictionAbilityHandler
import dev.sterner.witchery.handler.affliction.VampireClientSpecificEventHandler
import dev.sterner.witchery.handler.infusion.InfusionHandler
import dev.sterner.witchery.payload.DismountBroomC2SPayload
import dev.sterner.witchery.registry.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.entity.player.PlayerRenderer
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.*
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.network.PacketDistributor

import net.minecraft.client.model.ChestBoatModel
import net.minecraft.client.renderer.blockentity.HangingSignRenderer
import net.minecraft.client.renderer.blockentity.SignRenderer
import net.minecraft.client.renderer.entity.BoatRenderer
import net.minecraft.client.renderer.entity.NoopRenderer
import net.minecraft.client.renderer.entity.ThrownItemRenderer
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.NeoForge

@Mod(value = MODID, dist = [Dist.CLIENT])
class WitcheryClient(modContainer: ModContainer, modEventBus: IEventBus) {

    init {
        modContainer.registerExtensionPoint(
            IConfigScreenFactory::class.java,
            IConfigScreenFactory(::ConfigurationScreen)
        )

        modEventBus.addListener(::onClientSetup)
        modEventBus.addListener(::onAddLayer)
        modEventBus.addListener(::onRegisterColorHandlers)
        modEventBus.addListener(::onRegisterFluidExtensions)
        modEventBus.addListener(::onTooltipComponentFactories)
        modEventBus.addListener(::bindContainerRenderers)
        modEventBus.addListener(::registerParticle)
        modEventBus.addListener(::registerShader)
        modEventBus.addListener(::registerClientExtensions)
        modEventBus.addListener(::registerModelLayers)
        modEventBus.addListener(::registerEntityRenderers)

        NeoForge.EVENT_BUS.addListener(::onMouseScrolled)
        NeoForge.EVENT_BUS.addListener(::onRenderHud)
        NeoForge.EVENT_BUS.addListener(::onClientTick)
    }

    private fun onClientSetup(event: FMLClientSetupEvent) { }

    private fun onAddLayer(event: EntityRenderersEvent.AddLayers) {
        val skins = event.skins
        for (skin in skins) {
            when (skin) {
                PlayerSkin.Model.SLIM,
                PlayerSkin.Model.WIDE -> {
                    val renderer = event.getRenderer(EntityType.PLAYER)
                    if (renderer is PlayerRenderer) {
                        renderer.addLayer(DemonHeadFeatureRenderer(renderer, event.context))
                    }
                }
            }
        }
    }

    private fun onMouseScrolled(event: InputEvent.MouseScrollingEvent) {
        val bl = AfflictionAbilityHandler.scroll(Minecraft.getInstance(), event.scrollDeltaX, event.scrollDeltaY)
        if (bl) {
            event.isCanceled = true
        }
    }

    private fun onRegisterColorHandlers(event: RegisterColorHandlersEvent.Block) {
        event.register({ _, _, _, _ -> 0xAAFFFF }, WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get())
    }

    private fun onRegisterFluidExtensions(event: RegisterClientExtensionsEvent) {
        event.registerFluidType(object : IClientFluidTypeExtensions {
            override fun getStillTexture() =
                ResourceLocation.fromNamespaceAndPath(Witchery.MODID, "block/flowing_spirit_still")

            override fun getFlowingTexture() =
                ResourceLocation.fromNamespaceAndPath(Witchery.MODID, "block/flowing_spirit_flowing")

            override fun getTintColor() = 0xAAFFFF

            override fun getOverlayTexture() =
                ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_overlay")
        }, WitcheryFluids.FLOWING_SPIRIT_TYPE.get())
    }

    private fun onRenderHud(event: RenderGuiEvent.Post) {
        InfusionHandler.renderInfusionHud(event.guiGraphics, event.partialTick)
        ManifestationHandler.renderHud(event.guiGraphics, event.partialTick)
        VampireClientSpecificEventHandler.renderHud(event.guiGraphics)
        BarkBeltHandler.renderHud(event.guiGraphics, event.partialTick)
    }

    private fun onTooltipComponentFactories(event: RegisterClientTooltipComponentFactoriesEvent) {
        event.register(BloodPoolComponent::class.java, BloodPoolComponent::getClientTooltipComponent)
    }

    private fun onClientTick(event: ClientTickEvent.Post) {
        while (WitcheryKeyMappings.OPEN_ABILITY_SELECTION.consumeClick()) {
            Minecraft.getInstance().player?.let {
                AfflictionAbilityHandler.openSelectionScreen(it)
            }
        }
        while (WitcheryKeyMappings.BROOM_DISMOUNT_KEYMAPPING.consumeClick()) {
            Minecraft.getInstance().player?.stopRiding()
            if (Minecraft.getInstance().player != null) {
                PacketDistributor.sendToServer(DismountBroomC2SPayload())
            }
        }
    }

    private fun bindContainerRenderers(event: RegisterMenuScreensEvent) {
        event.register(WitcheryMenuTypes.OVEN_MENU_TYPE.get(), ::OvenScreen)
        event.register(WitcheryMenuTypes.ALTAR_MENU_TYPE.get(), ::AltarScreen)
        event.register(WitcheryMenuTypes.DISTILLERY_MENU_TYPE.get(), ::DistilleryScreen)
        event.register(WitcheryMenuTypes.SPINNING_WHEEL_MENU_TYPE.get(), ::SpinningWheelScreen)
    }

    private fun registerParticle(event: RegisterParticleProvidersEvent) {
        event.registerSpriteSet(WitcheryParticleTypes.COLOR_BUBBLE.get()) { ColorBubbleParticle.Provider(it) }
        event.registerSpriteSet(WitcheryParticleTypes.ZZZ.get()) { ZzzParticle.Provider(it) }
        event.registerSpriteSet(WitcheryParticleTypes.SNEEZE.get()) { SneezeParticle.SneezeProvider(it) }
        event.registerSpriteSet(WitcheryParticleTypes.SPLASHING_BLOOD.get()) { BloodSplashParticle.ParticleFactory(it) }
    }

    private fun registerShader(event: RegisterShadersEvent) {
        event.registerShader(
            ShaderInstance(event.resourceProvider, Witchery.id("spirit_portal"), DefaultVertexFormat.NEW_ENTITY)
        ) { WitcheryShaders.spiritPortal = it }
        event.registerShader(
            ShaderInstance(event.resourceProvider, Witchery.id("spirit_cage"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        ) { WitcheryShaders.soulLantern = it }
        event.registerShader(
            ShaderInstance(event.resourceProvider, Witchery.id("spirit_chain"), DefaultVertexFormat.NEW_ENTITY)
        ) { WitcheryShaders.spirit_chain = it }
        event.registerShader(
            ShaderInstance(event.resourceProvider, Witchery.id("soul_chain"), DefaultVertexFormat.NEW_ENTITY)
        ) { WitcheryShaders.soul_chain = it }
        event.registerShader(
            ShaderInstance(event.resourceProvider, Witchery.id("ghost"), DefaultVertexFormat.NEW_ENTITY)
        ) { WitcheryShaders.ghost = it }
        event.registerShader(
            ShaderInstance(event.resourceProvider, Witchery.id("ether"), DefaultVertexFormat.NEW_ENTITY)
        ) { WitcheryShaders.ether = it }
    }

    fun registerClientExtensions(event: RegisterClientExtensionsEvent) {
        event.registerItem(
            WitchesRobesItem.ArmorRender.INSTANCE,
            WITCHES_ROBES.get(),
            WITCHES_HAT.get(),
            WITCHES_SLIPPERS.get(),
            BABA_YAGAS_HAT.get()
        )
        event.registerItem(
            WitchesRobesItem.ArmorRender.INSTANCE,
            HUNTER_HELMET.get(),
            HUNTER_CHESTPLATE.get(),
            HUNTER_LEGGINGS.get(),
            HUNTER_BOOTS.get()
        )
        event.registerItem(
            WitchesRobesItem.ArmorRender.INSTANCE,
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
            WitcheryBlockEntityWithoutLevelRendererInstance(HuntsmanSpearBlockEntityWithoutLevelRenderer()),
            WitcheryItems.HUNTSMAN_SPEAR.get()
        )
        event.registerItem(
            WitcheryBlockEntityWithoutLevelRendererInstance(WerewolfAltarBlockEntityWithoutLevelRenderer()),
            WitcheryItems.WEREWOLF_ALTAR.get()
        )
        event.registerItem(
            WitcheryBlockEntityWithoutLevelRendererInstance(BloodCrucibleBlockEntityWithoutLevelRenderer()),
            WitcheryItems.BLOOD_CRUCIBLE.get()
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

    }

    fun registerModelLayers(event: EntityRenderersEvent.RegisterLayerDefinitions) {

        event.registerLayerDefinition(AltarClothBlockEntityModel.LAYER_LOCATION) { AltarClothBlockEntityModel.createBodyLayer() }
        event.registerLayerDefinition(AltarBlockEntityModel.LAYER_LOCATION) { AltarBlockEntityModel.createBodyLayer() }
        event.registerLayerDefinition(PhylacteryEtherModel.LAYER_LOCATION) { PhylacteryEtherModel.createBodyLayer() }
        event.registerLayerDefinition(PhylacteryEtherCoreModel.LAYER_LOCATION) { PhylacteryEtherCoreModel.createBodyLayer() }
        event.registerLayerDefinition(MushroomLogModel.LAYER_LOCATION) { MushroomLogModel.createBodyLayer() }
        event.registerLayerDefinition(SpiritPortalBlockEntityModel.LAYER_LOCATION) { SpiritPortalBlockEntityModel.createBodyLayer() }
        event.registerLayerDefinition(SpiritPortalPortalModel.LAYER_LOCATION) { SpiritPortalPortalModel.createBodyLayer() }
        event.registerLayerDefinition(WerewolfAltarModel.LAYER_LOCATION) { WerewolfAltarModel.createBodyLayer() }
        event.registerLayerDefinition(CoffinModel.LAYER_LOCATION) { CoffinModel.createBodyLayer() }
        event.registerLayerDefinition(BearTrapModel.LAYER_LOCATION) { BearTrapModel.createBodyLayer() }
        event.registerLayerDefinition(HuntsmanSpearModel.LAYER_LOCATION) { HuntsmanSpearModel.createBodyLayer() }
        event.registerLayerDefinition(ChainModel.LAYER_LOCATION) { ChainModel.createBodyLayer() }
        event.registerLayerDefinition(JarModel.LAYER_LOCATION) { JarModel.createBodyLayer() }
        event.registerLayerDefinition(ArmorPoppetModel.LAYER_LOCATION) { ArmorPoppetModel.createBodyLayer() }
        event.registerLayerDefinition(HungerPoppetModel.LAYER_LOCATION) { HungerPoppetModel.createBodyLayer() }
        event.registerLayerDefinition(VampiricPoppetModel.LAYER_LOCATION) { VampiricPoppetModel.createBodyLayer() }
        event.registerLayerDefinition(VoodooPoppetModel.LAYER_LOCATION) { VoodooPoppetModel.createBodyLayer() }
        event.registerLayerDefinition(WitchesRobesModel.LAYER_LOCATION) { WitchesRobesModel.createBodyLayer() }
        event.registerLayerDefinition(VampireArmorModel.LAYER_LOCATION) { VampireArmorModel.createBodyLayer() }
        event.registerLayerDefinition(HunterArmorModel.LAYER_LOCATION) { HunterArmorModel.createBodyLayer() }
        event.registerLayerDefinition(SpinningWheelWheelBlockEntityModel.LAYER_LOCATION) { SpinningWheelWheelBlockEntityModel.createBodyLayer() }
        event.registerLayerDefinition(SpinningWheelBlockEntityModel.LAYER_LOCATION) { SpinningWheelBlockEntityModel.createBodyLayer() }
        event.registerLayerDefinition(BloodCrucibleModel.LAYER_LOCATION) { BloodCrucibleModel.createBodyLayer() }
        event.registerLayerDefinition(DistilleryGemModel.LAYER_LOCATION) { DistilleryGemModel.createBodyLayer() }
        event.registerLayerDefinition(GlassContainerModel.LAYER_LOCATION) { GlassContainerModel.createBodyLayer() }
        event.registerLayerDefinition(BroomEntityModel.LAYER_LOCATION) { BroomEntityModel.createBodyLayer() }
        event.registerLayerDefinition(DreamWeaverBlockEntityModel.LAYER_LOCATION) { DreamWeaverBlockEntityModel.createBodyLayer() }
        event.registerLayerDefinition(MandrakeEntityModel.LAYER_LOCATION) { MandrakeEntityModel.createBodyLayer() }
        event.registerLayerDefinition(ImpEntityModel.LAYER_LOCATION) { ImpEntityModel.createBodyLayer() }
        event.registerLayerDefinition(OwlEntityModel.LAYER_LOCATION) { OwlEntityModel.createBodyLayer() }
        event.registerLayerDefinition(DemonEntityModel.LAYER_LOCATION) { DemonEntityModel.createBodyLayer() }
        event.registerLayerDefinition(EntEntityModel.LAYER_LOCATION) { EntEntityModel.createBodyLayer() }
        event.registerLayerDefinition(BansheeEntityModel.LAYER_LOCATION) { BansheeEntityModel.createBodyLayer() }
        event.registerLayerDefinition(HornedHuntsmanModel.LAYER_LOCATION) { HornedHuntsmanModel.createBodyLayer() }
        event.registerLayerDefinition(DeathEntityModel.LAYER_LOCATION) { DeathEntityModel.createBodyLayer() }
        event.registerLayerDefinition(SpectreEntityModel.LAYER_LOCATION) { SpectreEntityModel.createBodyLayer() }
        event.registerLayerDefinition(BabaYagaEntityModel.LAYER_LOCATION) { BabaYagaEntityModel.createBodyLayer() }
        event.registerLayerDefinition(ParasiticLouseEntityModel.LAYER_LOCATION) { ParasiticLouseEntityModel.createBodyLayer() }
        event.registerLayerDefinition(WerewolfEntityModel.LAYER_LOCATION) { WerewolfEntityModel.createBodyLayer() }
        event.registerLayerDefinition(VampireEntityModel.LAYER_LOCATION) { VampireEntityModel.createBodyLayer() }
        event.registerLayerDefinition(NightmareEntityModel.LAYER_LOCATION) { NightmareEntityModel.createBodyLayer() }
        event.registerLayerDefinition(LilithEntityModel.LAYER_LOCATION) { LilithEntityModel.createBodyLayer() }
        event.registerLayerDefinition(BoatModels.ROWAN_BOAT_LAYER, BoatModel::createBodyModel)
        event.registerLayerDefinition(BoatModels.ALDER_BOAT_LAYER, BoatModel::createBodyModel)
        event.registerLayerDefinition(BoatModels.HAWTHORN_BOAT_LAYER, BoatModel::createBodyModel)
        event.registerLayerDefinition(BoatModels.ROWAN_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)
        event.registerLayerDefinition(BoatModels.ALDER_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)
        event.registerLayerDefinition(BoatModels.HAWTHORN_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)
    }

    fun registerEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.SOUL_CAGE.get(), ::SoulCageBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.WEREWOLF_ALTAR.get(), ::WerewolfAltarBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.PHYLACTERY.get(), ::PhylacteryBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.ALTAR.get(), ::AltarBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.CAULDRON.get(), ::CauldronBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.BLOOD_CRUCIBLE.get(), ::BloodCrucibleBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.DISTILLERY.get(), ::DistilleryBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.BRAZIER.get(), ::BrazierBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.COFFIN.get(), ::CoffinBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.SPINNING_WHEEL.get(), ::SpinningWheelBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.CUSTOM_SIGN.get(), ::SignRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.CUSTOM_HANGING_SIGN.get(), ::HangingSignRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.DREAM_WEAVER.get(), ::DreamWeaverBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.POPPET.get(), ::PoppetBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.SPIRIT_PORTAL.get(), ::SpiritPortalBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.BEAR_TRAP.get(), ::BearTrapBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.BRUSHABLE_BLOCK.get(), ::SuspiciousGraveyardDirtBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.SACRIFICIAL_CIRCLE.get(), ::SacrificialCircleBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.GRASSPER.get(), ::GrassperBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.CRITTER_SNARE.get(), ::CritterSnareBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.EFFIGY.get(), ::EffigyBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.MUSHROOM_LOG.get(), ::MushroomLogBlockEntityRenderer)

        event.registerEntityRenderer(WitcheryEntityTypes.BROOM.get()) { BroomEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.CHAIN.get()) { ChainEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.MANDRAKE.get()) { MandrakeEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.ENT.get()) { EntEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.BANSHEE.get()) { BansheeEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.BABA_YAGA.get()) { BabaYagaEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.SPECTRE.get()) { SpectreEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.DEMON.get()) { DemonEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.COVEN_WITCH.get()) { CovenWitchEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.PARASITIC_LOUSE.get()) { ParasiticLouseEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.DEATH.get()) { DeathEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.HORNED_HUNTSMAN.get()) { HornedHuntsmanEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.OWL.get()) { OwlEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.INSANITY.get()) { InsanityEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.IMP.get()) { ImpEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.VAMPIRE.get()) { VampireEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.WEREWOLF.get()) { WerewolfEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.NIGHTMARE.get()) { NightmareEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.LILITH.get()) { LilithEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.ELLE.get()) { ElleEntityRenderer(it) }
        event.registerEntityRenderer(WitcheryEntityTypes.CUSTOM_BOAT.get()) { context -> BoatRenderer(context, false) }
        event.registerEntityRenderer(WitcheryEntityTypes.CUSTOM_CHEST_BOAT.get()) { context -> BoatRenderer(context, true) }
        event.registerEntityRenderer(WitcheryEntityTypes.FLOATING_ITEM.get(), ::FloatingItemEntityRenderer)
        event.registerEntityRenderer(WitcheryEntityTypes.THROWN_BREW.get(), ::ThrownItemRenderer)
        event.registerEntityRenderer(WitcheryEntityTypes.THROWN_POTION.get(), ::ThrownItemRenderer)
        event.registerEntityRenderer(WitcheryEntityTypes.SLEEPING_PLAYER.get(), ::SleepingPlayerEntityRenderer)
        event.registerEntityRenderer(WitcheryEntityTypes.SPECTRAL_PIG.get(), ::SpectralPigRenderer)
        event.registerEntityRenderer(WitcheryEntityTypes.AREA_EFFECT_CLOUD.get(), ::NoopRenderer)
        event.registerEntityRenderer(WitcheryEntityTypes.HUNTSMAN_SPEAR.get(), ::HuntsmanSpearRenderer)
    }
}
