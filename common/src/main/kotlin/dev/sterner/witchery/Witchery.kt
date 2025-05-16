package dev.sterner.witchery

import com.mojang.logging.LogUtils
import dev.architectury.event.EventResult
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
import dev.sterner.witchery.block.mushroom_log.MushroomLogBlock
import dev.sterner.witchery.block.ritual.RitualChalkBlock
import dev.sterner.witchery.block.sacrificial_circle.SacrificialBlockEntity
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
import dev.sterner.witchery.client.renderer.block.*
import dev.sterner.witchery.client.renderer.entity.*
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
import dev.sterner.witchery.platform.PlatformUtils
import dev.sterner.witchery.platform.UnderWaterBreathPlayerAttachment
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
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import org.slf4j.Logger
import java.util.UUID


object Witchery {

    const val MODID: String = "witchery"

    val LOGGER: Logger = LogUtils.getLogger()

    val debugRitualLog: Boolean = PlatformUtils.isDevEnv()

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

        BarkBeltHandler.registerEvents()
        BindSpectralCreaturesRitual.registerEvents()
        BitingBeltItem.registerEvents()
        BloodPoolHandler.registerEvents()
        BrazierBlockEntity.registerEvents()
        BrewOfSleepingItem.registerEvents()
        CaneSwordItem.registerEvents()
        CurseHandler.registerEvents()
        DreamWeaverHandler.registerEvents()
        EntSpawningHandler.registerEvents()
        EquipmentHandler.registerEvents()
        FamiliarHandler.registerEvents()
        InfernalInfusionHandler.registerEvents()
        InfusionHandler.registerEvents()
        LecternHandler.registerEvents()
        LightInfusionHandler.registerEvents()
        ManifestationHandler.registerEvents()
        MushroomLogBlock.registerEvents()
        MutandisHandler.registerEvents()
        NecroHandler.registerEvents()
        NightmareHandler.registerEvents()
        OtherwhereInfusionHandler.registerEvents()
        PoppetHandler.registerEvents()
        PotionHandler.registerEvents()
        RitualChalkBlock.registerEvents()
        SacrificialBlockEntity.registerEvents()
        SoulCageBlockEntity.registerEvents()
        TeleportQueueHandler.registerEvents()
        TickTaskScheduler.registerEvents()
        TransformationHandler.registerEvents()
        UnderWaterBreathPlayerAttachment.registerEvents()
        VampireChildrenHuntHandler.registerEvents()
        VampireEventHandler.registerEvents()
        WerewolfEventHandler.registerEvents()
        WineGlassItem.registerEvents()
        WitcheryCommands.registerEvents()
        WitcheryLootInjects.registerEvents()
        WitcherySpecialPotionEffects.registerEvents()
        WitcheryStructureInjects.registerEvents()

        InteractionEvent.RIGHT_CLICK_BLOCK.register { player, hand, pos, face ->

            if (player.mainHandItem.item is WineGlassItem && player.level().getBlockState(pos).`is`(BlockTags.WOOL)) {
                val bl = player.mainHandItem.has(WitcheryDataComponents.BLOOD.get())
                val bl2 = player.mainHandItem.has(WitcheryDataComponents.CHICKEN_BLOOD.get())
                if (bl || bl2) {
                    val bl3: UUID? = player.mainHandItem.get(WitcheryDataComponents.BLOOD.get())
                    val bl4 = player.mainHandItem.get(WitcheryDataComponents.CHICKEN_BLOOD.get())
                    if (bl3 != null || bl4 != null) {
                        player.level().setBlockAndUpdate(pos, WitcheryBlocks.BLOOD_STAINED_WOOL.get().defaultBlockState())
                        player.mainHandItem.remove(WitcheryDataComponents.BLOOD.get())
                        player.mainHandItem.remove(WitcheryDataComponents.CHICKEN_BLOOD.get())
                        player.mainHandItem.remove(WitcheryDataComponents.VAMPIRE_BLOOD.get())
                        return@register EventResult.interruptTrue()
                    }
                }

            }

            return@register EventResult.pass()
        }

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
        WitcheryModelLayers.register()
        WitcheryEntityRenderers.register()
        WitcheryBlockEntityRenderers.register()

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
            WitcheryBlocks.SOUL_CAGE.get(),
            WitcheryBlocks.MUSHROOM_LOG.get()
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