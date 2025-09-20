package dev.sterner.witchery

import dev.sterner.witchery.Witchery.Companion.MODID
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryEntityTypes
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
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory

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

            event.registerEntityRenderer(WitcheryEntityTypes.BROOM) { BroomEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.CHAIN) { ChainEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.MANDRAKE) { MandrakeEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.ENT) { EntEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.BANSHEE) { BansheeEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.BABA_YAGA) { BabaYagaEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.SPECTRE) { SpectreEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.DEMON) { DemonEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.COVEN_WITCH) { CovenWitchEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.PARASITIC_LOUSE) { ParasiticLouseEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.DEATH) { DeathEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.HORNED_HUNTSMAN) { HornedHuntsmanEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.OWL) { OwlEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.INSANITY) { InsanityEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.IMP) { ImpEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.VAMPIRE) { VampireEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.WEREWOLF) { WerewolfEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.NIGHTMARE) { NightmareEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.LILITH) { LilithEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.ELLE) { ElleEntityRenderer(it) }
            event.registerEntityRenderer(WitcheryEntityTypes.CUSTOM_BOAT) { context -> BoatRenderer(context, false) }
            event.registerEntityRenderer(WitcheryEntityTypes.CUSTOM_CHEST_BOAT) { context -> BoatRenderer(context, true) }
            event.registerEntityRenderer(WitcheryEntityTypes.FLOATING_ITEM, ::FloatingItemEntityRenderer)
            event.registerEntityRenderer(WitcheryEntityTypes.THROWN_BREW, ::ThrownItemRenderer)
            event.registerEntityRenderer(WitcheryEntityTypes.THROWN_POTION, ::ThrownItemRenderer)
            event.registerEntityRenderer(WitcheryEntityTypes.SLEEPING_PLAYER, ::SleepingPlayerEntityRenderer)
            event.registerEntityRenderer(WitcheryEntityTypes.SPECTRAL_PIG, ::SpectralPigRenderer)
            event.registerEntityRenderer(WitcheryEntityTypes.AREA_EFFECT_CLOUD, ::NoopRenderer)
            event.registerEntityRenderer(WitcheryEntityTypes.HUNTSMAN_SPEAR, ::HuntsmanSpearRenderer)
        }
    }
}
