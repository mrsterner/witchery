package dev.sterner.witchery.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.sterner.witchery.mixin.LivingEntityAccessor;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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

                bat.setShiftKeyDown(entity.isShiftKeyDown());
                bat.setSprinting(entity.isSprinting());
                bat.setSwimming(entity.isSwimming());
                bat.setInvisible(entity.isInvisible());
                bat.setGlowingTag(entity.hasGlowingTag());
                bat.setAirSupply(entity.getAirSupply());
                bat.setCustomName(entity.getCustomName());
                bat.setCustomNameVisible(entity.isCustomNameVisible());
                bat.setPose(entity.getPose());
                bat.setTicksFrozen(entity.getTicksFrozen());

                bat.setOnGround(entity.onGround());
                bat.horizontalCollision = entity.horizontalCollision;
                bat.verticalCollision = entity.verticalCollision;
                bat.verticalCollisionBelow = entity.verticalCollisionBelow;
                bat.minorHorizontalCollision = entity.minorHorizontalCollision;
                bat.setSharedFlagOnFire(entity.isOnFire());
                bat.invulnerableTime = entity.invulnerableTime;
                bat.noCulling = entity.noCulling;
                bat.isInPowderSnow = entity.isInPowderSnow;
                bat.wasInPowderSnow = entity.wasInPowderSnow;
                bat.wasOnFire = entity.wasOnFire;

                bat.swingingArm = entity.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                bat.deathTime = entity.deathTime;
                //bat.walkAnimation = entity.walkAnimation;

                float swimAmt = ((LivingEntityAccessor) entity).getSwimAmount();
                ((LivingEntityAccessor) bat).setSwimAmount(swimAmt);

                float swimAmtO = ((LivingEntityAccessor) entity).getSwimAmountO();
                ((LivingEntityAccessor) bat).setSwimAmountO(swimAmtO);
                bat.startUsingItem(entity.getUsedItemHand() == null ? InteractionHand.MAIN_HAND : entity.getUsedItemHand());
                
                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(bat)
                                .render(bat, entityYaw, partialTicks, poseStack, buffer, packedLight);
                ci.cancel();
            }
        }
    }

    @ModifyArg(method = "getRenderOffset(Lnet/minecraft/client/player/AbstractClientPlayer;F)Lnet/minecraft/world/phys/Vec3;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"), index = 1)
    private double witchery$applyModelScaleToPlayerOffset(double d, @Local(argsOnly = true) AbstractClientPlayer playerEntity) {
        if (TransformationPlayerAttachment.isBat(playerEntity)) {
            return 0.85f / 16;
        }

        return d;
    }
/*
    @ModifyReturnValue(method = "getArmPose", at = @At("RETURN"))
    private static HumanoidModel.ArmPose witchery$canePose(HumanoidModel.ArmPose original, @Local(argsOnly = true) AbstractClientPlayer player, @Local(argsOnly = true) InteractionHand hand){
        if (hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(WitcheryItems.INSTANCE.getCANE_SWORD().get())) {
            return HumanoidModel.ArmPose.BRUSH;
        }
        if (hand == InteractionHand.OFF_HAND && player.getOffhandItem().is(WitcheryItems.INSTANCE.getCANE_SWORD().get())) {
            return HumanoidModel.ArmPose.BRUSH;
        }

        return original;
    }

 */
}
