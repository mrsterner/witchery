package dev.sterner.witchery

import com.mojang.logging.LogUtils
import dev.architectury.event.events.client.ClientGuiEvent
import dev.architectury.event.events.client.ClientRawInputEvent
import dev.architectury.event.events.client.ClientTickEvent
import dev.architectury.event.events.common.*
import dev.architectury.networking.NetworkManager
import dev.architectury.registry.client.gui.ClientTooltipComponentRegistry
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry
import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import dev.architectury.registry.client.particle.ParticleProviderRegistry
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry
import dev.architectury.registry.client.rendering.ColorHandlerRegistry
import dev.architectury.registry.client.rendering.RenderTypeRegistry
import dev.architectury.registry.item.ItemPropertiesRegistry
import dev.architectury.registry.menu.MenuRegistry
import dev.sterner.witchery.api.client.BloodPoolComponent
import dev.sterner.witchery.api.schedule.TickTaskScheduler
import dev.sterner.witchery.block.brazier.BrazierBlockEntity
import dev.sterner.witchery.block.ritual.RitualChalkBlock
import dev.sterner.witchery.block.sacrificial_circle.SacrificialBlockEntity
import dev.sterner.witchery.block.soul_cage.SoulCageBlock
import dev.sterner.witchery.block.soul_cage.SoulCageBlockEntity
import dev.sterner.witchery.client.colors.PotionColor
import dev.sterner.witchery.client.colors.RitualChalkColors
import dev.sterner.witchery.client.model.*
import dev.sterner.witchery.client.model.poppet.ArmorPoppetModel
import dev.sterner.witchery.client.model.poppet.HungerPoppetModel
import dev.sterner.witchery.client.model.poppet.VampiricPoppetModel
import dev.sterner.witchery.client.model.poppet.VoodooPoppetModel
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.client.particle.SneezeParticle
import dev.sterner.witchery.client.particle.ZzzParticle
import dev.sterner.witchery.client.renderer.*
import dev.sterner.witchery.client.screen.AltarScreen
import dev.sterner.witchery.client.screen.DistilleryScreen
import dev.sterner.witchery.client.screen.OvenScreen
import dev.sterner.witchery.client.screen.SpinningWheelScreen
import dev.sterner.witchery.data.*
import dev.sterner.witchery.handler.*
import dev.sterner.witchery.handler.infusion.InfernalInfusionHandler
import dev.sterner.witchery.handler.infusion.InfusionHandler
import dev.sterner.witchery.handler.infusion.LightInfusionHandler
import dev.sterner.witchery.handler.infusion.OtherwhereInfusionHandler
import dev.sterner.witchery.handler.poppet.PoppetHandler
import dev.sterner.witchery.handler.transformation.TransformationHandler
import dev.sterner.witchery.handler.vampire.VampireAbilityHandler
import dev.sterner.witchery.handler.vampire.VampireChildrenHuntHandler
import dev.sterner.witchery.handler.vampire.VampireEventHandler
import dev.sterner.witchery.handler.werewolf.WerewolfAbilityHandler
import dev.sterner.witchery.handler.werewolf.WerewolfEventHandler
import dev.sterner.witchery.integration.modonomicon.WitcheryPageRendererRegistry
import dev.sterner.witchery.item.CaneSwordItem
import dev.sterner.witchery.item.TaglockItem
import dev.sterner.witchery.item.WineGlassItem
import dev.sterner.witchery.item.accessories.BitingBeltItem
import dev.sterner.witchery.item.brew.BrewOfSleepingItem
import dev.sterner.witchery.payload.DismountBroomC2SPayload
import dev.sterner.witchery.platform.DeathQueueLevelAttachment
import dev.sterner.witchery.platform.WitcheryPehkui
import dev.sterner.witchery.platform.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.platform.transformation.*
import dev.sterner.witchery.registry.*
import dev.sterner.witchery.registry.WitcheryDataComponents.UNSHEETED
import dev.sterner.witchery.registry.WitcheryItems.CANE_SWORD
import dev.sterner.witchery.ritual.BindSpectralCreaturesRitual
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.client.model.BoatModel
import net.minecraft.client.model.ChestBoatModel
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.HangingSignRenderer
import net.minecraft.client.renderer.blockentity.SignRenderer
import net.minecraft.client.renderer.entity.BoatRenderer
import net.minecraft.client.renderer.entity.NoopRenderer
import net.minecraft.client.renderer.entity.ThrownItemRenderer
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger


