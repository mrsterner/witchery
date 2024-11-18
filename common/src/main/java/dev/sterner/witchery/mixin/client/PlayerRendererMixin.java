package dev.sterner.witchery.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.sterner.witchery.platform.ManifestationPlayerAttachment;
import dev.sterner.witchery.platform.infusion.LightInfusionDataAttachment;
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "setModelProperties", at = @At("TAIL"))
    private void witchery$lightInfusionInvisibility(AbstractClientPlayer clientPlayer, CallbackInfo ci){
        if(LightInfusionDataAttachment.isInvisible(clientPlayer).isInvisible()){
            var model = getModel();
            model.setAllVisible(false);
        }
    }

    @WrapWithCondition(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private boolean witchery$renderGhostHand(ModelPart instance, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                                             @Local(argsOnly = true) MultiBufferSource multiBufferSource,
                                             @Local(argsOnly = true) AbstractClientPlayer player,
                                             @Local ResourceLocation resourceLocation){
        if (ManifestationPlayerAttachment.getData(player).getManifestationTimer() > 0) {
            int originalAlpha = (654311423 >> 24) & 0xFF;
            int reducedAlpha = (int) (originalAlpha * 0.75) << 24;
            int colorWithReducedAlpha = (654311423 & 0x00FFFFFF) | reducedAlpha;
            instance.render(poseStack, multiBufferSource.getBuffer(RenderType.entityTranslucent(resourceLocation)), packedLight, OverlayTexture.NO_OVERLAY, colorWithReducedAlpha);
            return false;
        }
        return true;
    }

    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void witchery$renderTransformation(AbstractClientPlayer entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci){

        if (TransformationPlayerAttachment.getForm(entity) == TransformationPlayerAttachment.TransformationType.BAT) {
            var bat = TransformationPlayerAttachment.getBatEntity(entity);
            if (bat != null) {
                bat.tickCount = entity.tickCount;
                bat.hurtTime = entity.hurtTime;
                bat.hurtDuration = entity.hurtDuration;
                bat.yHeadRot = entity.yHeadRot;
                bat.yBodyRot = entity.yBodyRot;
                bat.yHeadRotO = entity.yHeadRotO;
                bat.yBodyRotO = entity.yBodyRotO;
                bat.swinging = entity.swinging;
                bat.swingTime = entity.swingTime;
                bat.attackAnim = entity.attackAnim;
                bat.oAttackAnim = entity.oAttackAnim;
                bat.setXRot(entity.getXRot());
                bat.xRotO = entity.xRotO;

                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(bat)
                                .render(bat, entityYaw, partialTicks, poseStack, buffer, packedLight);
                ci.cancel();
            }
        }
    }
}
