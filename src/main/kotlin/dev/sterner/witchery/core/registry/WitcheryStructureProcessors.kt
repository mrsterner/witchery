package dev.sterner.witchery.core.registry

import com.google.common.base.Supplier
import dev.sterner.witchery.CovenWitchProcessor
import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object WitcheryStructureProcessors {

    val STRUCTURE_PROCESSOR_TYPES: DeferredRegister<StructureProcessorType<*>> =
        DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, Witchery.MODID)

    val COVEN_WITCH_PROCESSOR: DeferredHolder<StructureProcessorType<*>, StructureProcessorType<CovenWitchProcessor>> =
        STRUCTURE_PROCESSOR_TYPES.register("coven_witch_processor", Supplier {
            StructureProcessorType { CovenWitchProcessor.CODEC }
        })
}