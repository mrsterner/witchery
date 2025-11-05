package dev.sterner.witchery.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sterner.witchery.Witchery;
import dev.sterner.witchery.features.petrification.PetrificationTextureManager;
import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererPetrifiedMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    protected LivingEntityRendererPetrifiedMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"
            )
    )
    public <E extends Entity> void witchery$fixPetrifiedPose(
            EntityModel<E> instance,
            E entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float headYaw,
            float headPitch,
            Operation<Void> original
    ) {
        if (entity instanceof LivingEntity livingEntity) {
            PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(livingEntity);
            if (data.isPetrified()) {
                original.call(
                        instance,
                        entity,
                        data.getLimbSwing(),
                        data.getLimbSwingAmount(),
                        data.getAge(),
                        data.getHeadYaw(),
                        data.getHeadPitch()
                );
                return;
            }
        }
        original.call(instance, entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;prepareMobModel(Lnet/minecraft/world/entity/Entity;FFF)V"
            )
    )
    public <E extends Entity> void witchery$fixPrepModel(
            EntityModel<E> instance,
            E entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            Operation<Void> original
    ) {
        if (entity instanceof LivingEntity livingEntity) {
            PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(livingEntity);
            if (data.isPetrified()) {
                original.call(instance, entity, data.getLimbSwing(), data.getLimbSwingAmount(), partialTick);
                return;
            }
        }
        original.call(instance, entity, limbSwing, limbSwingAmount, partialTick);
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
                    shift = At.Shift.AFTER
            )
    )
    private void witchery$renderBreakOverlay(
            T entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            CallbackInfo ci
    ) {
        PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(entity);

        if (!data.isPetrified() || data.getBreakProgress() == 0) {
            return;
        }

        ResourceLocation baseTexture = this.getTextureLocation(entity);
        var size = PetrificationTextureManager.INSTANCE.getTextureSize(baseTexture);

        ResourceLocation breakTexture = witchery$getBreak(size, data);

        EntityModel<?> model = ((LivingEntityRenderer<?, ?>) (Object) this).getModel();
        VertexConsumer vertexConsumer = buffer.getBuffer(
                RenderType.entityCutout(breakTexture)
        );

        model.renderToBuffer(
                poseStack,
                vertexConsumer,
                packedLight,
                LivingEntityRenderer.getOverlayCoords(entity, 0.0F),
                -1
        );
    }

    @Unique
    private static ResourceLocation witchery$getBreak(PetrificationTextureManager.Size size, PetrifiedEntityAttachment.Data data) {
        float texW = size.getWidth();
        float texH = size.getHeight();
        float aspect = texW / texH;

        ResourceLocation breakTexture;
        int breakStage = data.getBreakStage();

        if (aspect >= 2.0) {
            breakTexture = Witchery.Companion.id(
                    "textures/block/break/destroy_stage_" + breakStage + "_64x32.png"
            );
        } else {
            breakTexture = Witchery.Companion.id(
                    "textures/block/break/destroy_stage_" + breakStage + ".png"
            );
        }
        return breakTexture;
    }
}