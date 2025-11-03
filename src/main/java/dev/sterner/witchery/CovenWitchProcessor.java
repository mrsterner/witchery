package dev.sterner.witchery;

import com.mojang.serialization.MapCodec;
import dev.sterner.witchery.core.registry.WitcheryBlocks;
import dev.sterner.witchery.core.registry.WitcheryEntityTypes;
import dev.sterner.witchery.core.registry.WitcheryStructureProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.UUID;

public class CovenWitchProcessor extends StructureProcessor {

    public static final MapCodec<CovenWitchProcessor> CODEC = MapCodec.unit(CovenWitchProcessor::new);

    @Override
    public StructureTemplate.StructureEntityInfo processEntity(
            LevelReader world,
            BlockPos seedPos,
            StructureTemplate.StructureEntityInfo rawEntityInfo,
            StructureTemplate.StructureEntityInfo entityInfo,
            StructurePlaceSettings placementSettings, StructureTemplate template
    ) {
        if (entityInfo.nbt.getString("id").equals(WitcheryEntityTypes.INSTANCE.getCOVEN_WITCH().getId().toString())) {

            CompoundTag newNbt = entityInfo.nbt.copy();

            UUID newUuid = UUID.randomUUID();
            newNbt.putUUID("UUID", newUuid);

            BlockPos witchPos = entityInfo.blockPos;
            for (int x = -10; x <= 10; x++) {
                for (int y = -5; y <= 5; y++) {
                    for (int z = -10; z <= 10; z++) {
                        BlockPos checkPos = witchPos.offset(x, y, z);
                        if (world.getBlockState(checkPos).is(WitcheryBlocks.INSTANCE.getPOTTED_ROWAN_SAPLING().get())) {
                            CompoundTag homeTag = new CompoundTag();
                            homeTag.putInt("X", checkPos.getX());
                            homeTag.putInt("Y", checkPos.getY());
                            homeTag.putInt("Z", checkPos.getZ());
                            newNbt.put("HomePos", homeTag);

                            break;
                        }
                    }
                }
            }

            return new StructureTemplate.StructureEntityInfo(
                    entityInfo.pos,
                    entityInfo.blockPos,
                    newNbt
            );
        }

        return super.processEntity(world, seedPos, rawEntityInfo, entityInfo, placementSettings, template);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return WitcheryStructureProcessors.INSTANCE.getCOVEN_WITCH_PROCESSOR().get();
    }
}