package dev.sterner.witchery.registry

import dev.architectury.event.events.common.LifecycleEvent
import dev.sterner.witchery.VillageHelper
import dev.sterner.witchery.Witchery.MODID
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList

object WitcheryStructureInjects {
    fun registerEvents() {
        LifecycleEvent.SERVER_STARTED.register(::addStructure)
    }

    /**
     * Adds Graveyards to Plains and Taiga Villages.
     */
    private fun addStructure(server: MinecraftServer) {
        val builtinTemplate: Registry<StructureTemplatePool> =
            server.registryAccess().registry(Registries.TEMPLATE_POOL).get()
        val builtinProcessor: Registry<StructureProcessorList> =
            server.registryAccess().registry(Registries.PROCESSOR_LIST).get()

        VillageHelper.addBuildingToPool(
            builtinTemplate, builtinProcessor,
            ResourceLocation.parse("minecraft:village/plains/houses"),
            "$MODID:village/houses/plains_graveyard",
            2
        )

        VillageHelper.addBuildingToPool(
            builtinTemplate, builtinProcessor,
            ResourceLocation.parse("minecraft:village/plains/houses"),
            "$MODID:village/houses/plains_graveyard_2",
            2
        )

        VillageHelper.addBuildingToPool(
            builtinTemplate, builtinProcessor,
            ResourceLocation.parse("minecraft:village/plains/houses"),
            "$MODID:village/houses/plains_graveyard_3",
            2
        )

        VillageHelper.addBuildingToPool(
            builtinTemplate, builtinProcessor,
            ResourceLocation.parse("minecraft:village/taiga/houses"),
            "$MODID:village/houses/plains_graveyard",
            2
        )

        VillageHelper.addBuildingToPool(
            builtinTemplate, builtinProcessor,
            ResourceLocation.parse("minecraft:village/taiga/houses"),
            "$MODID:village/houses/plains_graveyard_2",
            2
        )

        VillageHelper.addBuildingToPool(
            builtinTemplate, builtinProcessor,
            ResourceLocation.parse("minecraft:village/taiga/houses"),
            "$MODID:village/houses/plains_graveyard_3",
            2
        )

    }
}