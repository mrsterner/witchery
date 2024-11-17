package dev.sterner.witchery.mixin;

import dev.sterner.witchery.api.VillagerTransfix;
import dev.sterner.witchery.payload.SpawnTransfixParticlesS2CPayload;
import dev.sterner.witchery.registry.WitcheryPayloads;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class VillagerMixin implements VillagerTransfix {

    @Unique int witchery$transfixCounter = 0;
    @Unique Vec3 witchery$transfixVector = null;

    @Override
    public void setTransfixedLookVector(@NotNull Vec3 vec3) {
        witchery$transfixVector = vec3;
        witchery$transfixCounter = 20 * 10;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void onTick(CallbackInfo ci) {

        Villager villager = Villager.class.cast(this);

        if (!villager.level().isClientSide) {
            if (witchery$transfixCounter > 0) {
                villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                villager.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
                villager.getLookControl().setLookAt(witchery$transfixVector);

                WitcheryPayloads.INSTANCE.sendToPlayers(villager.level(), new SpawnTransfixParticlesS2CPayload(villager.position(), witchery$transfixCounter < 20));

                witchery$transfixCounter--;
            } else {
                witchery$transfixVector = null;
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putInt("WitcheryTransfixCounter", witchery$transfixCounter);
        if (witchery$transfixVector != null) {
            compoundTag.putDouble("WitcheryTransfixVectorX", witchery$transfixVector.x);
            compoundTag.putDouble("WitcheryTransfixVectorY", witchery$transfixVector.y);
            compoundTag.putDouble("WitcheryTransfixVectorZ", witchery$transfixVector.z);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        witchery$transfixCounter = compoundTag.getInt("WitcheryTransfixCounter");
        if (compoundTag.contains("WitcheryTransfixVectorX")) {
            witchery$transfixVector =
                    new Vec3(
                            compoundTag.getDouble("WitcheryTransfixVectorX"),
                            compoundTag.getDouble("WitcheryTransfixVectorY"),
                            compoundTag.getDouble("WitcheryTransfixVectorZ")
                    );
        }
    }

    @Override
    public boolean isTransfixed() {
        return witchery$transfixCounter > 0;
    }
}
