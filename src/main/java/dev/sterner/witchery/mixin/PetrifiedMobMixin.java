package dev.sterner.witchery.mixin;

import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class PetrifiedMobMixin {

    @Inject(method = "serverAiStep()V", at = @At("HEAD"), cancellable = true)
    public void witchery$serverAiStep(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(entity);

        if (data.isPetrified()) {
            ci.cancel();
        }
    }

    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    public void witchery$isSunBurnTick(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(entity);

        if (data.isPetrified()) {
            cir.setReturnValue(false);
        }
    }
}