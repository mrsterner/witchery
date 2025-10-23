package dev.sterner.witchery.core.registry

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import dev.sterner.witchery.Witchery
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import net.minecraft.Util
import net.minecraft.client.renderer.RenderStateShard.*
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderType.create
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack


object WitcheryRenderTypes {

    private val targetStack: ThreadLocal<ItemStack> = ThreadLocal()

    fun setStack(stack: ItemStack) {
        targetStack.set(stack)
    }

    fun checkAllBlack(): Boolean {
        val target = targetStack.get()
        if (target != null && !target.isEmpty) {
            val bl = target.`is`(WitcheryItems.NECROMANTIC_STONE.get())
            val bl2 = target.`is`(WitcheryItems.ETERNAL_CATALYST.get())
            val bl3 = target.`is`(WitcheryItems.HAGS_RING.get())

            return bl || bl2 || bl3
        }

        return false
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

    private fun makeSpectral(texture: ResourceLocation, shaderInstance: ShaderInstance): RenderType.CompositeState {
        return RenderType.CompositeState.builder()
            .setShaderState(ShaderStateShard { shaderInstance })
            .setTextureState(TextureStateShard(texture, false, true))
            .setTransparencyState(ADDITIVE_TRANSPARENCY)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setCullState(CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .createCompositeState(true)
    }

    val SOUL_CHAIN = Util.memoize { texture: ResourceLocation ->
        create(
            Witchery.MODID + "soul_chain",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            262144,
            true,
            false,
            makeSpectral(texture, WitcheryShaders.soul_chain!!)
        )
    }

    val SPIRIT_CHAIN = Util.memoize { texture: ResourceLocation ->
        create(
            Witchery.MODID + "spirit_chain",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            262144,
            true,
            false,
            makeSpectral(texture, WitcheryShaders.spirit_chain!!)
        )
    }

    private fun createGhostRenderType(
        transparency: TransparencyStateShard,
        shader: ShaderStateShard,
        name: String = "ghost"
    ) = Util.memoize { resourceLocation: ResourceLocation ->
        create(
            Witchery.MODID + name,
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            262144,
            true,
            false,
            RenderType.CompositeState.builder()
                .setShaderState(shader)
                .setTextureState(TextureStateShard(resourceLocation, false, true))
                .setTransparencyState(transparency)
                .setCullState(CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .createCompositeState(true)
        )
    }

    val GHOST_ADDITIVE = createGhostRenderType(
        ADDITIVE_TRANSPARENCY,
        ShaderStateShard(WitcheryShaders::additive_ghost),
        "ghost_additive"
    )
    val GHOST = createGhostRenderType(TRANSLUCENT_TRANSPARENCY, ShaderStateShard(WitcheryShaders::ghost))

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
            262144,
            true,
            false,
            compositeState!!
        )
    }

    val LIFE = Util.memoize { resourceLocation: ResourceLocation ->
        val compositeState: RenderType.CompositeState? =
            RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_EYES_SHADER)
                .setTextureState(TextureStateShard(resourceLocation, false, true))
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .setCullState(CULL)
                .setWriteMaskState(COLOR_WRITE)
                .createCompositeState(true)
        create(
            Witchery.MODID + "life",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            262144,
            true,
            false,
            compositeState!!
        )
    }

    val SOFT_ADDITIVE_TRANSPARENCY = TransparencyStateShard(
        "soft_additive_transparency",
        {
            RenderSystem.enableBlend()
            RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE
            )
        },
        {
            RenderSystem.disableBlend()
            RenderSystem.defaultBlendFunc()
        }
    )

    val INNER_SOUL_CAGE = Util.memoize { resourceLocation: ResourceLocation ->
        val compositeState: RenderType.CompositeState? =
            RenderType.CompositeState.builder()
                .setShaderState(ShaderStateShard(WitcheryShaders::soulCage))
                .setTextureState(TextureStateShard(resourceLocation, false, true))
                .setTransparencyState(SOFT_ADDITIVE_TRANSPARENCY)
                .setCullState(CULL)
                .setWriteMaskState(COLOR_WRITE)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(true)
        create(
            Witchery.MODID + "inner_soul_cage",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            262144,
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
            262144,
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
            262144,
            true,
            false,
            compositeState!!
        )
    }

}