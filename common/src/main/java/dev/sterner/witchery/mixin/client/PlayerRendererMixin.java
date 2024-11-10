package dev.sterner.witchery.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.sterner.witchery.platform.PlayerManifestationDataAttachment;
import dev.sterner.witchery.platform.infusion.LightInfusionDataAttachment;
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
        if (PlayerManifestationDataAttachment.getData(player).getManifestationTimer() > 0) {
            int originalAlpha = (654311423 >> 24) & 0xFF;
            int reducedAlpha = (int) (originalAlpha * 0.75) << 24;
            int colorWithReducedAlpha = (654311423 & 0x00FFFFFF) | reducedAlpha;
            instance.render(poseStack, multiBufferSource.getBuffer(RenderType.entityTranslucent(resourceLocation)), packedLight, OverlayTexture.NO_OVERLAY, colorWithReducedAlpha);
            return false;
        }
        return true;
    }
}
