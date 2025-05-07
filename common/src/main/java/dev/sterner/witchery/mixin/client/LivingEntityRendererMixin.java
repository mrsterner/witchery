package dev.sterner.witchery.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.sterner.witchery.handler.transformation.TransformationHandler;
import dev.sterner.witchery.platform.ManifestationPlayerAttachment;
import dev.sterner.witchery.platform.EtherealEntityAttachment;
import dev.sterner.witchery.platform.infusion.LightInfusionDataAttachment;
import dev.sterner.witchery.registry.WitcheryRenderTypes;
import dev.sterner.witchery.registry.WitcheryTags;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    @Shadow protected M model;

    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @ModifyReturnValue(method = "getShadowRadius(Lnet/minecraft/world/entity/LivingEntity;)F", at = @At("RETURN"))
    private float witchery$getShadowRadius(float original, @Local(argsOnly = true) T entity) {
        if (entity instanceof Player player) {
            if (LightInfusionDataAttachment.isInvisible(player).isInvisible()) {
                return 0f;
            }
            if (TransformationHandler.isBat(player)) {
                return original / 2f;
            }
            if (TransformationHandler.isWolf(player)) {
                return original / 1.5f;
            }
        }
        return original;
    }

    @WrapWithCondition(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
    private boolean witchery$manifestationAlpha(EntityModel<T> instance, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color, @Local MultiBufferSource source, @Local T entity){
        if(entity instanceof Player player) {
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

    @ModifyArgs(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal = 0))
    private void witchery$applyModelScales(Args args, LivingEntity livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource bufferSource, int i) {
        if (livingEntity instanceof Player player && (TransformationHandler.isBat(player) || TransformationHandler.isWolf(player))) {
            float x = args.get(0);
            float y = args.get(1);
            float z = args.get(2);
            args.set(0, x * 0.75);
            args.set(1, y * 0.75);
            args.set(2, z * 0.75);
        }
    }

    @ModifyReturnValue(
            method = "getRenderType",
            at = @At(value = "RETURN")
    )
    private RenderType witchery$necroMod(@Nullable RenderType original, @Local(argsOnly = true) T livingEntity){
        if (livingEntity.getType().is(WitcheryTags.INSTANCE.getNECROMANCER_SUMMONABLE())) {
            var bl = EtherealEntityAttachment.getData(livingEntity).isEthereal();
            if (bl) {
                ResourceLocation resourceLocation = this.getTextureLocation(livingEntity);
                return WitcheryRenderTypes.INSTANCE.getGHOST().apply(resourceLocation);
            }
        }
        return original;
    }
}
