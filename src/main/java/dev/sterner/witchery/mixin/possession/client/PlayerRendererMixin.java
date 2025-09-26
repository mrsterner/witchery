package dev.sterner.witchery.mixin.possession.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment;
import dev.sterner.witchery.registry.WitcheryRenderTypes;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRendererMixin {

    @Shadow public abstract ResourceLocation getTextureLocation(AbstractClientPlayer entity);

    @Override
    protected RenderType witchery$replaceRenderLayer(@Nullable RenderType base, LivingEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (base != null && entity instanceof AbstractClientPlayer clientPlayer) {

            if (AfflictionPlayerAttachment.getData(clientPlayer).isSoulForm()) {
                return WitcheryRenderTypes.INSTANCE.getGHOST_ADDITIVE().apply(getTextureLocation(clientPlayer));
            }
        }

        return super.witchery$replaceRenderLayer(base, entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
