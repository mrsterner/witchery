package dev.sterner.witchery

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import dev.sterner.witchery.Witchery.Companion.MODID
import dev.sterner.witchery.api.client.BloodPoolComponent
import dev.sterner.witchery.client.colors.PotionColor
import dev.sterner.witchery.client.colors.RitualChalkColors
import dev.sterner.witchery.client.layer.DemonHeadFeatureRenderer
import dev.sterner.witchery.client.model.*
import dev.sterner.witchery.client.model.poppet.ArmorPoppetModel
import dev.sterner.witchery.client.model.poppet.HungerPoppetModel
import dev.sterner.witchery.client.model.poppet.VampiricPoppetModel
import dev.sterner.witchery.client.model.poppet.VoodooPoppetModel
import dev.sterner.witchery.client.particle.BloodSplashParticle
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.client.particle.SneezeParticle
import dev.sterner.witchery.client.particle.ZzzParticle
import dev.sterner.witchery.client.renderer.block.*
import dev.sterner.witchery.client.renderer.entity.*
import dev.sterner.witchery.client.renderer.without_level.*
import dev.sterner.witchery.client.screen.AltarScreen
import dev.sterner.witchery.client.screen.DistilleryScreen
import dev.sterner.witchery.client.screen.OvenScreen
import dev.sterner.witchery.client.screen.SpinningWheelScreen
import dev.sterner.witchery.handler.BarkBeltHandler
import dev.sterner.witchery.handler.ManifestationHandler
import dev.sterner.witchery.handler.affliction.AfflictionAbilityHandler
import dev.sterner.witchery.handler.affliction.LichdomClientSpecificEventHandler
import dev.sterner.witchery.handler.affliction.VampireClientSpecificEventHandler
import dev.sterner.witchery.handler.affliction.WerewolfClientSpecificEventHandler
import dev.sterner.witchery.handler.infusion.InfusionHandler
import dev.sterner.witchery.item.TaglockItem
import dev.sterner.witchery.item.WitchesRobesItem
import dev.sterner.witchery.payload.DismountBroomC2SPayload
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
import net.minecraft.client.Minecraft
import net.minecraft.client.model.BoatModel
import net.minecraft.client.model.ChestBoatModel
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.blockentity.HangingSignRenderer
import net.minecraft.client.renderer.blockentity.SignRenderer
import net.minecraft.client.renderer.entity.BoatRenderer
import net.minecraft.client.renderer.entity.NoopRenderer
import net.minecraft.client.renderer.entity.ThrownItemRenderer
import net.minecraft.client.renderer.entity.player.PlayerRenderer
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.*
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.network.PacketDistributor

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
        modEventBus.addListener(::onRegisterBlockColors)
        modEventBus.addListener(::onRegisterItemColors)

        NeoForge.EVENT_BUS.addListener(::onMouseScrolled)
        NeoForge.EVENT_BUS.addListener(::onRenderHud)
        NeoForge.EVENT_BUS.addListener(::onClientTick)
    }

    fun onRegisterItemColors(event: RegisterColorHandlersEvent.Item) {
        event.register(
            PotionColor,
            WitcheryItems.WITCHERY_POTION.get()
        )
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            registerItemProperties()
            registerRenderLayers()
        }
    }

    fun onRegisterBlockColors(event: RegisterColorHandlersEvent.Block) {
        event.register(
            RitualChalkColors,
            WitcheryBlocks.RITUAL_CHALK_BLOCK.get(),
            WitcheryBlocks.INFERNAL_CHALK_BLOCK.get(),
            WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get(),
            WitcheryBlocks.SACRIFICIAL_CIRCLE.get()
        )
    }

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
                ResourceLocation.fromNamespaceAndPath(MODID, "block/flowing_spirit_still")

            override fun getFlowingTexture() =
                ResourceLocation.fromNamespaceAndPath(MODID, "block/flowing_spirit_flowing")

            override fun getTintColor() = 0xAAFFFF

            override fun getOverlayTexture() =
                ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_overlay")
        }, WitcheryFluids.FLOWING_SPIRIT_TYPE.get())
    }

    private fun onRenderHud(event: RenderGuiEvent.Post) {
        InfusionHandler.renderInfusionHud(event.guiGraphics, event.partialTick)
        ManifestationHandler.renderHud(event.guiGraphics, event.partialTick)
        VampireClientSpecificEventHandler.renderHud(event.guiGraphics)
        WerewolfClientSpecificEventHandler.renderHud(event.guiGraphics)
        LichdomClientSpecificEventHandler.renderHud(event.guiGraphics)
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
            ShaderInstance(
                event.resourceProvider,
                Witchery.id("spirit_cage"),
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP
            )
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
        event.registerBlockEntityRenderer(
            WitcheryBlockEntityTypes.WEREWOLF_ALTAR.get(),
            ::WerewolfAltarBlockEntityRenderer
        )
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.PHYLACTERY.get(), ::PhylacteryBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.ALTAR.get(), ::AltarBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.CAULDRON.get(), ::CauldronBlockEntityRenderer)
        event.registerBlockEntityRenderer(
            WitcheryBlockEntityTypes.BLOOD_CRUCIBLE.get(),
            ::BloodCrucibleBlockEntityRenderer
        )
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.DISTILLERY.get(), ::DistilleryBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.BRAZIER.get(), ::BrazierBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.COFFIN.get(), ::CoffinBlockEntityRenderer)
        event.registerBlockEntityRenderer(
            WitcheryBlockEntityTypes.SPINNING_WHEEL.get(),
            ::SpinningWheelBlockEntityRenderer
        )
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.CUSTOM_SIGN.get(), ::SignRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.CUSTOM_HANGING_SIGN.get(), ::HangingSignRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.DREAM_WEAVER.get(), ::DreamWeaverBlockEntityRenderer)
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.POPPET.get(), ::PoppetBlockEntityRenderer)
        event.registerBlockEntityRenderer(
            WitcheryBlockEntityTypes.SPIRIT_PORTAL.get(),
            ::SpiritPortalBlockEntityRenderer
        )
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.BEAR_TRAP.get(), ::BearTrapBlockEntityRenderer)
        event.registerBlockEntityRenderer(
            WitcheryBlockEntityTypes.BRUSHABLE_BLOCK.get(),
            ::SuspiciousGraveyardDirtBlockEntityRenderer
        )
        event.registerBlockEntityRenderer(
            WitcheryBlockEntityTypes.SACRIFICIAL_CIRCLE.get(),
            ::SacrificialCircleBlockEntityRenderer
        )
        event.registerBlockEntityRenderer(WitcheryBlockEntityTypes.GRASSPER.get(), ::GrassperBlockEntityRenderer)
        event.registerBlockEntityRenderer(
            WitcheryBlockEntityTypes.CRITTER_SNARE.get(),
            ::CritterSnareBlockEntityRenderer
        )
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
        event.registerEntityRenderer(WitcheryEntityTypes.CUSTOM_CHEST_BOAT.get()) { context ->
            BoatRenderer(
                context,
                true
            )
        }
        event.registerEntityRenderer(WitcheryEntityTypes.FLOATING_ITEM.get(), ::FloatingItemEntityRenderer)
        event.registerEntityRenderer(WitcheryEntityTypes.THROWN_BREW.get(), ::ThrownItemRenderer)
        event.registerEntityRenderer(WitcheryEntityTypes.THROWN_POTION.get(), ::ThrownItemRenderer)
        event.registerEntityRenderer(WitcheryEntityTypes.SLEEPING_PLAYER.get(), ::SleepingPlayerEntityRenderer)
        event.registerEntityRenderer(WitcheryEntityTypes.SPECTRAL_PIG.get(), ::SpectralPigRenderer)
        event.registerEntityRenderer(WitcheryEntityTypes.AREA_EFFECT_CLOUD.get(), ::NoopRenderer)
        event.registerEntityRenderer(WitcheryEntityTypes.HUNTSMAN_SPEAR.get(), ::HuntsmanSpearRenderer)
    }

    private fun registerItemProperties() {
        ItemProperties.register(
            WitcheryItems.WAYSTONE.get(),
            Witchery.id("is_bound")
        ) { itemStack, client, entity, i ->
            val customData = itemStack.get(WitcheryDataComponents.GLOBAL_POS_COMPONENT.get())
            val customData2 = itemStack.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
            when {
                TaglockItem.getPlayerProfile(itemStack) != null || customData2 != null -> 2.0f
                customData != null -> 1.0f
                else -> 0f
            }
        }

        ItemProperties.register(
            WitcheryItems.TAGLOCK.get(),
            Witchery.id("expired")
        ) { itemStack, _, _, _, ->
            val expired = itemStack.get(WitcheryDataComponents.EXPIRED_TAGLOCK.get())
            if (expired == true) 1.0f else 0f
        }

        ItemProperties.register(
            WitcheryItems.CHALICE.get(),
            Witchery.id("has_soup")
        ) { stack, _, _, _ ->
            val hasSoup = stack.get(WitcheryDataComponents.HAS_SOUP.get()) ?: false
            if (hasSoup) 1.0f else 0f
        }

        ItemProperties.register(
            WitcheryItems.WINE_GLASS.get(),
            Witchery.id("blood")
        ) { stack, _, _, _ ->
            val hasBlood = stack.get(WitcheryDataComponents.BLOOD.get())
            if (hasBlood != null) 1.0f else 0f
        }

        ItemProperties.register(
            WitcheryItems.QUARTZ_SPHERE.get(),
            Witchery.id("has_sun")
        ) { stack, _, _, _ ->
            val hasSun = stack.get(WitcheryDataComponents.HAS_SUN.get())
            if (hasSun != null) 1.0f else 0f
        }

        ItemProperties.register(
            WitcheryItems.CANE_SWORD.get(),
            Witchery.id("unsheeted")
        ) { stack, _, _, _ ->
            val isUnsheathed = stack.get(WitcheryDataComponents.UNSHEETED.get()) ?: false
            if (isUnsheathed) 1.0f else 0f
        }
    }

    private fun registerRenderLayers() {
        ItemBlockRenderTypes.setRenderLayer(WitcheryFluids.FLOWING_SPIRIT_FLOWING.get(), RenderType.translucent())
        ItemBlockRenderTypes.setRenderLayer(WitcheryFluids.FLOWING_SPIRIT_STILL.get(), RenderType.translucent())

        val cutoutBlocks = listOf(
            WitcheryBlocks.CENSER,
            WitcheryBlocks.GOLDEN_CHALK_BLOCK,
            WitcheryBlocks.RITUAL_CHALK_BLOCK,
            WitcheryBlocks.INFERNAL_CHALK_BLOCK,
            WitcheryBlocks.OTHERWHERE_CHALK_BLOCK,
            WitcheryBlocks.CAULDRON,
            WitcheryBlocks.GLINTWEED,
            WitcheryBlocks.EMBER_MOSS,
            WitcheryBlocks.SPANISH_MOSS,
            WitcheryBlocks.MANDRAKE_CROP,
            WitcheryBlocks.BELLADONNA_CROP,
            WitcheryBlocks.COPPER_WITCHES_OVEN,
            WitcheryBlocks.IRON_WITCHES_OVEN,
            WitcheryBlocks.SNOWBELL_CROP,
            WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION,
            WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION,
            WitcheryBlocks.GARLIC_CROP,
            WitcheryBlocks.WORMWOOD_CROP,
            WitcheryBlocks.WOLFSFBANE_CROP,
            WitcheryBlocks.WATER_ARTICHOKE_CROP,
            WitcheryBlocks.ROWAN_LEAVES,
            WitcheryBlocks.ROWAN_BERRY_LEAVES,
            WitcheryBlocks.ROWAN_DOOR,
            WitcheryBlocks.ROWAN_TRAPDOOR,
            WitcheryBlocks.ROWAN_SAPLING,
            WitcheryBlocks.POTTED_ROWAN_SAPLING,
            WitcheryBlocks.ALDER_LEAVES,
            WitcheryBlocks.ALDER_DOOR,
            WitcheryBlocks.ALDER_TRAPDOOR,
            WitcheryBlocks.ALDER_SAPLING,
            WitcheryBlocks.POTTED_ALDER_SAPLING,
            WitcheryBlocks.HAWTHORN_LEAVES,
            WitcheryBlocks.HAWTHORN_DOOR,
            WitcheryBlocks.HAWTHORN_TRAPDOOR,
            WitcheryBlocks.HAWTHORN_SAPLING,
            WitcheryBlocks.POTTED_HAWTHORN_SAPLING,
            WitcheryBlocks.DISTILLERY,
            WitcheryBlocks.DEMON_HEART,
            WitcheryBlocks.BLOOD_POPPY,
            WitcheryBlocks.ARTHANA,
            WitcheryBlocks.CHALICE,
            WitcheryBlocks.DISTURBED_COTTON,
            WitcheryBlocks.WISPY_COTTON,
            WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT,
            WitcheryBlocks.SACRIFICIAL_CIRCLE,
            WitcheryBlocks.SUNLIGHT_COLLECTOR,
            WitcheryBlocks.GRASSPER,
            WitcheryBlocks.FLOWING_SPIRIT_BLOCK,
            WitcheryBlocks.BRAZIER,
            WitcheryBlocks.WITCHS_LADDER,
            WitcheryBlocks.CLAY_EFFIGY,
            WitcheryBlocks.SCARECROW,
            WitcheryBlocks.EFFIGY_COMPONENT,
            WitcheryBlocks.CRITTER_SNARE,
            WitcheryBlocks.SOUL_CAGE,
            WitcheryBlocks.MUSHROOM_LOG
        )

        cutoutBlocks.forEach { block ->
            ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout())
        }
    }
}
