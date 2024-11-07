package dev.sterner.witchery.registry

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import dev.architectury.platform.Platform
import dev.sterner.witchery.Witchery
import net.minecraft.Util
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderStateShard.*
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderType.create
import net.minecraft.resources.ResourceLocation

object WitcheryRenderTypes {

    val BUFFER_SIZE = if (Platform.isModLoaded("sodium")) {
        262144
    } else {
        256
    }

    val SPIRIT_PORTAL = Util.memoize { resourceLocation: ResourceLocation ->
        val compositeState: RenderType.CompositeState? =
            RenderType.CompositeState.builder()
                .setShaderState(ShaderStateShard(WitcheryShaders::spiritPortal))
                .setTextureState(TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(true)
        create(
            Witchery.MODID + "spirit_portal",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.TRIANGLES,
            BUFFER_SIZE,
            true,
            false,
            compositeState!!
        )
    }
}