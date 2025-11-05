package dev.sterner.witchery.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.sterner.witchery.features.necromancy.EtherealEntityAttachment;
import dev.sterner.witchery.features.petrification.PetrificationTextureManager;
import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment;
import dev.sterner.witchery.features.spirit_world.ManifestationPlayerAttachment;
import dev.sterner.witchery.core.registry.WitcheryRenderTypes;
import dev.sterner.witchery.core.registry.WitcheryTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    @Shadow
    protected M model;

    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @WrapWithCondition(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
    private boolean witchery$manifestationAlpha(EntityModel<T> instance, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color, @Local MultiBufferSource source, @Local T entity) {
        if (entity instanceof Player player) {
            if (ManifestationPlayerAttachment.getData(player).getManifestationTimer() > 0) {
                int originalAlpha = (654311423 >> 24) & 0xFF;
                int reducedAlpha = (int) (originalAlpha * 0.75) << 24;
                int colorWithReducedAlpha = (654311423 & 0x00FFFFFF) | reducedAlpha;

                VertexConsumer vertexConsumer2 = source.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
                this.model.renderToBuffer(poseStack, vertexConsumer2, packedLight, packedOverlay, colorWithReducedAlpha);

                return false;
            }

        }
        return true;
    }

    @ModifyReturnValue(
            method = "getRenderType",
            at = @At(value = "RETURN")
    )
    private RenderType witchery$necroMod(@Nullable RenderType original, @Local(argsOnly = true) T livingEntity) {
        if (livingEntity.getType().is(WitcheryTags.INSTANCE.getNECROMANCER_SUMMONABLE())) {
            var bl = EtherealEntityAttachment.getData(livingEntity).isEthereal();
            if (bl) {
                ResourceLocation resourceLocation = this.getTextureLocation(livingEntity);
                return WitcheryRenderTypes.INSTANCE.getGHOST().apply(resourceLocation);
            }
        }
        return original;
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD")
    )
    private void witchery$onRenderStart(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        PetrificationTextureManager.INSTANCE.setCurrentEntity(entity);
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN")
    )
    private void witchery$onRenderEnd(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        PetrificationTextureManager.INSTANCE.clearCurrentEntity();
    }

    @ModifyVariable(
            method = "getRenderType",
            at = @At("STORE"),
            ordinal = 0
    )
    private ResourceLocation witchery$modifyTexture(ResourceLocation original, T entity) {
        PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(entity);

        if (!data.isPetrified()) {
            return original;
        }

        return PetrificationTextureManager.INSTANCE.getPetrifiedTexture(original);
    }
}
