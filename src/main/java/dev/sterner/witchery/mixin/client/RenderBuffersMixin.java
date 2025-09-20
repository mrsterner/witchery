package dev.sterner.witchery.mixin.client;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import dev.sterner.witchery.registry.WitcheryRenderTypes;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBuffers.class)
public class RenderBuffersMixin {

    @Inject(method = "put", at = @At("HEAD"))
    private static void witchery$putBuffers(Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder> mapBuilders, RenderType renderType, CallbackInfo ci) {
        WitcheryRenderTypes.addGlintType(mapBuilders);
    }
}
