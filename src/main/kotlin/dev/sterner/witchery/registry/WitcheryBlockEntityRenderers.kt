package dev.sterner.witchery.registry

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry
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
import dev.sterner.witchery.client.renderer.block.SoulCageBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.SpinningWheelBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.SpiritPortalBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.SuspiciousGraveyardDirtBlockEntityRenderer
import dev.sterner.witchery.client.renderer.block.WerewolfAltarBlockEntityRenderer
import net.minecraft.client.renderer.blockentity.HangingSignRenderer
import net.minecraft.client.renderer.blockentity.SignRenderer

object WitcheryBlockEntityRenderers {

    fun register(){
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.PHYLACTERY.get(), ::PhylacteryBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.ALTAR.get(), ::AltarBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CAULDRON.get(), ::CauldronBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.BLOOD_CRUCIBLE.get(), ::BloodCrucibleBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.DISTILLERY.get(), ::DistilleryBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.BRAZIER.get(), ::BrazierBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.COFFIN.get(), ::CoffinBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.SPINNING_WHEEL.get(), ::SpinningWheelBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CUSTOM_SIGN.get(), ::SignRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CUSTOM_HANGING_SIGN.get(), ::HangingSignRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.DREAM_WEAVER.get(), ::DreamWeaverBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.POPPET.get(), ::PoppetBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.SPIRIT_PORTAL.get(), ::SpiritPortalBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.BEAR_TRAP.get(), ::BearTrapBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.BRUSHABLE_BLOCK.get(), ::SuspiciousGraveyardDirtBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.SACRIFICIAL_CIRCLE.get(), ::SacrificialCircleBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.GRASSPER.get(), ::GrassperBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CRITTER_SNARE.get(), ::CritterSnareBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.EFFIGY.get(), ::EffigyBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.MUSHROOM_LOG.get(), ::MushroomLogBlockEntityRenderer)
    }
}