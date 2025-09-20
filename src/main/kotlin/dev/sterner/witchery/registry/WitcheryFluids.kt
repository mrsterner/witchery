package dev.sterner.witchery.registry

import dev.architectury.core.fluid.ArchitecturyFlowingFluid
import dev.architectury.core.fluid.ArchitecturyFluidAttributes
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid

object WitcheryFluids {
    val FLUIDS: DeferredRegister<Fluid> = DeferredRegister.create(Witchery.MODID, Registries.FLUID)
    val FLUIDS_INFOS: MutableList<ArchitecturyFluidAttributes> = mutableListOf()

    val FLOWING_FLOWING_SPIRIT: RegistrySupplier<FlowingFluid> = FLUIDS.register(
        "flowing_flowing_spirit"
    ) {
        ArchitecturyFlowingFluid.Flowing(
            BLOOD_ATTRIBUTES
        )
    }

    val FLOWING_SPIRIT_STILL: RegistrySupplier<FlowingFluid> = FLUIDS.register(
        "flowing_spirit_still"
    ) {
        ArchitecturyFlowingFluid.Source(
            BLOOD_ATTRIBUTES
        )
    }

    private val BLOOD_ATTRIBUTES: ArchitecturyFluidAttributes = SimpleArchitecturyFluidAttributes.ofSupplier(
        { FLOWING_FLOWING_SPIRIT },
        { FLOWING_SPIRIT_STILL })
        .blockSupplier { WitcheryBlocks.FLOWING_SPIRIT_BLOCK }
        .bucketItemSupplier { WitcheryItems.FLOWING_SPIRIT_BUCKET }
        .slopeFindDistance(4)
        .dropOff(1)
        .tickDelay(8)
        .explosionResistance(100.0f)
        .convertToSource(false)
        .sourceTexture(ResourceLocation.fromNamespaceAndPath(Witchery.MODID, "block/flowing_spirit_still"))
        .flowingTexture(ResourceLocation.fromNamespaceAndPath(Witchery.MODID, "block/flowing_spirit_flowing"))

    fun register() {
        FLUIDS_INFOS.add(BLOOD_ATTRIBUTES)
    }
}