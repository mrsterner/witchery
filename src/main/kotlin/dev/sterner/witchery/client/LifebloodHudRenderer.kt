package dev.sterner.witchery.client

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryShaders
import dev.sterner.witchery.features.lifeblood.LifebloodPlayerAttachment
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import org.lwjgl.opengl.GL11
import kotlin.math.ceil
import kotlin.math.pow

object LifebloodHudRenderer {

    /** Heartbeat speed - higher = faster */
    private const val HEARTBEAT_SPEED = 0.45f

    /** Scale increase for first beat */
    private const val FIRST_BEAT_SCALE = 0.09f

    /** Scale increase for second beat */
    private const val SECOND_BEAT_SCALE = 0.06f

    /** Minimum glow intensity (at rest) */
    private const val MIN_GLOW_INTENSITY = 0.5f

    /** Maximum glow intensity (at peak) */
    private const val MAX_GLOW_INTENSITY = 1.0f

    /** Shader wobble intensity */
    private const val WOBBLE_INTENSITY = 0.008f

    /** Shader animation speed */
    private const val WOBBLE_SPEED = 1.2f

    /** Shader wave frequency */
    private const val WOBBLE_FREQUENCY = 10.0f

    fun renderHud(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return

        if (minecraft.options.hideGui || player.isSpectator || player.isCreative) return

        val data = LifebloodPlayerAttachment.getData(player)
        if (data.lifebloodPoints <= 0) return

        val screenWidth = minecraft.window.guiScaledWidth
        val screenHeight = minecraft.window.guiScaledHeight

        val healthBarY = screenHeight - 39
        var lifebloodY = healthBarY - 11

        if (player.armorValue > 0) {
            lifebloodY -= 10
        }

        if (player.absorptionAmount > 0) {
            lifebloodY -= 10
        }

        renderLifebloodHearts(guiGraphics, player, data, screenWidth, lifebloodY)
    }

    private fun renderLifebloodHearts(
        guiGraphics: GuiGraphics,
        player: Player,
        data: LifebloodPlayerAttachment.Data,
        screenWidth: Int,
        y: Int
    ) {
        val poseStack = guiGraphics.pose()
        val totalHearts = ceil(data.lifebloodPoints / 5.0).toInt()
        val startX = screenWidth / 2 - 92
        val size = 10

        for (i in 0 until totalHearts) {
            val row = i / 10
            val x = startX + (i % 10) * 8
            val yPos = y - row * 4

            val pointsInThisHeart = if (i == totalHearts - 1) {
                val remainder = data.getPartialHeartPoints()
                if (remainder == 0) 5 else remainder
            } else {
                5
            }

            val emissiveTexture = getLifebloodEmissiveTexture(pointsInThisHeart)

            val heartbeat = calculateHeartbeat(player.level().gameTime.toFloat() / 20.0f)
            val heartbeatScale = heartbeat.scale
            val glowAlpha = heartbeat.glowIntensity

            poseStack.pushPose()

            poseStack.translate(
                (x + size / 2.0),
                (yPos + size / 2.0),
                0.0
            )
            poseStack.scale(heartbeatScale, heartbeatScale, 1f)
            poseStack.translate(
                -(size / 2.0),
                -(size / 2.0),
                0.0
            )

            renderWobblyHeart(
                player,
                poseStack,
                Witchery.id("textures/gui/lifeblood_empty.png"),
                0f,
                0f,
                size.toFloat(),
                i,
                1.0f,
                false
            )

            renderWobblyHeart(
                player,
                poseStack,
                emissiveTexture,
                0f,
                0f,
                size.toFloat(),
                i,
                glowAlpha,
                true
            )

            poseStack.popPose()
        }
    }

    private data class HeartbeatData(val scale: Float, val glowIntensity: Float)

