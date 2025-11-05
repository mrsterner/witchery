package dev.sterner.witchery.mixin.client;

import net.minecraft.client.renderer.texture.HttpTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(HttpTexture.class)
public interface HttpTextureAccessor {

    @Accessor("file")
    File getFile();

}
