package dev.sterner.witchery.mixin.client;


import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityModel.class)
public abstract class PetrifiedAnimationMixin<T extends LivingEntity> {


/*
    @Inject(
            method = "setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void freezeAnimation(T entity, float limbSwing, float limbSwingAmount,
                                 float ageInTicks, float netHeadYaw, float headPitch,
                                 CallbackInfo ci) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(livingEntity);

        if (data.isPetrified() && data.getProgress() > 0.3f) {
            EntityModel<T> model = (EntityModel<T>) (Object) this;

            float freezeFactor = data.getProgress();

            float frozenLimbSwing = limbSwing * (1.0f - freezeFactor);
            float frozenLimbSwingAmount = limbSwingAmount * (1.0f - freezeFactor);

            float frozenHeadYaw = netHeadYaw * (1.0f - freezeFactor);
            float frozenHeadPitch = headPitch * (1.0f - freezeFactor);

            float frozenAgeInTicks = ageInTicks * (1.0f - freezeFactor * 0.8f);

            model.setupAnim(entity, frozenLimbSwing, frozenLimbSwingAmount,
                    frozenAgeInTicks, frozenHeadYaw, frozenHeadPitch);

            ci.cancel();
        }
    }

 */
}
