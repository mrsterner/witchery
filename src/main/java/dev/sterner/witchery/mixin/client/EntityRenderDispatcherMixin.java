package dev.sterner.witchery.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sterner.witchery.features.infusion.LightInfusionPlayerAttachment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void witchery$noShadow(PoseStack poseStack, MultiBufferSource buffer, Entity entity, float weight, float partialTicks, LevelReader level, float size, CallbackInfo ci){
        if (entity instanceof Player player) {
            if (LightInfusionPlayerAttachment.isInvisible(player).isInvisible()) {
                ci.cancel();
            }
        }
    }
}
