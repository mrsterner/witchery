package dev.sterner.witchery.registry

import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import dev.architectury.platform.Platform
import dev.sterner.witchery.Witchery
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import net.minecraft.Util
import net.minecraft.client.renderer.RenderStateShard.*
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderType.create
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack


object WitcheryRenderTypes {

    private val targetStack: ThreadLocal<ItemStack> = ThreadLocal()

    fun setStack(stack: ItemStack) {
        targetStack.set(stack)
    }

    fun checkAllBlack(): Boolean {
        val target = targetStack.get()
        return target != null && !target.isEmpty && (target.`is`(WitcheryItems.NECROMANTIC_STONE.get()))
    }

    @JvmStatic
    fun addGlintType(mapBuilders: Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder>) {
        addGlintTypes(mapBuilders, GLINT.apply(Witchery.id("textures/misc/all_black.png")))
        addGlintTypes(mapBuilders, GLINT_DIRECT.apply(Witchery.id("textures/misc/all_black.png")))
    }

    private fun addGlintTypes(
        map: Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder>,
        renderType: RenderType
    ) {
        if (!map.containsKey(renderType)) {
            map[renderType] = ByteBufferBuilder(renderType.bufferSize())
        }
    }

    val BUFFER_SIZE = if (Platform.isModLoaded("sodium")) {
        262144
    } else {
        256
    }

    init {
        RenderType.RENDERTYPE_EYES_SHADER
    }

    val SPIRIT_PORTAL = Util.memoize { resourceLocation: ResourceLocation ->
        val compositeState: RenderType.CompositeState? =
            RenderType.CompositeState.builder()
                .setShaderState(ShaderStateShard(WitcheryShaders::spiritPortal))
                .setTextureState(TextureStateShard(resourceLocation, false, true))
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .setCullState(CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .setWriteMaskState(COLOR_WRITE)
                .createCompositeState(true)
        create(
            Witchery.MODID + "spirit_portal",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            BUFFER_SIZE,
            true,
            false,
            compositeState!!
        )
    }

    val GLINT = Util.memoize { resourceLocation: ResourceLocation ->
        val compositeState: RenderType.CompositeState? =
            RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_GLINT_SHADER)
                .setTextureState(TextureStateShard(resourceLocation, true, false))
                .setTransparencyState(GLINT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setDepthTestState(EQUAL_DEPTH_TEST)
                .setOverlayState(OVERLAY)
                .setWriteMaskState(COLOR_WRITE)
                .setTexturingState(GLINT_TEXTURING)
                .createCompositeState(true)
        create(
            Witchery.MODID + "glint",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            BUFFER_SIZE,
            true,
            false,
            compositeState!!
        )
    }

    val GLINT_DIRECT = Util.memoize { resourceLocation: ResourceLocation ->
        val compositeState: RenderType.CompositeState? =
            RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER)
                .setTextureState(TextureStateShard(resourceLocation, true, false))
                .setTransparencyState(GLINT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setDepthTestState(EQUAL_DEPTH_TEST)
                .setOverlayState(OVERLAY)
                .setWriteMaskState(COLOR_WRITE)
                .setTexturingState(GLINT_TEXTURING)
                .createCompositeState(true)
        create(
            Witchery.MODID + "glint_direct",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            BUFFER_SIZE,
            true,
            false,
            compositeState!!
        )
    }
}