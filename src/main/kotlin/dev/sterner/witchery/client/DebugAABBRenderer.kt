package dev.sterner.witchery.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.Matrix4f

object DebugAABBRenderer {

    private data class DebugAABB(
        val aabb: AABB,
        val color: Int,
        var remainingTicks: Int
    )

    private val debugAABBs = mutableListOf<DebugAABB>()

    fun addAABB(aabb: AABB, color: Int, durationTicks: Int) {
        debugAABBs.add(DebugAABB(aabb, color, durationTicks))
    }

    fun tick() {
        debugAABBs.removeAll { debug ->
            debug.remainingTicks--
            debug.remainingTicks <= 0
        }
    }

    fun render(poseStack: PoseStack, bufferSource: MultiBufferSource, cameraPos: Vec3) {
        if (debugAABBs.isEmpty()) return

        val buffer = bufferSource.getBuffer(RenderType.lines())

        debugAABBs.forEach { debug ->
            val aabb = debug.aabb.move(-cameraPos.x, -cameraPos.y, -cameraPos.z)
            val color = debug.color

            val r = ((color shr 16) and 0xFF) / 255f
            val g = ((color shr 8) and 0xFF) / 255f
            val b = (color and 0xFF) / 255f
            val a = 1.0f

            renderAABBLines(poseStack, buffer, aabb, r, g, b, a)
        }
    }

    private fun renderAABBLines(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        aabb: AABB,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        val matrix = poseStack.last().pose()

        // Bottom face
        drawLine(matrix, buffer, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY, aabb.minZ, r, g, b, a)
        drawLine(matrix, buffer, aabb.maxX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ, r, g, b, a)
        drawLine(matrix, buffer, aabb.maxX, aabb.minY, aabb.maxZ, aabb.minX, aabb.minY, aabb.maxZ, r, g, b, a)
        drawLine(matrix, buffer, aabb.minX, aabb.minY, aabb.maxZ, aabb.minX, aabb.minY, aabb.minZ, r, g, b, a)

        // Top face
        drawLine(matrix, buffer, aabb.minX, aabb.maxY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.minZ, r, g, b, a)
        drawLine(matrix, buffer, aabb.maxX, aabb.maxY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, r, g, b, a)
        drawLine(matrix, buffer, aabb.maxX, aabb.maxY, aabb.maxZ, aabb.minX, aabb.maxY, aabb.maxZ, r, g, b, a)
        drawLine(matrix, buffer, aabb.minX, aabb.maxY, aabb.maxZ, aabb.minX, aabb.maxY, aabb.minZ, r, g, b, a)

        // Vertical edges
        drawLine(matrix, buffer, aabb.minX, aabb.minY, aabb.minZ, aabb.minX, aabb.maxY, aabb.minZ, r, g, b, a)
        drawLine(matrix, buffer, aabb.maxX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.minZ, r, g, b, a)
        drawLine(matrix, buffer, aabb.maxX, aabb.minY, aabb.maxZ, aabb.maxX, aabb.maxY, aabb.maxZ, r, g, b, a)
        drawLine(matrix, buffer, aabb.minX, aabb.minY, aabb.maxZ, aabb.minX, aabb.maxY, aabb.maxZ, r, g, b, a)
    }

    private fun drawLine(
        matrix: Matrix4f,
        buffer: VertexConsumer,
        x1: Double,
        y1: Double,
        z1: Double,
        x2: Double,
        y2: Double,
        z2: Double,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        buffer.addVertex(matrix, x1.toFloat(), y1.toFloat(), z1.toFloat())
            .setColor(r, g, b, a)
            .setNormal(0f, 1f, 0f)

        buffer.addVertex(matrix, x2.toFloat(), y2.toFloat(), z2.toFloat())
            .setColor(r, g, b, a)
            .setNormal(0f, 1f, 0f)
    }
}