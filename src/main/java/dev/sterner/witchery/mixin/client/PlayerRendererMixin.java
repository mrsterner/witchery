package dev.sterner.witchery.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.sterner.witchery.block.coffin.CoffinBlock;
import dev.sterner.witchery.data_attachment.ManifestationPlayerAttachment;
import dev.sterner.witchery.data_attachment.infusion.LightInfusionPlayerAttachment;
import dev.sterner.witchery.handler.affliction.TransformationHandler;
import dev.sterner.witchery.mixin.LivingEntityAccessor;
import dev.sterner.witchery.mixin.WalkAnimationStateAccessor;
import dev.sterner.witchery.registry.WitcheryMobEffects;
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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "setModelProperties", at = @At("TAIL"))
    private void witchery$lightInfusionInvisibility(AbstractClientPlayer clientPlayer, CallbackInfo ci) {
        if (LightInfusionPlayerAttachment.isInvisible(clientPlayer).isInvisible()) {
            var model = getModel();
            model.setAllVisible(false);
        }

        if (clientPlayer.hasEffect(WitcheryMobEffects.INSTANCE.getGROTESQUE())) {
            var model = getModel();
            model.head.visible = false;
        }
    }

    @WrapWithCondition(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private boolean witchery$renderGhostHand(ModelPart instance, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                                             @Local(argsOnly = true) MultiBufferSource multiBufferSource,
                                             @Local(argsOnly = true) AbstractClientPlayer player,
                                             @Local ResourceLocation resourceLocation) {
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
    private void witchery$dontRenderCoffinSleep(AbstractClientPlayer entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        Optional<BlockPos> pos = entity.getSleepingPos();
        if (pos.isPresent() && entity.level().getBlockState(pos.get()).getBlock() instanceof CoffinBlock) {
            ci.cancel();
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void witchery$renderTransformation(AbstractClientPlayer entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (TransformationHandler.isBat(entity)) {
            var bat = TransformationHandler.getBatEntity(entity);
            if (bat != null) {
                witchery$copyTransforms(bat, entity);
                var bl = entity.onGround() && !entity.getAbilities().flying;
                bat.setResting(bl);
                poseStack.pushPose();
                if (bl) {
                    poseStack.translate(0f, 0.85f, 0f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-180f));
                }
                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(bat)
                        .render(bat, entityYaw, partialTicks, poseStack, buffer, packedLight);
                poseStack.popPose();
                ci.cancel();
            }
        } else if (TransformationHandler.isWolf(entity)) {
            var wolf = TransformationHandler.getWolfEntity(entity);
            if (wolf != null) {
                witchery$copyTransforms(wolf, entity);
                wolf.setInSittingPose(entity.isShiftKeyDown());
                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(wolf)
                        .render(wolf, entityYaw, partialTicks, poseStack, buffer, packedLight);
                ci.cancel();
            }
        } else if (TransformationHandler.isWerewolf(entity)) {
            var werewolf = TransformationHandler.getWerewolf(entity);
            if (werewolf != null) {
                witchery$copyTransforms(werewolf, entity);
                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(werewolf)
                        .render(werewolf, entityYaw, partialTicks, poseStack, buffer, packedLight);
                ci.cancel();
            }
        }
    }

    @Unique
    private void witchery$copyTransforms(Mob to, AbstractClientPlayer from) {
        to.tickCount = from.tickCount;
        to.hurtTime = from.hurtTime;
        to.hurtDuration = from.hurtDuration;
        to.yHeadRot = from.yHeadRot;
        to.yBodyRot = from.yBodyRot;
        to.yHeadRotO = from.yHeadRotO;
        to.yBodyRotO = from.yBodyRotO;
        to.swinging = from.swinging;
        to.swingTime = from.swingTime;
        to.attackAnim = from.attackAnim;
        to.oAttackAnim = from.oAttackAnim;
        to.setXRot(from.getXRot());
        to.xRotO = from.xRotO;

        to.setShiftKeyDown(from.isShiftKeyDown());
        to.setSprinting(from.isSprinting());
        to.setSwimming(from.isSwimming());
        to.setInvisible(from.isInvisible());
        to.setGlowingTag(from.hasGlowingTag());
        to.setAirSupply(from.getAirSupply());
        to.setCustomName(from.getCustomName());
        to.setCustomNameVisible(from.isCustomNameVisible());
        to.setPose(from.getPose());
        to.setTicksFrozen(from.getTicksFrozen());

        to.setOnGround(from.onGround());
        to.horizontalCollision = from.horizontalCollision;
        to.verticalCollision = from.verticalCollision;
        to.verticalCollisionBelow = from.verticalCollisionBelow;
        to.minorHorizontalCollision = from.minorHorizontalCollision;
        to.setSharedFlagOnFire(from.isOnFire());
        to.invulnerableTime = from.invulnerableTime;
        to.noCulling = from.noCulling;
        to.isInPowderSnow = from.isInPowderSnow;
        to.wasInPowderSnow = from.wasInPowderSnow;
        to.wasOnFire = from.wasOnFire;

        to.swingingArm = from.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        to.deathTime = from.deathTime;

        WalkAnimationStateAccessor toAccessor = (WalkAnimationStateAccessor) to.walkAnimation;
        WalkAnimationStateAccessor fromAccessor = (WalkAnimationStateAccessor) from.walkAnimation;

        toAccessor.setWalkSpeed(fromAccessor.getWalkSpeed());
        toAccessor.setWalkSpeedOld(fromAccessor.getWalkSpeedOld());
        toAccessor.setWalkPosition(fromAccessor.getWalkPosition());



        float swimAmt = ((LivingEntityAccessor) from).getSwimAmount();
        ((LivingEntityAccessor) to).setSwimAmount(swimAmt);

        float swimAmtO = ((LivingEntityAccessor) from).getSwimAmountO();
        ((LivingEntityAccessor) to).setSwimAmountO(swimAmtO);
        to.startUsingItem(from.getUsedItemHand());
    }
}
