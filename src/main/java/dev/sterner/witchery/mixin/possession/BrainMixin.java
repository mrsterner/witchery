package dev.sterner.witchery.mixin.possession;

import dev.sterner.witchery.api.interfaces.DisableableAiController;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Brain.class)
public abstract class BrainMixin implements DisableableAiController {
    @Unique
    private boolean witchery$disabled;

    @Override
    public void witchery$setDisabled(boolean disabled) {
        this.witchery$disabled = disabled;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(ServerLevel world, LivingEntity entity, CallbackInfo ci) {
        if (this.witchery$disabled) {
            ci.cancel();
        }
    }

    @Inject(method = "checkMemory", at = @At("HEAD"), cancellable = true)
    private void checkMemory(MemoryModuleType<?> type, MemoryStatus state, CallbackInfoReturnable<Boolean> cir) {
        if (this.witchery$disabled) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getMemory", at = @At("HEAD"), cancellable = true)
    private void getMemory(MemoryModuleType<?> type, CallbackInfoReturnable<Optional<?>> cir) {
        if (this.witchery$disabled) {
            cir.setReturnValue(Optional.empty());
        }
    }
}