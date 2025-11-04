package dev.sterner.witchery.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererPetrifiedMixin<T extends Entity> {

    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"))
    public void witchery$setupAnim(EntityModel<T> instance, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, Operation<Void> original) {
        if (entity instanceof LivingEntity livingEntity) {
            PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(livingEntity);
            if (data.isPetrified()) {
                original.call(instance, entity, data.getLimbSwing(), data.getLimbSwingAmount(), data.getAge(), data.getHeadYaw(), data.getHeadPitch());
            } else {
                original.call(instance, entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
            }
        } else {
            original.call(instance, entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        }
    }


    @WrapOperation(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;prepareMobModel(Lnet/minecraft/world/entity/Entity;FFF)V"
            )
    )
    public void witchery$prepModel(EntityModel<T> instance, T entity, float limbSwing, float limbSwingAmount, float partialTick, Operation<Void> original) {
        if (entity instanceof LivingEntity livingEntity) {
            PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(livingEntity);
            if (data.isPetrified()) {

                LivingEntity renderEntity = (LivingEntity) entity.getType().create(entity.level());
                if (renderEntity != null) {
                    renderEntity.walkAnimation.setSpeed(data.getLimbSwingAmount());
                    renderEntity.tickCount = (int) data.getAge();

                    renderEntity.yHeadRot = livingEntity.yHeadRot;
                    renderEntity.yHeadRotO = livingEntity.yHeadRotO;
                    renderEntity.yBodyRot = livingEntity.yBodyRot;
                    renderEntity.yBodyRotO = livingEntity.yBodyRotO;

                    original.call(instance, renderEntity, data.getLimbSwing(), data.getLimbSwingAmount(), partialTick);
                }

            } else {
                original.call(instance, entity, limbSwing, limbSwingAmount, partialTick);
            }
        }
    }

}
