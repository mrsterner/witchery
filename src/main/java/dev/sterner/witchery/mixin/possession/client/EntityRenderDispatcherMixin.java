package dev.sterner.witchery.mixin.possession.client;


import com.mojang.blaze3d.vertex.PoseStack;
import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment;
import dev.sterner.witchery.data_attachment.possession.PossessionEvents;
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    public Camera camera;
    @Unique
    @Nullable
    private Entity witchery$camerasPossessed;

    @Inject(method = "prepare", at = @At("HEAD"))
    private void witchery$prepare(Level level, Camera activeRenderInfo, Entity entity, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        Entity camera = client.getCameraEntity();
        if (camera instanceof Player player) {
            witchery$camerasPossessed = PossessionComponentAttachment.INSTANCE.get(player).getHost();
        }
    }

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void witchery$shouldRender(Entity entity, Frustum visibleRegion, double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        if (witchery$camerasPossessed == entity) {
            var event = new PossessionEvents.AllowRender(entity);
            NeoForge.EVENT_BUS.post(event);
            if (camera.isDetached() || !event.isCanceled()) {
                info.setReturnValue(false);
            }
        }
    }

    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void witchery$renderShadow(PoseStack matrices, MultiBufferSource vertices, Entity rendered, float distance, float tickDelta, LevelReader world, float radius, CallbackInfo ci) {
        if (rendered instanceof Player player && AfflictionPlayerAttachment.getData(player).isVagrant()) {
            ci.cancel();
        }
    }
}
