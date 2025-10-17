package dev.sterner.witchery.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import org.joml.Matrix4f

object OreHighlightRenderer {

    data class HighlightedOre(val pos: BlockPos, val endTime: Long)

    private val highlightedOres = mutableListOf<HighlightedOre>()

    fun addHighlightedOres(positions: List<BlockPos>, duration: Int) {
        val endTime = System.currentTimeMillis() + (duration * 50)
        highlightedOres.addAll(positions.map { HighlightedOre(it, endTime) })
    }

    fun tick() {
        val currentTime = System.currentTimeMillis()
        highlightedOres.removeIf { it.endTime < currentTime }
    }

    @JvmStatic
    fun renderOreHighlights(poseStack: PoseStack, camera: Camera, partialTick: Float) {
        if (highlightedOres.isEmpty()) return

        val minecraft = Minecraft.getInstance()

        poseStack.pushPose()

        val camPos = camera.position
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z)

        val bufferSource = minecraft.renderBuffers().bufferSource()
        val buffer = bufferSource.getBuffer(RenderType.lines())

        for (highlight in highlightedOres) {
            val alpha = ((highlight.endTime - System.currentTimeMillis()) / (10000f)).coerceIn(0f, 1f)

            renderBox(
                poseStack, buffer,
                highlight.pos.x.toDouble(),
                highlight.pos.y.toDouble(),
                highlight.pos.z.toDouble(),
                1.0, 1.0, 1.0,
                1f, 1f, 0f, alpha
            )
        }

        bufferSource.endBatch()
        poseStack.popPose()
    }

    private fun renderBox(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        x: Double, y: Double, z: Double,
        width: Double, height: Double, depth: Double,
        red: Float, green: Float, blue: Float, alpha: Float
    ) {
        val matrix = poseStack.last().pose()
        val normal = poseStack.last()

        val x1 = x.toFloat()
        val y1 = y.toFloat()
        val z1 = z.toFloat()
        val x2 = (x + width).toFloat()
        val y2 = (y + height).toFloat()
        val z2 = (z + depth).toFloat()

        line(buffer, matrix, normal, x1, y1, z1, x2, y1, z1, red, green, blue, alpha)
        line(buffer, matrix, normal, x2, y1, z1, x2, y1, z2, red, green, blue, alpha)
        line(buffer, matrix, normal, x2, y1, z2, x1, y1, z2, red, green, blue, alpha)
        line(buffer, matrix, normal, x1, y1, z2, x1, y1, z1, red, green, blue, alpha)

        line(buffer, matrix, normal, x1, y2, z1, x2, y2, z1, red, green, blue, alpha)
        line(buffer, matrix, normal, x2, y2, z1, x2, y2, z2, red, green, blue, alpha)
        line(buffer, matrix, normal, x2, y2, z2, x1, y2, z2, red, green, blue, alpha)
        line(buffer, matrix, normal, x1, y2, z2, x1, y2, z1, red, green, blue, alpha)

        line(buffer, matrix, normal, x1, y1, z1, x1, y2, z1, red, green, blue, alpha)
        line(buffer, matrix, normal, x2, y1, z1, x2, y2, z1, red, green, blue, alpha)
        line(buffer, matrix, normal, x2, y1, z2, x2, y2, z2, red, green, blue, alpha)
        line(buffer, matrix, normal, x1, y1, z2, x1, y2, z2, red, green, blue, alpha)
    }

    private fun line(
        buffer: VertexConsumer,
        matrix: Matrix4f,
        normal: PoseStack.Pose,
        x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float,
        red: Float, green: Float, blue: Float, alpha: Float
    ) {
        val dx = x2 - x1
        val dy = y2 - y1
        val dz = z2 - z1
        val length = Mth.sqrt(dx * dx + dy * dy + dz * dz)
        val nx = dx / length
        val ny = dy / length
        val nz = dz / length

        buffer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha).setNormal(normal, nx, ny, nz)
        buffer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha).setNormal(normal, nx, ny, nz)
    }
}