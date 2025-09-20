package dev.sterner.witchery.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.registry.WitcheryDataComponents;
import dev.sterner.witchery.registry.WitcheryItems;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> extends AgeableListModel<T> implements ArmedModel, HeadedModel {

    @Shadow
    @Final
    public ModelPart leftArm;

    @Shadow
    @Final
    public ModelPart rightArm;

    @Inject(method = "poseLeftArm", at = @At("TAIL"))
    private void witchery$setLeftArmPose(CallbackInfo ci, @Local T livingEntity) {
        if (livingEntity.getOffhandItem().is(WitcheryItems.INSTANCE.getCANE_SWORD().get())) {
            if (Boolean.FALSE.equals(livingEntity.getOffhandItem().get(WitcheryDataComponents.INSTANCE.getUNSHEETED().get()))) {
                this.leftArm.xRot = this.leftArm.xRot * 1.5F - (float) (Math.PI / 5);
                this.leftArm.yRot = 0.0F;
            }
        }
    }

    @Inject(method = "poseRightArm", at = @At("TAIL"))
    private void witchery$setRightArmPose(CallbackInfo ci, @Local T livingEntity) {
        if (livingEntity.getMainHandItem().is(WitcheryItems.INSTANCE.getCANE_SWORD().get())) {
            if (Boolean.FALSE.equals(livingEntity.getMainHandItem().get(WitcheryDataComponents.INSTANCE.getUNSHEETED().get()))) {
                this.rightArm.xRot = this.rightArm.xRot * 1.5F - (float) (Math.PI / 5);
                this.rightArm.yRot = 0.0F;
            }
        }
    }
}
