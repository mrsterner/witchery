package dev.sterner.witchery.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererPetrifiedMixin<T extends Entity> {

    @WrapWithCondition(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"))
    public boolean witchery$setupAnim(EntityModel<T> instance, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity instanceof LivingEntity livingEntity) {
            PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(livingEntity);
            return !data.isPetrified();
        }

        return true;
    }

    @WrapWithCondition(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;prepareMobModel(Lnet/minecraft/world/entity/Entity;FFF)V"
            )
    )
    public boolean witchery$prepModel(EntityModel<T> instance, T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        if (entity instanceof LivingEntity livingEntity) {
            PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(livingEntity);
            if (data.isPetrified()) {

                livingEntity.yBodyRotO = data.getBodyRot();
                livingEntity.yBodyRot = data.getBodyRot();
                livingEntity.yHeadRot = livingEntity.yHeadRotO;
                return false;
            }
        }
        return true;
    }
}
