package dev.sterner.witchery.neoforge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.sterner.witchery.Witchery;
import dev.sterner.witchery.registry.WitcheryRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

     @Inject(method = "render", at = @At("HEAD"))
     private void witchery$saveItem(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci){
         WitcheryRenderTypes.INSTANCE.setStack(itemStack);
     }

     @WrapOperation(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;glint()Lnet/minecraft/client/renderer/RenderType;"))
     private static RenderType witchery$getFoilBufferRenderType(Operation<RenderType> original) {
         if (WitcheryRenderTypes.INSTANCE.checkAllBlack()) {
             return WitcheryRenderTypes.INSTANCE.getGLINT().apply(Witchery.INSTANCE.id("textures/misc/all_black.png"));
         }
         return original.call();
    }

    @WrapOperation(method = "getFoilBufferDirect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;glint()Lnet/minecraft/client/renderer/RenderType;"))
    private static RenderType witchery$getDirectFoilBufferRenderType(Operation<RenderType> original) {
         if (WitcheryRenderTypes.INSTANCE.checkAllBlack()) {
             return WitcheryRenderTypes.INSTANCE.getGLINT_DIRECT().apply(Witchery.INSTANCE.id("textures/misc/all_black.png"));
         }
         return original.call();
    }
}