package dev.sterner.witchery.mixin.possession.client;


import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    public Camera camera;
    @Nullable
    private Entity requiem_camerasPossessed;

    /**
     * Called once per frame, used to update the entity
     */
    @Inject(method = "configure", at = @At("HEAD"))
    private void updateCamerasPossessedEntity(World w, Camera c, Entity e, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        Entity camera = client.getCameraEntity();
        requiem_camerasPossessed = camera == null ? null : PossessionComponent.getHost(camera);
    }

    /**
     * Prevents the camera's possessed entity from rendering
     */
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void preventPossessedRender(Entity entity, Frustum visibleRegion, double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        if (requiem_camerasPossessed == entity) {
            if (camera.isThirdPerson() || !RenderSelfPossessedEntityCallback.EVENT.invoker().allowRender(entity)) {
                info.setReturnValue(false);
            }
        }
    }

    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void preventShadowRender(MatrixStack matrices, VertexConsumerProvider vertices, Entity rendered, float distance, float tickDelta, WorldView world, float radius, CallbackInfo ci) {
        if (RemnantComponent.isVagrant(rendered)) {
            ci.cancel();
        }
    }
}