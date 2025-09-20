package dev.sterner.witchery

import dev.sterner.witchery.Witchery.Companion.MODID
import dev.sterner.witchery.api.client.BloodPoolComponent
import dev.sterner.witchery.client.model.*
import dev.sterner.witchery.client.model.poppet.ArmorPoppetModel
import dev.sterner.witchery.client.model.poppet.HungerPoppetModel
import dev.sterner.witchery.client.model.poppet.VampiricPoppetModel
import dev.sterner.witchery.client.model.poppet.VoodooPoppetModel
import dev.sterner.witchery.client.renderer.block.*
import dev.sterner.witchery.client.renderer.entity.*
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
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryKeyMappings
import dev.sterner.witchery.registry.WitcheryMenuTypes
import net.minecraft.client.Minecraft
import net.minecraft.client.model.BoatModel
import net.minecraft.client.model.ChestBoatModel
import net.minecraft.client.renderer.blockentity.HangingSignRenderer
import net.minecraft.client.renderer.blockentity.SignRenderer
import net.minecraft.client.renderer.entity.BoatRenderer
import net.minecraft.client.renderer.entity.NoopRenderer
import net.minecraft.client.renderer.entity.ThrownItemRenderer
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RenderGuiEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.network.PacketDistributor

@Mod(value = MODID, dist = [Dist.CLIENT])
@EventBusSubscriber(modid = MODID, value = [Dist.CLIENT])
class WitcheryClient(container: ModContainer) {

    init {
        container.registerExtensionPoint(
            IConfigScreenFactory::class.java,
            IConfigScreenFactory(::ConfigurationScreen)
        )
    }

    companion object {
        @JvmStatic
        @SubscribeEvent
        fun onClientSetup(event: FMLClientSetupEvent) {

        }

        @SubscribeEvent
        fun onMouseScrolled(event: InputEvent.MouseScrollingEvent){
            AfflictionAbilityHandler.scroll(event, Minecraft.getInstance(), event.mouseX, event.mouseY)
        }

        @SubscribeEvent
        fun onRenderHud(event: RenderGuiEvent) {
            InfusionHandler.renderInfusionHud(event.guiGraphics, event.partialTick)
            ManifestationHandler.renderHud(event.guiGraphics, event.partialTick)
            VampireClientSpecificEventHandler.renderHud(event.guiGraphics)
            BarkBeltHandler.renderHud(event.guiGraphics, event.partialTick)
        }

        @SubscribeEvent
        fun onTooltipComponentFactories(event: RegisterClientTooltipComponentFactoriesEvent) {
            event.register(BloodPoolComponent::class.java,
                BloodPoolComponent::getClientTooltipComponent)
        }

        @SubscribeEvent
        fun onClientTick(event: ClientTickEvent.Post){
            while (WitcheryKeyMappings.OPEN_ABILITY_SELECTION.consumeClick()) {
                Minecraft.getInstance().player?.let { player ->
                    AfflictionAbilityHandler.openSelectionScreen(player)
                }
            }
            while (WitcheryKeyMappings.BROOM_DISMOUNT_KEYMAPPING.consumeClick()) {
                Minecraft.getInstance().player?.stopRiding()
                if (Minecraft.getInstance().player != null) {
                    PacketDistributor.sendToServer(DismountBroomC2SPayload())
                }
            }
        }

        @SubscribeEvent
        fun bindContainerRenderers(event: RegisterMenuScreensEvent) {
            event.register(WitcheryMenuTypes.OVEN_MENU_TYPE.get(), ::OvenScreen)
            event.register(WitcheryMenuTypes.ALTAR_MENU_TYPE.get(), ::AltarScreen)
            event.register(WitcheryMenuTypes.DISTILLERY_MENU_TYPE.get(), ::DistilleryScreen)
            event.register(WitcheryMenuTypes.SPINNING_WHEEL_MENU_TYPE.get(), ::SpinningWheelScreen)
        }

        @JvmStatic
        @SubscribeEvent
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

        @JvmStatic
        @SubscribeEvent
        fun registerEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
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
}
