package dev.sterner.witchery.mixin.possession;

import dev.sterner.witchery.data_attachment.possession.DisableableAiController;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GoalSelector.class)
public abstract class GoalSelectorMixin implements DisableableAiController {
    @Unique
    private boolean requiem$disabled;

    @Override
    public void requiem$setDisabled(boolean disabled) {
        this.requiem$disabled = disabled;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if (this.requiem$disabled) {
            ci.cancel();
        }
    }
}