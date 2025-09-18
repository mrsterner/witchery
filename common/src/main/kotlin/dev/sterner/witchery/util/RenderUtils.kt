package dev.sterner.witchery.util

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.Witchery.id
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.SoulPoolPlayerAttachment
import dev.sterner.witchery.registry.WitcheryShaders
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

object RenderUtils {

    /**
     * Blit from GuiGraphics but also handles alpha and supports color
     */
    fun blitWithAlpha(
        poseStack: PoseStack,
        atlasLocation: ResourceLocation?,
        x: Int,
        y: Int,
        uOffset: Float,
        vOffset: Float,
        width: Int,
        height: Int,
        textureWidth: Int,
        textureHeight: Int,
        alpha: Float = 1.0f,
        color: Int = 0xFFFFFF
    ) {
        val red = (color shr 16 and 255) / 255.0f
        val green = (color shr 8 and 255) / 255.0f
        val blue = (color and 255) / 255.0f

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        if (atlasLocation != null) {
            RenderSystem.setShaderTexture(0, atlasLocation)
        }
        RenderSystem.setShader { GameRenderer.getPositionTexColorShader() }

        val matrix4f: Matrix4f = poseStack.last().pose()
        val bufferBuilder =
            Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
        val minU = uOffset / textureWidth.toFloat()
        val maxU = (uOffset + width) / textureWidth.toFloat()
        val minV = vOffset / textureHeight.toFloat()
        val maxV = (vOffset + height) / textureHeight.toFloat()

        bufferBuilder.addVertex(matrix4f, x.toFloat(), y.toFloat(), 0f).setColor(red, green, blue, alpha)
            .setUv(minU, minV)
        bufferBuilder.addVertex(matrix4f, x.toFloat(), (y + height).toFloat(), 0f).setColor(red, green, blue, alpha)
            .setUv(minU, maxV)
        bufferBuilder.addVertex(matrix4f, (x + width).toFloat(), (y + height).toFloat(), 0f)
            .setColor(red, green, blue, alpha).setUv(maxU, maxV)
        bufferBuilder.addVertex(matrix4f, (x + width).toFloat(), y.toFloat(), 0f).setColor(red, green, blue, alpha)
            .setUv(maxU, minV)

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())

