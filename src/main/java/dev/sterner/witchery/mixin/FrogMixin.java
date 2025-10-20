package dev.sterner.witchery.mixin;


import dev.sterner.witchery.features.ritual.RainingToadAttachment;
import net.minecraft.world.entity.animal.frog.Frog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Frog.class)
public abstract class FrogMixin extends FrogEntityMixin {

    @Inject(method = "calculateFallDamage", at = @At("HEAD"), cancellable = true)
    private void preventFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
        Frog self = Frog.class.cast(this);
        RainingToadAttachment.Data data = RainingToadAttachment.getData(self);
        if (data.getSafeFall()) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void witchery$tick(CallbackInfo ci){
        Frog self = Frog.class.cast(this);

        if (!self.level().isClientSide) {
            RainingToadAttachment.Data data = RainingToadAttachment.getData(self);

            if (data.isPoisonous() && self.getAge() > 20 * 60 * 2) {
                data.setPoisonous(false);
                RainingToadAttachment.setData(self, data);
            }
        }
    }
}
