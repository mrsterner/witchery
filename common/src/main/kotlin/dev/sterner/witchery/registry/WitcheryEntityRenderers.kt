package dev.sterner.witchery.registry

import dev.architectury.registry.client.level.entity.EntityRendererRegistry
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
import net.minecraft.client.renderer.entity.BoatRenderer
import net.minecraft.client.renderer.entity.NoopRenderer
import net.minecraft.client.renderer.entity.ThrownItemRenderer

object WitcheryEntityRenderers {

    fun register(){
        EntityRendererRegistry.register(WitcheryEntityTypes.BROOM) { BroomEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.CHAIN) { ChainEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.MANDRAKE) { MandrakeEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.ENT) { EntEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.BANSHEE) { BansheeEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.BABA_YAGA) { BabaYagaEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.SPECTRE) { SpectreEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.DEMON) { DemonEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.COVEN_WITCH) { CovenWitchEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.PARASITIC_LOUSE) { ParasiticLouseEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.DEATH) { DeathEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.HORNED_HUNTSMAN) { HornedHuntsmanEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.OWL) { OwlEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.INSANITY) { InsanityEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.IMP) { ImpEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.VAMPIRE) { VampireEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.WEREWOLF) { WerewolfEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.NIGHTMARE) { NightmareEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.LILITH) { LilithEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.ELLE) { ElleEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.CUSTOM_BOAT) { context -> BoatRenderer(context, false) }
        EntityRendererRegistry.register(WitcheryEntityTypes.CUSTOM_CHEST_BOAT) { context -> BoatRenderer(context, true) }
        EntityRendererRegistry.register(WitcheryEntityTypes.FLOATING_ITEM, ::FloatingItemEntityRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.THROWN_BREW, ::ThrownItemRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.THROWN_POTION, ::ThrownItemRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.SLEEPING_PLAYER, ::SleepingPlayerEntityRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.SPECTRAL_PIG, ::SpectralPigRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.AREA_EFFECT_CLOUD, ::NoopRenderer)
    }
}