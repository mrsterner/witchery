package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Brain.class)
public class BrainMixin<E extends LivingEntity> {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void witchery$tick(CallbackInfo ci, @Local(argsOnly = true) E entity) {
        if (entity instanceof LivingEntity) {
            PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(entity);

            if (data.isPetrified()) {
                ci.cancel();
            }
        }
    }
}
