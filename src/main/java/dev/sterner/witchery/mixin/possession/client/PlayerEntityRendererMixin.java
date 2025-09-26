package dev.sterner.witchery.mixin.possession.client;


import com.mojang.blaze3d.vertex.PoseStack;
import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment;
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    @Shadow protected abstract void setModelProperties(AbstractClientPlayer clientPlayer);

    public PlayerEntityRendererMixin(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Unique
    private static void setupRenderDelegate(LivingEntity rendered, LivingEntity delegate) {
        delegate.yBodyRot = rendered.yBodyRot;
        delegate.yBodyRotO = rendered.yBodyRotO;
        delegate.setYRot(rendered.getYRot());
        delegate.yRotO = rendered.yRotO;

        delegate.setXRot(rendered.getXRot());
        delegate.xRotO = rendered.xRotO;

        delegate.yHeadRot = rendered.yHeadRot;
        delegate.yHeadRotO = rendered.yHeadRotO;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Player rendering hijack part 1
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Prevents players possessing something from being rendered, and renders their possessed entity
     * instead. This both prevents visual stuttering from position desync and lets mods render the player
     * correctly.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cancelRender(AbstractClientPlayer renderedPlayer, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int lightmap, CallbackInfo ci) {
        LivingEntity possessedEntity = PossessionComponentAttachment.INSTANCE.get(renderedPlayer).getHost();
        if (possessedEntity != null) {
            if (renderedPlayer == Minecraft.getInstance().player) {
                setupRenderDelegate(renderedPlayer, possessedEntity);
                this.entityRenderDispatcher.render(possessedEntity, 0, 0, 0, yaw, tickDelta, matrices, vertexConsumers, lightmap);
            }
            ci.cancel();
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Hand rendering hijack
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    @Inject(method = "renderRightHand", at = @At("HEAD"), cancellable = true)
    private void renderRightArm(PoseStack matrices, MultiBufferSource vertices, int lightmap, AbstractClientPlayer renderedPlayer, CallbackInfo ci) {
        if (requiem_renderPossessedArm(matrices, vertices, renderedPlayer, lightmap, true)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftHand", at = @At("HEAD"), cancellable = true)
    private void renderLeftArm(PoseStack matrices, MultiBufferSource vertices, int lightmap, AbstractClientPlayer renderedPlayer, CallbackInfo ci) {
        if (requiem_renderPossessedArm(matrices, vertices, renderedPlayer, lightmap, false)) {
            ci.cancel();
        }
    }

    @Unique
    private boolean requiem_renderPossessedArm(PoseStack matrices, MultiBufferSource vertices, AbstractClientPlayer renderedPlayer, int lightmapCoordinates, boolean rightArm) {
        if (AfflictionPlayerAttachment.getData(renderedPlayer).isVagrant()) {
            LivingEntity possessed = PossessionComponentAttachment.INSTANCE.get(renderedPlayer).getHost();
            if (possessed != null) {
                EntityRenderer<? super LivingEntity> possessedRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(possessed);
                // If the mob has an arm, render it instead of the player's
                if (possessedRenderer instanceof RenderLayerParent) {
                    Model possessedModel = ((RenderLayerParent<?, ?>) possessedRenderer).getModel();
                    if (possessedModel instanceof HumanoidModel) {
                        @SuppressWarnings("unchecked") HumanoidModel<LivingEntity> bipedModel = (HumanoidModel<LivingEntity>) possessedModel;
                        PlayerModel<AbstractClientPlayer> playerModel = this.getModel();
                        ModelPart arm = rightArm ? bipedModel.rightArm : bipedModel.leftArm;
                        this.setModelProperties(renderedPlayer);
                        bipedModel.leftArmPose = playerModel.leftArmPose;
                        bipedModel.rightArmPose = playerModel.rightArmPose;
                        bipedModel.attackTime = 0.0F;
                        bipedModel.crouching = false;
                        bipedModel.setupAnim(possessed, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                        arm.xRot = 0.0F;
                        arm.render(matrices, vertices.getBuffer(possessedModel.renderType((possessedRenderer).getTextureLocation(possessed))), lightmapCoordinates, OverlayTexture.NO_OVERLAY);
                    }
                }
            }
            // prevent rendering a soul's arm regardless
            return true;
        }
        return false;
    }
}
