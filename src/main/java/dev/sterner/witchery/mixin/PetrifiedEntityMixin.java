package dev.sterner.witchery.mixin;

import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class PetrifiedEntityMixin {

    @Shadow public abstract int getMaxAirSupply();

    @Inject(
            method = "getAirSupply",
            at = @At("RETURN"),
            cancellable = true
    )
    private void witchery$preventMovement(CallbackInfoReturnable<Integer> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(livingEntity);

            if (data.isPetrified()) {
                cir.setReturnValue(getMaxAirSupply());
            }
        }

    }
}
