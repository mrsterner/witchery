package dev.sterner.witchery;

import com.mojang.serialization.MapCodec;
import dev.sterner.witchery.core.registry.WitcheryEntityTypes;
import dev.sterner.witchery.core.registry.WitcheryStructureProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CovenWitchProcessor extends StructureProcessor {

    public static final MapCodec<CovenWitchProcessor> CODEC = MapCodec.unit(CovenWitchProcessor::new);

    private boolean hasSpawnedWitch = false;

    @Override
    public @NotNull List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor serverLevel,
            BlockPos offset,
            BlockPos pos,
            List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructurePlaceSettings settings
    ) {

        if (!hasSpawnedWitch) {
            hasSpawnedWitch = true;

            BoundingBox boundingBox = settings.getBoundingBox();
            if (boundingBox != null) {
                int centerX = (boundingBox.minX() + boundingBox.maxX()) / 2;
                int centerZ = (boundingBox.minZ() + boundingBox.maxZ()) / 2;

                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(centerX, boundingBox.maxY(), centerZ);

                var witch = WitcheryEntityTypes.INSTANCE.getCOVEN_WITCH().get().create(serverLevel.getLevel());
                if (witch != null) {
                    witch.moveTo(
                            centerX + 0.5,
                            mutablePos.getY() - 4.0,
                            centerZ + 0.5,
                            0f,
                            0f
                    );
                    witch.setPersistenceRequired();
                    witch.setIsCoven(false);

                    witch.finalizeSpawn(
                            serverLevel,
                            serverLevel.getCurrentDifficultyAt(witch.blockPosition()),
                            MobSpawnType.STRUCTURE,
                            null
                    );

                    serverLevel.addFreshEntity(witch);
                }
            }
        }

        return super.finalizeProcessing(serverLevel, offset, pos, originalBlockInfos, processedBlockInfos, settings);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return WitcheryStructureProcessors.INSTANCE.getCOVEN_WITCH_PROCESSOR().get();
    }
}