        RenderSystem.disableBlend()
    }


    fun innerRenderBlood(guiGraphics: GuiGraphics, living: LivingEntity, y: Int, x: Int) {
        val data = BloodPoolLivingEntityAttachment.getData(living)
        val bloodPool = data.bloodPool
        val maxBlood = data.maxBlood
        innerRenderBlood(guiGraphics, maxBlood, bloodPool, y, x)
    }

    /**
     * Renders a living entity's blood drops. Used for vampire hot bar and also for hud element when looking at an entity with blood
     */
    fun innerRenderBlood(guiGraphics: GuiGraphics, maxBlood: Int, bloodPool: Int, y: Int, x: Int) {

        val dropCount = maxBlood / WitcheryConstants.BLOOD_DROP
        val fullIcons = bloodPool / WitcheryConstants.BLOOD_DROP
        val partialFill = bloodPool % WitcheryConstants.BLOOD_DROP
        val iconSize = 10
        for (i in 0 until dropCount) {
            val xPos = x - i * 7 - 8

            blitWithAlpha(
                guiGraphics.pose(),
                id("textures/gui/blood_pool_empty.png"),
                xPos,
                y - 1,
                0f,
                0f,
                iconSize,
                iconSize,
                iconSize,
                iconSize,
                1.0f,
                0xFFFFFF
            )

            if (i < fullIcons) {
                blitWithAlpha(
                    guiGraphics.pose(),
                    id("textures/gui/blood_pool_full.png"),
                    xPos,
                    y - 1,
                    0f,
                    0f,
                    iconSize,
                    iconSize,
                    iconSize,
                    iconSize,
                    1.0f,
                    0xFFFFFF
                )
            } else if (i == fullIcons && partialFill > 0) {
                val filledHeight = (partialFill * iconSize) / 300
                val emptyHeight = iconSize - filledHeight
                blitWithAlpha(
                    guiGraphics.pose(),
                    id("textures/gui/blood_pool_full.png"),
                    xPos,
                    y + emptyHeight - 1,
                    0f,
                    emptyHeight.toFloat(),
                    iconSize,
                    filledHeight,
                    iconSize,
                    iconSize,
                    1.0f,
                    0xFFFFFF
                )
            }
        }
    }

    fun innerRenderSouls(guiGraphics: GuiGraphics, player: Player, y: Int, x: Int) {
        val data = SoulPoolPlayerAttachment.getData(player)
        val maxSouls = data.maxSouls
        val currentSouls = data.soulPool
        val iconSize = 10

        for (i in 0 until maxSouls) {
            val xPos = x - i * 12 - 8

            blitWithAlpha(
                guiGraphics.pose(),
                Witchery.id("textures/gui/soul_empty.png"),
                xPos,
                y - 1,
                0f,
                0f,
                iconSize,
                iconSize,
                iconSize,
                iconSize,
                1.0f,
                0xFFFFFF
            )

            if (i < currentSouls) {
                blitWithAlpha(
                    guiGraphics.pose(),
                    id("textures/gui/soul_pure.png"),
                    xPos,
                    y - 1,
                    0f,
                    0f,
                    iconSize,
                    iconSize,
                    iconSize,
                    iconSize,
                    1.0f,
                    0xFFFFFF
                )
            }
        }
    }

    /**
     * Render the bat HUD icons showing how much time bat form has left
     */
    fun innerRenderBat(guiGraphics: GuiGraphics, maxTicks: Int, ticks: Int, y: Int, x: Int) {
        val q = 60 * 20
        val dropCount = maxTicks / q
        val fullIcons = ticks / q
        val partialFill = ticks % q
        val width = 13
        val height = 7

        for (i in 0 until dropCount) {
            val xPos = x + i * 12 - 8

            // Draw empty icon first (for all icons)
            blitWithAlpha(
                guiGraphics.pose(),
                id("textures/gui/affliction_abilities/bat_form_empty.png"),
                xPos,
                y - 1,
                0f,
                0f,
                width,
                height,
                width,
                height,
                1.0f,
                0xFFFFFF
            )

            // Draw full icon if this icon is within the full range
            if (i < fullIcons) {
                blitWithAlpha(
                    guiGraphics.pose(),
                    id("textures/gui/affliction_abilities/bat_form_full.png"),
                    xPos,
                    y - 1,
                    0f,
                    0f,
                    width,
                    height,
                    width,
                    height,
                    1.0f,
                    0xFFFFFF
                )
            } else if (i == fullIcons && partialFill > 0) {
                // Calculate the filled width (instead of height)
                val filledWidth = (partialFill * width) / q
                val emptyWidth = width - filledWidth

                // Draw the full portion of the last icon (from left to right)
                blitWithAlpha(
                    guiGraphics.pose(),
                    id("textures/gui/affliction_abilities/bat_form_full.png"),
                    xPos,
                    y - 1,
                    0f,
                    0f,
                    filledWidth,
                    height,
                    width,
                    height,
                    1.0f,
                    0xFFFFFF
                )
            }
        }
    }

    fun renderChalk(
        poseStack: PoseStack,
        px: Int = 0,
        py: Int = 0,
        texture: ResourceLocation,
        color: Int
    ) {
        blitWithAlpha(poseStack, texture, 1 + px, 1 + 32 + py, 0f, 0f, 16, 16, 16, 16, 0.45f, 0x000000)
        blitWithAlpha(poseStack, texture, 0 + px, 0 + 32 + py, 0f, 0f, 16, 16, 16, 16, 1f, color)
    }

    fun renderChalk(
        poseStack: PoseStack,
        px: Int = 0,
        py: Int = 0,
        texture: ResourceLocation
    ) {
        blitWithAlpha(poseStack, texture, 1 + px, 1 + 32 + py, 0f, 0f, 16, 16, 16, 16, 0.45f, 0x000000)
        blitWithAlpha(poseStack, texture, 0 + px, 0 + 32 + py, 0f, 0f, 16, 16, 16, 16)
    }

    fun renderEntityOnScreen(
        guiGraphics: GuiGraphics,
        x1: Int,
                             y1: Int,
                             x2: Int,
                             y2: Int,
                             scale: Int,
                             yOffset: Float,
                             mouseX: Float,
                             mouseY: Float,
                             entity: LivingEntity){
        guiGraphics.enableScissor(x1, y1, x2, y2)
        renderEntityInInventoryFollowsMouse(guiGraphics, x1, y1, x2, y2, scale, yOffset, mouseX, mouseY, entity)
        guiGraphics.disableScissor()
    }

    fun renderEntityInInventoryFollowsMouse(
        guiGraphics: GuiGraphics,
        x1: Int,
        y1: Int,
        x2: Int,
        y2: Int,
        scale: Int,
        yOffset: Float,
        mouseX: Float,
        mouseY: Float,
        entity: LivingEntity
    ) {
        val f = (x1 + x2).toFloat() / 2.0f
        val g = (y1 + y2).toFloat() / 2.0f
        //guiGraphics.enableScissor(x1, y1, x2, y2)
        val h = atan(((f - mouseX) / 40.0f).toDouble()).toFloat()
        val i = atan(((g - mouseY) / 40.0f).toDouble()).toFloat()
        val quaternionf = Quaternionf().rotateZ(Math.PI.toFloat())
        val quaternionf2 = Quaternionf().rotateX(i * 20.0f * (Math.PI / 180.0).toFloat())
        quaternionf.mul(quaternionf2)
        val j = entity.yBodyRot
        val k = entity.yRot
        val l = entity.xRot
        val m = entity.yHeadRotO
        val n = entity.yHeadRot
        entity.yBodyRot = 180.0f + h * 20.0f
        entity.yRot = 180.0f + h * 40.0f
        entity.xRot = -i * 20.0f
        entity.yHeadRot = entity.yRot
        entity.yHeadRotO = entity.yRot
        val o = entity.scale
        val vector3f = Vector3f(0.0f, entity.bbHeight / 2.0f + yOffset * o, 0.0f)
        val p = scale.toFloat() / o
        renderEntityInInventory(guiGraphics, f, g, p, vector3f, quaternionf, quaternionf2, entity)
        entity.yBodyRot = j
        entity.yRot = k
        entity.xRot = l
        entity.yHeadRotO = m
        entity.yHeadRot = n
        //guiGraphics.disableScissor()
    }

    fun renderEntityInInventory(
        guiGraphics: GuiGraphics,
        x: Float,
        y: Float,
        scale: Float,
        translate: Vector3f,
        pose: Quaternionf?,
        cameraOrientation: Quaternionf?,
        entity: LivingEntity
    ) {
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()
        poseStack.translate(x.toDouble(), y.toDouble(), 950.0)
        poseStack.scale(scale, scale, -scale)
        poseStack.translate(translate.x, translate.y, translate.z)
        poseStack.mulPose(pose)
        Lighting.setupForEntityInInventory()
        val entityRenderDispatcher = Minecraft.getInstance().entityRenderDispatcher
        if (cameraOrientation != null) {
            entityRenderDispatcher.overrideCameraOrientation(
                cameraOrientation.conjugate(Quaternionf()).rotateY(Math.PI.toFloat())
            )
        }

        entityRenderDispatcher.setRenderShadow(false)
        RenderSystem.runAsFancy {
            entityRenderDispatcher.render(
                entity,
                0.0,
                0.0,
                0.0,
                0.0f,
                1.0f,
                poseStack,
                guiGraphics.bufferSource(),
                15728880
            )
        }
        guiGraphics.flush()
        entityRenderDispatcher.setRenderShadow(true)
        poseStack.popPose()
        Lighting.setupFor3DItems()
    }

    val TEXTURE_FRONT = Witchery.id("textures/entity/soul_lantern_front.png")
    val TEXTURE_BACK = Witchery.id("textures/entity/soul_lantern_front.png")
    val TEXTURE_LEFT = Witchery.id("textures/entity/soul_lantern_right.png")
    val TEXTURE_RIGHT = Witchery.id("textures/entity/soul_lantern_right.png")
    val TEXTURE_TOP = Witchery.id("textures/entity/soul_lantern_top.png")
    val TEXTURE_BOTTOM = Witchery.id("textures/entity/soul_lantern_top.png")
    val TEXTURE_CORE = Witchery.id("textures/entity/soul_lantern_core.png")

    fun renderGlowBoxEffect11(
        fadeProgress: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource
    ) {
        val shader = WitcheryShaders.soulLantern ?: return

        poseStack.pushPose()
        poseStack.translate(0.5, 0.5, 0.5)

        val time = System.currentTimeMillis() / 1000.0
        val wobble = sin(time * 2.0).toFloat() * 0.025f
        val wobble2 = cos(time * 1.5).toFloat() * 0.025f

        val glowMin = -5.5f + wobble
        val glowMax = 5.5f + wobble2

        val matrix = poseStack.last().pose()

        if (bufferSource is MultiBufferSource.BufferSource) {
            bufferSource.endBatch()
        }

        val prevShader = RenderSystem.getShader()
        val prevCull = GL11.glIsEnabled(GL11.GL_CULL_FACE)
        val prevDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK)

        RenderSystem.enableBlend()
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE)
        RenderSystem.disableCull()
        RenderSystem.depthMask(false)
        RenderSystem.enableDepthTest()
        RenderSystem.depthFunc(GL11.GL_LEQUAL)

        shader.safeGetUniform("Alpha")?.set(fadeProgress * 0.7f)
        RenderSystem.setShader { shader }

        RenderSystem.setShaderTexture(0, TEXTURE_FRONT)
        val builderFront = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builderFront, matrix, glowMin, glowMin, glowMax, glowMax, glowMin, glowMax, glowMax, glowMax, glowMax, glowMin, glowMax, glowMax)
        BufferUploader.drawWithShader(builderFront.buildOrThrow())

        RenderSystem.setShaderTexture(0, TEXTURE_BACK)
        val builderBack = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builderBack, matrix, glowMax, glowMin, glowMin, glowMin, glowMin, glowMin, glowMin, glowMax, glowMin, glowMax, glowMax, glowMin)
        BufferUploader.drawWithShader(builderBack.buildOrThrow())

        RenderSystem.setShaderTexture(0, TEXTURE_LEFT)
        val builderLeft = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builderLeft, matrix, glowMin, glowMin, glowMin, glowMin, glowMin, glowMax, glowMin, glowMax, glowMax, glowMin, glowMax, glowMin)
        BufferUploader.drawWithShader(builderLeft.buildOrThrow())

        RenderSystem.setShaderTexture(0, TEXTURE_RIGHT)
        val builderRight = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builderRight, matrix, glowMax, glowMin, glowMax, glowMax, glowMin, glowMin, glowMax, glowMax, glowMin, glowMax, glowMax, glowMax)
        BufferUploader.drawWithShader(builderRight.buildOrThrow())

        RenderSystem.setShaderTexture(0, TEXTURE_TOP)
        val builderTop = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builderTop, matrix, glowMin, glowMax, glowMax, glowMax, glowMax, glowMax, glowMax, glowMax, glowMin, glowMin, glowMax, glowMin)
        BufferUploader.drawWithShader(builderTop.buildOrThrow())

        RenderSystem.setShaderTexture(0, TEXTURE_BOTTOM)
        val builderBottom = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builderBottom, matrix, glowMin, glowMin, glowMin, glowMax, glowMin, glowMin, glowMax, glowMin, glowMax, glowMin, glowMin, glowMax)
        BufferUploader.drawWithShader(builderBottom.buildOrThrow())

        val coreSize = 5.4f
        RenderSystem.setShaderTexture(0, TEXTURE_CORE)

        val builderCoreFront = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builderCoreFront, matrix, -coreSize, -coreSize, coreSize, coreSize, -coreSize, coreSize, coreSize, coreSize, coreSize, -coreSize, coreSize, coreSize)
        BufferUploader.drawWithShader(builderCoreFront.buildOrThrow())

        val builderCoreBack = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builderCoreBack, matrix, coreSize, -coreSize, -coreSize, -coreSize, -coreSize, -coreSize, -coreSize, coreSize, -coreSize, coreSize, coreSize, -coreSize)
        BufferUploader.drawWithShader(builderCoreBack.buildOrThrow())

        val builderCoreLeft = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builderCoreLeft, matrix, -coreSize, -coreSize, -coreSize, -coreSize, -coreSize, coreSize, -coreSize, coreSize, coreSize, -coreSize, coreSize, -coreSize)
        BufferUploader.drawWithShader(builderCoreLeft.buildOrThrow())

        val builderCoreRight = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builderCoreRight, matrix, coreSize, -coreSize, coreSize, coreSize, -coreSize, -coreSize, coreSize, coreSize, -coreSize, coreSize, coreSize, coreSize)
        BufferUploader.drawWithShader(builderCoreRight.buildOrThrow())

        val builderCoreTop = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builderCoreTop, matrix, -coreSize, coreSize, coreSize, coreSize, coreSize, coreSize, coreSize, coreSize, -coreSize, -coreSize, coreSize, -coreSize)
        BufferUploader.drawWithShader(builderCoreTop.buildOrThrow())

        val builderCoreBottom = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        addGlowFace(builderCoreBottom, matrix, -coreSize, -coreSize, -coreSize, coreSize, -coreSize, -coreSize, coreSize, -coreSize, coreSize, -coreSize, -coreSize, coreSize)
        BufferUploader.drawWithShader(builderCoreBottom.buildOrThrow())

        RenderSystem.defaultBlendFunc()
        RenderSystem.disableBlend()
        RenderSystem.depthMask(prevDepthMask)
        if (prevCull) {
            RenderSystem.enableCull()
        }

        if (prevShader != null) {
            RenderSystem.setShader { prevShader }
        }

        poseStack.popPose()
    }

    private fun addGlowFace(
        builder: BufferBuilder,
        matrix: Matrix4f,
        x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float,
        x3: Float, y3: Float, z3: Float,
        x4: Float, y4: Float, z4: Float
    ) {
        val light = 240

        builder.addVertex(matrix, x1, y1, z1).setColor(255, 255, 255, 255).setUv(0f, 0f).setUv2(light, light)
        builder.addVertex(matrix, x2, y2, z2).setColor(255, 255, 255, 255).setUv(1f, 0f).setUv2(light, light)
        builder.addVertex(matrix, x3, y3, z3).setColor(255, 255, 255, 255).setUv(1f, 1f).setUv2(light, light)
        builder.addVertex(matrix, x4, y4, z4).setColor(255, 255, 255, 255).setUv(0f, 1f).setUv2(light, light)
    }
}