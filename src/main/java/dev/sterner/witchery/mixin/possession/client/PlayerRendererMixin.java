package dev.sterner.witchery.mixin.possession.client;


import com.mojang.blaze3d.vertex.PoseStack;
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment;
import dev.sterner.witchery.features.possession.PossessionComponentAttachment;
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
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    @Shadow protected abstract void setModelProperties(AbstractClientPlayer clientPlayer);

    public PlayerRendererMixin(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void witchery$render(AbstractClientPlayer renderedPlayer, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int lightmap, CallbackInfo ci) {
        LivingEntity possessedEntity = PossessionComponentAttachment.INSTANCE.get(renderedPlayer).getHost();
        if (possessedEntity != null) {
            if (renderedPlayer == Minecraft.getInstance().player) {
                possessedEntity.yBodyRot = renderedPlayer.yBodyRot;
                possessedEntity.yBodyRotO = renderedPlayer.yBodyRotO;
                possessedEntity.setYRot(renderedPlayer.getYRot());
                possessedEntity.yRotO = renderedPlayer.yRotO;

                possessedEntity.setXRot(renderedPlayer.getXRot());
                possessedEntity.xRotO = renderedPlayer.xRotO;

                possessedEntity.yHeadRot = renderedPlayer.yHeadRot;
                possessedEntity.yHeadRotO = renderedPlayer.yHeadRotO;

                this.entityRenderDispatcher.render(possessedEntity, 0, 0, 0, yaw, tickDelta, matrices, vertexConsumers, lightmap);
            }
            ci.cancel();
        }
    }

    @Inject(method = "renderRightHand", at = @At("HEAD"), cancellable = true)
    private void witchery$renderRightHand(PoseStack matrices, MultiBufferSource vertices, int lightmap, AbstractClientPlayer renderedPlayer, CallbackInfo ci) {
        if (witchery$renderPossessedArm(matrices, vertices, renderedPlayer, lightmap, true)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftHand", at = @At("HEAD"), cancellable = true)
    private void witchery$renderLeftHand(PoseStack matrices, MultiBufferSource vertices, int lightmap, AbstractClientPlayer renderedPlayer, CallbackInfo ci) {
        if (witchery$renderPossessedArm(matrices, vertices, renderedPlayer, lightmap, false)) {
            ci.cancel();
        }
    }

    @Unique
    private boolean witchery$renderPossessedArm(PoseStack matrices, MultiBufferSource vertices, AbstractClientPlayer renderedPlayer, int lightmapCoordinates, boolean rightArm) {
        var vag = AfflictionPlayerAttachment.getData(renderedPlayer).isVagrant();
        if (vag) {
            LivingEntity possessed = PossessionComponentAttachment.INSTANCE.get(renderedPlayer).getHost();
            if (possessed != null) {
                EntityRenderer<? super LivingEntity> possessedRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(possessed);
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
            return true;
        }
        return false;
    }
}
