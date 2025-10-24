package dev.sterner.witchery;

import com.mojang.datafixers.util.Pair;
import dev.sterner.witchery.mixin.StructureTemplatePoolAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;
import java.util.List;

public class VillageHelper {

    public static void addBuildingToPool(Registry<StructureTemplatePool> templatePoolRegistry,
                                         Registry<StructureProcessorList> processorListRegistry,
                                         ResourceLocation poolRL,
                                         String nbtPieceRL,
                                         int weight) {
        addBuildingToPool(templatePoolRegistry, processorListRegistry, poolRL, nbtPieceRL, weight, null);
    }

    public static void addBuildingToPool(Registry<StructureTemplatePool> templatePoolRegistry,
                                         Registry<StructureProcessorList> processorListRegistry,
                                         ResourceLocation poolRL,
                                         String nbtPieceRL,
                                         int weight,
                                         String processorListId) {

        StructureTemplatePool pool = templatePoolRegistry.get(poolRL);
        if (pool == null) return;

        ResourceLocation processorRL = processorListId != null
                ? ResourceLocation.parse(processorListId)
                : ResourceLocation.withDefaultNamespace("empty");

        Holder<StructureProcessorList> processorHolder = processorListRegistry.getHolderOrThrow(
                ResourceKey.create(Registries.PROCESSOR_LIST, processorRL)
        );

        SinglePoolElement piece = SinglePoolElement.single(nbtPieceRL, processorHolder)
                .apply(StructureTemplatePool.Projection.RIGID);

        for (int i = 0; i < weight; i++) {
            var mut = ((StructureTemplatePoolAccessor) pool).getTemplates();
            mut.add(piece);
            ((StructureTemplatePoolAccessor) pool).setTemplates(mut);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(
                ((StructureTemplatePoolAccessor) pool).getRawTemplates()
        );
        listOfPieceEntries.add(new Pair<>(piece, weight));
        ((StructureTemplatePoolAccessor) pool).setRawTemplates(listOfPieceEntries);
    }
}