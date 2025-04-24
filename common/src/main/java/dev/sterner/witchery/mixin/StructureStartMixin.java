package dev.sterner.witchery.mixin;

import dev.sterner.witchery.VillageHelper;
import dev.sterner.witchery.handler.VillageWallHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureStart.class)
public class StructureStartMixin {

    @Inject(method = "placeInChunk", at = @At("HEAD"))
    private void onPlaceVillage(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator generator,
            RandomSource random,
            BoundingBox box,
            ChunkPos chunkPos,
            CallbackInfo ci
    ) {
        StructureStart self = (StructureStart) (Object) this;
        Structure structure = self.getStructure();
        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ResourceKey<Structure> key = structureRegistry.getResourceKey(structure).orElse(null);
        if (key != null && key.location().getPath().contains("village")) {
            BoundingBox bounds = self.getBoundingBox();

            if (VillageWallHandler.INSTANCE.markVillage(bounds)) {
                System.out.println("New village detected at: " + bounds);
            }
        }
    }
}