object Witchery {

    const val MODID: String = "witchery"

    val LOGGER: Logger = LogUtils.getLogger()

    val debugRitualLog: Boolean = true

    fun id(name: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MODID, name)
    }

    fun logDebugRitual(message: String) {
        if (debugRitualLog) {
            println(message)
        }
    }

    @JvmStatic
    fun init() {
        WitcheryCurseRegistry.register()
        WitcheryFetishEffects.register()
        WitcheryRitualRegistry.register()
        WitcheryMobEffects.register()
        WitcherySpecialPotionEffects.register()
        WitcheryPehkui.register()
        WitcheryFluids.FLUIDS.register()
        WitcheryFluids.register()
        WitcheryArmorMaterials.MATERIALS.register()
        WitcheryBlocks.BLOCKS.register()
        WitcheryBlockEntityTypes.BLOCK_ENTITY_TYPES.register()
        WitcheryItems.ITEMS.register()
        WitcheryEntityTypes.ENTITY_TYPES.register()
        WitcherySounds.SOUNDS.register()
        WitcheryCreativeModeTabs.TABS.register()
        WitcheryParticleTypes.PARTICLES.register()
        WitcheryRecipeTypes.RECIPE_TYPES.register()
        WitcheryRecipeSerializers.RECIPE_SERIALIZERS.register()
        WitcheryMenuTypes.MENU_TYPES.register()
        WitcheryDataComponents.DATA.register()
        WitcheryCommands.COMMAND_ARGUMENTS.register()
        WitcheryFeatures.FEATURES.register()
        WitcheryPayloads.register()
        WitcheryEntityAttributes.register()
        WitcheryPoppetRegistry.register()

        PotionDataReloadListener.registerListener()
        FetishEffectReloadListener.registerListener()
        NaturePowerReloadListener.registerListener()
        ErosionReloadListener.registerListener()
        BloodPoolReloadListener.registerListener()

        WitcheryModonomiconLoaders.register()

        WitcheryCommands.registerEvents()
        VampireEventHandler.registerEvents()
        WerewolfEventHandler.registerEvents()
        CurseHandler.registerEvents()
        PotionHandler.registerEvents()
        NecroHandler.registerEvents()
        MutandisHandler.registerEvents()
        PoppetHandler.registerEvents()
        FamiliarHandler.registerEvents()
        CaneSwordItem.registerEvents()
        EquipmentHandler.registerEvents()
        BitingBeltItem.registerEvents()
        BloodPoolHandler.registerEvents()
        DreamWeaverHandler.registerEvents()
        BrewOfSleepingItem.registerEvents()
        InfusionHandler.registerEvents()
        SacrificialBlockEntity.registerEvents()
        LecternHandler.registerEvents()
        WineGlassItem.registerEvents()
        EntSpawningHandler.registerEvents()
        RitualChalkBlock.registerEvents()
        WitcherySpecialPotionEffects.registerEvents()
        TeleportQueueHandler.registerEvents()
        ManifestationHandler.registerEvents()
        VampireChildrenHuntHandler.registerEvents()
        InfernalInfusionHandler.registerEvents()
        BloodPoolHandler.registerEvents()
        LightInfusionHandler.registerEvents()
        OtherwhereInfusionHandler.registerEvents()
        NightmareHandler.registerEvents()
        TransformationHandler.registerEvents()
        BarkBeltHandler.registerEvents()
        TickTaskScheduler.registerEvents()
        BindSpectralCreaturesRitual.registerEvents()
        BrazierBlockEntity.registerEvents()
        SoulCageBlockEntity.registerEvents()
        WitcheryLootInjects.registerEvents()
        WitcheryStructureInjects.registerEvents()

        PlayerEvent.PLAYER_RESPAWN.register { player, _, _ ->
            VampireAbilityHandler.setAbilityIndex(player, -1)
            WerewolfAbilityHandler.setAbilityIndex(player, -1)
        }

        PlayerEvent.PLAYER_JOIN.register { serverPlayer ->
            val data = DeathQueueLevelAttachment.getData(serverPlayer.serverLevel())
            if (data.killerQueue.contains(serverPlayer.uuid)) {
                serverPlayer.kill()
            }
            VampirePlayerAttachment.sync(serverPlayer, VampirePlayerAttachment.getData(serverPlayer))
            WerewolfPlayerAttachment.sync(serverPlayer, WerewolfPlayerAttachment.getData(serverPlayer))
            BloodPoolLivingEntityAttachment.sync(serverPlayer, BloodPoolLivingEntityAttachment.getData(serverPlayer))
            TransformationPlayerAttachment.sync(serverPlayer, TransformationPlayerAttachment.getData(serverPlayer))
            InfusionPlayerAttachment.sync(serverPlayer, InfusionPlayerAttachment.getPlayerInfusion(serverPlayer))
        }
    }

    @JvmStatic
    @Environment(EnvType.CLIENT)
    fun initClient() {
        EntityModelLayerRegistry.register(AltarClothBlockEntityModel.LAYER_LOCATION) { AltarClothBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(AltarBlockEntityModel.LAYER_LOCATION) { AltarBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpiritPortalBlockEntityModel.LAYER_LOCATION) { SpiritPortalBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpiritPortalPortalModel.LAYER_LOCATION) { SpiritPortalPortalModel.createBodyLayer() }
        EntityModelLayerRegistry.register(WerewolfAltarModel.LAYER_LOCATION) { WerewolfAltarModel.createBodyLayer() }
        EntityModelLayerRegistry.register(CoffinModel.LAYER_LOCATION) { CoffinModel.createBodyLayer() }
        EntityModelLayerRegistry.register(BearTrapModel.LAYER_LOCATION) { BearTrapModel.createBodyLayer() }
        EntityModelLayerRegistry.register(ChainModel.LAYER_LOCATION) { ChainModel.createBodyLayer() }
        EntityModelLayerRegistry.register(JarModel.LAYER_LOCATION) { JarModel.createBodyLayer() }
        EntityModelLayerRegistry.register(ArmorPoppetModel.LAYER_LOCATION) { ArmorPoppetModel.createBodyLayer() }
        EntityModelLayerRegistry.register(HungerPoppetModel.LAYER_LOCATION) { HungerPoppetModel.createBodyLayer() }
        EntityModelLayerRegistry.register(VampiricPoppetModel.LAYER_LOCATION) { VampiricPoppetModel.createBodyLayer() }
        EntityModelLayerRegistry.register(VoodooPoppetModel.LAYER_LOCATION) { VoodooPoppetModel.createBodyLayer() }
        EntityModelLayerRegistry.register(WitchesRobesModel.LAYER_LOCATION) { WitchesRobesModel.createBodyLayer() }
        EntityModelLayerRegistry.register(VampireArmorModel.LAYER_LOCATION) { VampireArmorModel.createBodyLayer() }
        EntityModelLayerRegistry.register(HunterArmorModel.LAYER_LOCATION) { HunterArmorModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpinningWheelWheelBlockEntityModel.LAYER_LOCATION) { SpinningWheelWheelBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpinningWheelBlockEntityModel.LAYER_LOCATION) { SpinningWheelBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(VampireAltarModel.LAYER_LOCATION) { VampireAltarModel.createBodyLayer() }
        EntityModelLayerRegistry.register(DistilleryGemModel.LAYER_LOCATION) { DistilleryGemModel.createBodyLayer() }
        EntityModelLayerRegistry.register(GlassContainerModel.LAYER_LOCATION) { GlassContainerModel.createBodyLayer() }
        EntityModelLayerRegistry.register(BroomEntityModel.LAYER_LOCATION) { BroomEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(DreamWeaverBlockEntityModel.LAYER_LOCATION) { DreamWeaverBlockEntityModel.createBodyLayer() }

        EntityRendererRegistry.register(WitcheryEntityTypes.BROOM) { BroomEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.CHAIN) { ChainEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.MANDRAKE) { MandrakeEntityRenderer(it) }
        EntityModelLayerRegistry.register(MandrakeEntityModel.LAYER_LOCATION) { MandrakeEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.IMP) { ImpEntityRenderer(it) }
        EntityModelLayerRegistry.register(ImpEntityModel.LAYER_LOCATION) { ImpEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.OWL) { OwlEntityRenderer(it) }
        EntityModelLayerRegistry.register(OwlEntityModel.LAYER_LOCATION) { OwlEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.DEMON) { DemonEntityRenderer(it) }
        EntityModelLayerRegistry.register(DemonEntityModel.LAYER_LOCATION) { DemonEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.ENT) { EntEntityRenderer(it) }
        EntityModelLayerRegistry.register(EntEntityModel.LAYER_LOCATION) { EntEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.BANSHEE) { BansheeEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.SPECTRE) { SpectreEntityRenderer(it) }
        EntityModelLayerRegistry.register(BansheeEntityModel.LAYER_LOCATION) { BansheeEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpectreEntityModel.LAYER_LOCATION) { SpectreEntityModel.createBodyLayer() }

        EntityRendererRegistry.register(WitcheryEntityTypes.PARASITIC_LOUSE) { ParasiticLouseEntityRenderer(it) }
        EntityModelLayerRegistry.register(ParasiticLouseEntityModel.LAYER_LOCATION) { ParasiticLouseEntityModel.createBodyLayer() }

        EntityRendererRegistry.register(WitcheryEntityTypes.INSANITY) { InsanityEntityRenderer(it) }

        EntityRendererRegistry.register(WitcheryEntityTypes.VAMPIRE) { VampireEntityRenderer(it) }
        EntityModelLayerRegistry.register(VampireEntityModel.LAYER_LOCATION) { VampireEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.WEREWOLF) { WerewolfEntityRenderer(it) }
        EntityModelLayerRegistry.register(WerewolfEntityModel.LAYER_LOCATION) { WerewolfEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.NIGHTMARE) { NightmareEntityRenderer(it) }
        EntityModelLayerRegistry.register(NightmareEntityModel.LAYER_LOCATION) { NightmareEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.LILITH) { LilithEntityRenderer(it) }
        EntityModelLayerRegistry.register(LilithEntityModel.LAYER_LOCATION) { LilithEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.ELLE) { ElleEntityRenderer(it) }

        EntityRendererRegistry.register(WitcheryEntityTypes.CUSTOM_BOAT) { context -> BoatRenderer(context, false) }
        EntityModelLayerRegistry.register(BoatModels.ROWAN_BOAT_LAYER, BoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.ALDER_BOAT_LAYER, BoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.HAWTHORN_BOAT_LAYER, BoatModel::createBodyModel)
        EntityRendererRegistry.register(WitcheryEntityTypes.CUSTOM_CHEST_BOAT) { context ->
            BoatRenderer(
                context,
                true
            )
        }
        EntityModelLayerRegistry.register(BoatModels.ROWAN_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.ALDER_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.HAWTHORN_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)

        EntityRendererRegistry.register(WitcheryEntityTypes.FLOATING_ITEM, ::FloatingItemEntityRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.THROWN_BREW, ::ThrownItemRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.THROWN_POTION, ::ThrownItemRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.SLEEPING_PLAYER, ::SleepingPlayerEntityRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.SPECTRAL_PIG, ::SpectralPigRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.AREA_EFFECT_CLOUD, ::NoopRenderer)

        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.ALTAR.get(), ::AltarBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CAULDRON.get(), ::CauldronBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.VAMPIRE_ALTAR.get(), ::VampireAltarBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.DISTILLERY.get(), ::DistilleryBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.BRAZIER.get(), ::BrazierBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.SOUL_CAGE.get(), ::SoulCageBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.COFFIN.get(), ::CoffinBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.SPINNING_WHEEL.get(), ::SpinningWheelBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CUSTOM_SIGN.get(), ::SignRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CUSTOM_HANGING_SIGN.get(), ::HangingSignRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.DREAM_WEAVER.get(), ::DreamWeaverBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.POPPET.get(), ::PoppetBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.SPIRIT_PORTAL.get(), ::SpiritPortalBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.WEREWOLF_ALTAR.get(), ::WerewolfAltarBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.BEAR_TRAP.get(), ::BearTrapBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.BRUSHABLE_BLOCK.get(), ::SuspiciousGraveyardDirtBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.SACRIFICIAL_CIRCLE.get(), ::SacrificialCircleBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.GRASSPER.get(), ::GrassperBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CRITTER_SNARE.get(), ::CritterSnareBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.EFFIGY.get(), ::EffigyBlockEntityRenderer)

        ClientTooltipComponentRegistry.register(
            BloodPoolComponent::class.java,
            BloodPoolComponent::getClientTooltipComponent
        )

        ParticleProviderRegistry.register(WitcheryParticleTypes.COLOR_BUBBLE.get(), ColorBubbleParticle::Provider)
        ParticleProviderRegistry.register(WitcheryParticleTypes.ZZZ.get(), ZzzParticle::Provider)
        ParticleProviderRegistry.register(WitcheryParticleTypes.SNEEZE.get(), SneezeParticle::SneezeProvider)

        MenuRegistry.registerScreenFactory(WitcheryMenuTypes.OVEN_MENU_TYPE.get(), ::OvenScreen)
        MenuRegistry.registerScreenFactory(WitcheryMenuTypes.ALTAR_MENU_TYPE.get(), ::AltarScreen)
        MenuRegistry.registerScreenFactory(WitcheryMenuTypes.DISTILLERY_MENU_TYPE.get(), ::DistilleryScreen)
        MenuRegistry.registerScreenFactory(WitcheryMenuTypes.SPINNING_WHEEL_MENU_TYPE.get(), ::SpinningWheelScreen)

        WitcheryPageRendererRegistry.register()

        ClientGuiEvent.RENDER_HUD.register(InfusionHandler::renderInfusionHud)
        ClientGuiEvent.RENDER_HUD.register(ManifestationHandler::renderHud)
        ClientGuiEvent.RENDER_HUD.register { guiGraphics, _ -> VampireEventHandler.renderHud(guiGraphics) }
        ClientGuiEvent.RENDER_HUD.register(WerewolfEventHandler::renderHud)
        ClientGuiEvent.RENDER_HUD.register(BarkBeltHandler::renderHud)

        ItemPropertiesRegistry.register(
            WitcheryItems.WAYSTONE.get(),
            ResourceLocation.fromNamespaceAndPath(MODID, "is_bound")
        ) { itemStack, _, _, _ ->
            var ret = 0f
            val customData = itemStack.get(WitcheryDataComponents.GLOBAL_POS_COMPONENT.get())
            val customData2 = itemStack.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
            if (TaglockItem.getPlayerProfile(itemStack) != null || customData2 != null) {
                ret = 2.0f
            } else if (customData != null) {
                ret = 1.0f
            }
            ret
        }

        ItemPropertiesRegistry.register(
            WitcheryItems.TAGLOCK.get(),
            ResourceLocation.fromNamespaceAndPath(MODID, "expired")
        ) { itemStack, _, _, _ ->
            var ret = 0f
            val customData = itemStack.get(WitcheryDataComponents.EXPIRED_TAGLOCK.get())
            if (customData != null && customData) {
                ret = 1.0f
            }
            ret
        }

        ItemPropertiesRegistry.register(
            WitcheryItems.CHALICE.get(),
            ResourceLocation.fromNamespaceAndPath(MODID, "has_soup")
        ) { stack, _, _, _ ->
            val data = stack.get(WitcheryDataComponents.HAS_SOUP.get()) ?: return@register 0f
            if (data)
                return@register 1f
            0f
        }

        ItemPropertiesRegistry.register(
            WitcheryItems.WINE_GLASS.get(),
            ResourceLocation.fromNamespaceAndPath(MODID, "blood")
        ) { stack, _, _, _ ->
            stack.get(WitcheryDataComponents.BLOOD.get()) ?: return@register 0f
            return@register 1f
        }

        ItemPropertiesRegistry.register(
            WitcheryItems.QUARTZ_SPHERE.get(),
            ResourceLocation.fromNamespaceAndPath(MODID, "has_sun")
        ) { stack, _, _, _ ->
            stack.get(WitcheryDataComponents.HAS_SUN.get()) ?: return@register 0f
            return@register 1f
        }

        ItemPropertiesRegistry.register(
            CANE_SWORD.get(),
            ResourceLocation.fromNamespaceAndPath(MODID, "unsheeted")
        ) { stack, _, _, _ ->
            val bl = stack.has(UNSHEETED.get())
            if (bl) {
                val bl2 = stack.get(UNSHEETED.get())!!
                if (bl2) {
                    return@register 1f
                }
            }

            return@register 0f
        }

        ColorHandlerRegistry.registerBlockColors(
            RitualChalkColors,
            WitcheryBlocks.RITUAL_CHALK_BLOCK.get(),
            WitcheryBlocks.INFERNAL_CHALK_BLOCK.get(),
            WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get(),
            WitcheryBlocks.SACRIFICIAL_CIRCLE.get()
        )

        ColorHandlerRegistry.registerItemColors(
            PotionColor,
            WitcheryItems.WITCHERY_POTION.get()
        )

        RenderTypeRegistry.register(
            RenderType.translucent(),
            WitcheryFluids.FLOWING_FLOWING_SPIRIT.get(),
            WitcheryFluids.FLOWING_SPIRIT_STILL.get()
        )

        RenderTypeRegistry.register(
            RenderType.cutout(),
            WitcheryBlocks.GOLDEN_CHALK_BLOCK.get(),
            WitcheryBlocks.RITUAL_CHALK_BLOCK.get(),
            WitcheryBlocks.INFERNAL_CHALK_BLOCK.get(),
            WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get(),
            WitcheryBlocks.CAULDRON.get(),
            WitcheryBlocks.GLINTWEED.get(),
            WitcheryBlocks.EMBER_MOSS.get(),
            WitcheryBlocks.SPANISH_MOSS.get(),
            WitcheryBlocks.MANDRAKE_CROP.get(),
            WitcheryBlocks.BELLADONNA_CROP.get(),
            WitcheryBlocks.COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.IRON_WITCHES_OVEN.get(),
            WitcheryBlocks.SNOWBELL_CROP.get(),
            WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.GARLIC_CROP.get(),
            WitcheryBlocks.WORMWOOD_CROP.get(),
            WitcheryBlocks.WOLFSFBANE_CROP.get(),
            WitcheryBlocks.WATER_ARTICHOKE_CROP.get(),
            WitcheryBlocks.ROWAN_LEAVES.get(),
            WitcheryBlocks.ROWAN_BERRY_LEAVES.get(),
            WitcheryBlocks.ROWAN_DOOR.get(),
            WitcheryBlocks.ROWAN_TRAPDOOR.get(),
            WitcheryBlocks.ROWAN_SAPLING.get(),
            WitcheryBlocks.POTTED_ROWAN_SAPLING.get(),
            WitcheryBlocks.ALDER_LEAVES.get(),
            WitcheryBlocks.ALDER_DOOR.get(),
            WitcheryBlocks.ALDER_TRAPDOOR.get(),
            WitcheryBlocks.ALDER_SAPLING.get(),
            WitcheryBlocks.POTTED_ALDER_SAPLING.get(),
            WitcheryBlocks.HAWTHORN_LEAVES.get(),
            WitcheryBlocks.HAWTHORN_DOOR.get(),
            WitcheryBlocks.HAWTHORN_TRAPDOOR.get(),
            WitcheryBlocks.HAWTHORN_SAPLING.get(),
            WitcheryBlocks.POTTED_HAWTHORN_SAPLING.get(),
            WitcheryBlocks.DISTILLERY.get(),
            WitcheryBlocks.DEMON_HEART.get(),
            WitcheryBlocks.BLOOD_POPPY.get(),
            WitcheryBlocks.ARTHANA.get(),
            WitcheryBlocks.CHALICE.get(),
            WitcheryBlocks.DISTURBED_COTTON.get(),
            WitcheryBlocks.WISPY_COTTON.get(),
            WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get(),
            WitcheryBlocks.SACRIFICIAL_CIRCLE.get(),
            WitcheryBlocks.SUNLIGHT_COLLECTOR.get(),
            WitcheryBlocks.GRASSPER.get(),
            WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get(),
            WitcheryBlocks.BRAZIER.get(),
            WitcheryBlocks.WITCHS_LADDER.get(),
            WitcheryBlocks.TRENT_EFFIGY.get(),
            WitcheryBlocks.SCARECROW.get(),
            WitcheryBlocks.CRITTER_SNARE.get(),
            WitcheryBlocks.SOUL_CAGE.get()
        )

        KeyMappingRegistry.register(WitcheryKeyMappings.BROOM_DISMOUNT_KEYMAPPING)
        ClientRawInputEvent.MOUSE_SCROLLED.register(VampireAbilityHandler::scroll)
        ClientRawInputEvent.MOUSE_SCROLLED.register(WerewolfAbilityHandler::scroll)


        ClientTickEvent.CLIENT_POST.register(ClientTickEvent.Client { minecraft: Minecraft? ->
            while (WitcheryKeyMappings.BROOM_DISMOUNT_KEYMAPPING.consumeClick()) {
                minecraft?.player?.stopRiding()
                if (minecraft?.player != null) {
                    NetworkManager.sendToServer(DismountBroomC2SPayload())
                }
            }
        })
    }
}
