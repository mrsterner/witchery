package dev.sterner.witchery.mixin;

import dev.sterner.witchery.core.api.interfaces.VillagerTransfix;
import dev.sterner.witchery.network.SpawnTransfixParticlesS2CPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Villager.class)
public class VillagerMixin implements VillagerTransfix {

    @Unique
    int witchery$transfixCounter = 0;
    @Unique
    Vec3 witchery$transfixVector = null;

    @Unique
    UUID witchery$mesmerisedUUID = null;
    @Unique
    int witchery$mesmerisedUUIDCounter = 0;

    @Override
    public void setTransfixedLookVector(@NotNull Vec3 vec3) {
        witchery$transfixVector = vec3;
        witchery$transfixCounter = 20 * 10;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void witchery$onTick(CallbackInfo ci) {

        Villager villager = Villager.class.cast(this);

        if (!villager.level().isClientSide) {
            if (witchery$transfixCounter > 0) {
                if (witchery$mesmerisedUUIDCounter <= 0) {
                    villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                    villager.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
                }
                villager.getLookControl().setLookAt(witchery$transfixVector);

                PacketDistributor.sendToPlayersTrackingEntityAndSelf(villager, new SpawnTransfixParticlesS2CPayload(villager.position(), witchery$transfixCounter < 20));

                witchery$transfixCounter--;
            } else {
                witchery$transfixVector = null;
            }

            if (witchery$mesmerisedUUIDCounter > 0) {
                var player = villager.level().getPlayerByUUID(witchery$getMesmerized());
                if (player != null) {
                    villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(player.position(), 1f, 2));
                }
                witchery$mesmerisedUUIDCounter--;
            } else {
                witchery$mesmerisedUUID = null;
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void witchery$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putInt("WitcheryTransfixCounter", witchery$transfixCounter);
        compoundTag.putInt("WitcheryMesmerisedUUIDCounter", witchery$mesmerisedUUIDCounter);
        if (witchery$transfixVector != null) {
            compoundTag.putDouble("WitcheryTransfixVectorX", witchery$transfixVector.x);
            compoundTag.putDouble("WitcheryTransfixVectorY", witchery$transfixVector.y);
            compoundTag.putDouble("WitcheryTransfixVectorZ", witchery$transfixVector.z);
        }
        if (witchery$mesmerisedUUID != null) {
            compoundTag.putUUID("WitcheryMesmerisedUUID", witchery$mesmerisedUUID);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void witchery$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        witchery$transfixCounter = compoundTag.getInt("WitcheryTransfixCounter");
        witchery$mesmerisedUUIDCounter = compoundTag.getInt("WitcheryMesmerisedUUIDCounter");
        if (compoundTag.contains("WitcheryTransfixVectorX")) {
            witchery$transfixVector =
                    new Vec3(
                            compoundTag.getDouble("WitcheryTransfixVectorX"),
                            compoundTag.getDouble("WitcheryTransfixVectorY"),
                            compoundTag.getDouble("WitcheryTransfixVectorZ")
                    );
        }
        if (compoundTag.contains("WitcheryMesmerisedUUID")) {
            witchery$mesmerisedUUID = compoundTag.getUUID("WitcheryMesmerisedUUID");
        }
    }

    @Override
    public boolean witchery$isTransfixed() {
        return witchery$transfixCounter > 0;
    }

    @Override
    public void witchery$setMesmerized(@NotNull UUID uuid) {
        this.witchery$mesmerisedUUID = uuid;
        this.witchery$mesmerisedUUIDCounter = 20 * 20;
    }

    @Override
    public boolean witchery$isMesmerized() {
        return witchery$mesmerisedUUID != null;
    }

    @NotNull
    @Override
    public UUID witchery$getMesmerized() {
        return witchery$mesmerisedUUID;
    }
}