    /**
     * Creates a heartbeat effect with two quick beats (lub-dub)
     * Returns both scale and glow intensity that peak together
     */
    private fun calculateHeartbeat(timeInSeconds: Float): HeartbeatData {
        val cycleTime = timeInSeconds * HEARTBEAT_SPEED
        val phase = cycleTime % 1.0f

        val glowRange = MAX_GLOW_INTENSITY - MIN_GLOW_INTENSITY

        return when {
            phase < 0.08f -> {
                val t = phase / 0.08f
                val scale = 1.0f + easeOutCubic(t) * FIRST_BEAT_SCALE
                val glow = MIN_GLOW_INTENSITY + easeOutCubic(t) * glowRange
                HeartbeatData(scale, glow)
            }
            phase < 0.2f -> {
                val t = (phase - 0.08f) / 0.12f
                val scale = (1.0f + FIRST_BEAT_SCALE) - easeInQuad(t) * FIRST_BEAT_SCALE
                val glow = MAX_GLOW_INTENSITY - easeInQuad(t) * glowRange
                HeartbeatData(scale, glow)
            }
            phase < 0.25f -> {
                HeartbeatData(1.0f, MIN_GLOW_INTENSITY)
            }
            phase < 0.32f -> {
                val t = (phase - 0.25f) / 0.07f
                val scale = 1.0f + easeOutCubic(t) * SECOND_BEAT_SCALE
                val glow = MIN_GLOW_INTENSITY + easeOutCubic(t) * glowRange
                HeartbeatData(scale, glow)
            }
            phase < 0.42f -> {
                val t = (phase - 0.32f) / 0.1f
                val scale = (1.0f + SECOND_BEAT_SCALE) - easeInQuad(t) * SECOND_BEAT_SCALE
                val glow = MAX_GLOW_INTENSITY - easeInQuad(t) * glowRange
                HeartbeatData(scale, glow)
            }
            else -> {
                HeartbeatData(1.0f, MIN_GLOW_INTENSITY)
            }
        }
    }

    private fun easeOutCubic(t: Float): Float {
        return 1f - (1f - t).pow(3)
    }

    private fun easeInQuad(t: Float): Float {
        return t * t
    }

    private fun renderWobblyHeart(
        player: Player,
        poseStack: PoseStack,
        texture: ResourceLocation,
        x: Float,
        y: Float,
        size: Float,
        index: Int,
        alpha: Float = 1.0f,
        additive: Boolean = false
    ) {
        val shader = WitcheryShaders.lifeblood ?: return

        RenderSystem.setShaderTexture(0, texture)
        RenderSystem.setShader { shader }
        RenderSystem.enableBlend()

        if (additive) {
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        } else {
            RenderSystem.defaultBlendFunc()
        }

        val gameTime = (player.level().gameTime.toFloat() / 20.0f) + index * 0.5f

        shader.safeGetUniform("GameTime").set(gameTime)
        shader.safeGetUniform("Intensity").set(WOBBLE_INTENSITY)
        shader.safeGetUniform("Speed").set(WOBBLE_SPEED)
        shader.safeGetUniform("Frequency").set(WOBBLE_FREQUENCY)

        RenderSystem.setShaderColor(1f, 1f, 1f, alpha)

        val matrix = poseStack.last().pose()
        val tesselator = Tesselator.getInstance()
        val bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)

        bufferBuilder.addVertex(matrix, x, y + size, 0f).setUv(0f, 1f)
        bufferBuilder.addVertex(matrix, x + size, y + size, 0f).setUv(1f, 1f)
        bufferBuilder.addVertex(matrix, x + size, y, 0f).setUv(1f, 0f)
        bufferBuilder.addVertex(matrix, x, y, 0f).setUv(0f, 0f)

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableBlend()
    }

    private fun getLifebloodEmissiveTexture(points: Int): ResourceLocation {
        return when (points) {
            0 -> Witchery.id("textures/gui/lifeblood_empty.png")
            1 -> Witchery.id("textures/gui/lifeblood_1_emissive.png")
            2 -> Witchery.id("textures/gui/lifeblood_2_emissive.png")
            3 -> Witchery.id("textures/gui/lifeblood_3_emissive.png")
            4 -> Witchery.id("textures/gui/lifeblood_4_emissive.png")
            else -> Witchery.id("textures/gui/lifeblood_full_emissive.png")
        }
    }
}