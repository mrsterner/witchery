package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.platform.Window
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.MirrorModel
import dev.sterner.witchery.content.block.mirror.MirrorBlockEntity
import dev.sterner.witchery.core.registry.WitcheryRenderTypes
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.model.SkullModel
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.Matrix4f
import org.joml.Matrix4fStack
import org.joml.Vector4f

class MirrorBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<MirrorBlockEntity> {

    private val model = MirrorModel(ctx.bakeLayer(MirrorModel.LAYER_LOCATION))
    private val zombieHeadModel = SkullModel.createHumanoidHeadLayer().bakeRoot()
    private val texture = Witchery.id("textures/block/mirror.png")
    private val zombieHeadTexture = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/zombie/zombie.png")
    private val darkStoneTexture = ResourceLocation.fromNamespaceAndPath("minecraft","textures/block/stone.png")

    override fun getRenderBoundingBox(blockEntity: MirrorBlockEntity): AABB {
        val pos = blockEntity.blockPos
        return AABB(
            pos.x - 1.0,
            pos.y - 1.0,
            pos.z - 1.0,
            pos.x + 2.0,
            pos.y + 3.0,
            pos.z + 2.0
        )
    }

    override fun render(
        blockEntity: MirrorBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val dirYRot = dir.toYRot()
        val isDemon = blockEntity.mode == MirrorBlockEntity.Mode.DEMONIC || blockEntity.hasDemon

        if (isDemon) {
            renderDemonMirror(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, dirYRot)
        } else {
            renderNormalMirror(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, dirYRot)
        }
    }

    private fun renderNormalMirror(
        blockEntity: MirrorBlockEntity,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int,
        dirYRot: Float
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5, 0.0, 0.5)
        poseStack.mulPose(Axis.YP.rotationDegrees(-dirYRot))
        poseStack.scale(-1f, -1f, 1f)

        if (blockEntity.isSmallMirror) {
            model.single.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                packedOverlay,
                -1
            )
        } else {
            model.full.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                packedOverlay,
                -1
            )
        }

        poseStack.popPose()
    }

    private fun renderDemonMirror(
        blockEntity: MirrorBlockEntity,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int,
        dirYRot: Float
    ) {
        val minecraft = Minecraft.getInstance()
        val window = minecraft.window

        poseStack.pushPose()
        poseStack.translate(0.5, 0.0, 0.5)
        poseStack.mulPose(Axis.YP.rotationDegrees(-dirYRot))
        poseStack.scale(-1f, -1f, 1f)

        if (blockEntity.isSmallMirror) {
            model.frame2.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                packedOverlay,
                -1
            )
        } else {
            model.frame.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                packedOverlay,
                -1
            )
        }
        poseStack.popPose()

        val scissorBounds = calculateMirrorScissorBounds(
            blockEntity,
            dirYRot,
            blockEntity.isSmallMirror,
            window
        )

        if (scissorBounds != null) {
            if (bufferSource is MultiBufferSource.BufferSource) {
                bufferSource.endBatch()
            }

            RenderSystem.enableScissor(
                scissorBounds.x,
                scissorBounds.y,
                scissorBounds.width,
                scissorBounds.height
            )

            try {
                renderMirrorRoom(
                    blockEntity,
                    poseStack,
                    bufferSource,
                    packedLight,
                    dirYRot
                )

                if (bufferSource is MultiBufferSource.BufferSource) {
                    bufferSource.endBatch()
                }
            } finally {
                RenderSystem.disableScissor()
            }
        }
    }

    private fun calculateMirrorScissorBounds(
        blockEntity: MirrorBlockEntity,
        facingYRot: Float,
        isSmall: Boolean,
        window: Window
    ): ScissorBounds? {
        val minecraft = Minecraft.getInstance()
        val camera = minecraft.gameRenderer.mainCamera
        val projectionMatrix = RenderSystem.getProjectionMatrix()
        val modelViewStack = RenderSystem.getModelViewStack()

        val pos = blockEntity.blockPos

        val (minX, maxX, minY, maxY, mirrorZ) = if (isSmall) {
            Tuple5(
                -13.0 / 16.0,
                1.0 / 16.0,
                -14.0 / 16.0,
                0.0 / 16.0,
                -1.0 / 16.0
            )
        } else {
            Tuple5(
                -7.0 / 16.0,
                7.0 / 16.0,
                -31.0 / 16.0,
                -1.0 / 16.0,
                -8.0 / 16.0
            )
        }

        val worldCenterX = pos.x + 0.5
        val worldCenterY = pos.y
        val worldCenterZ = pos.z + 0.5

        val offsetX = if (isSmall) 6.0 / 16.0 else 0.0
        val offsetY = if (isSmall) 23.0 / 16.0 else 0.0
        val offsetZ = if (isSmall) -7.0 / 16.0 else 0.0

        val corners = mutableListOf<Vec3>()

        val angle = Math.toRadians(-facingYRot.toDouble())
        val cos = kotlin.math.cos(angle)
        val sin = kotlin.math.sin(angle)

        for (x in listOf(minX, maxX)) {
            for (y in listOf(minY, maxY)) {
                val scaledX = -x + offsetX
                val scaledY = -y + offsetY
                val scaledZ = mirrorZ + offsetZ

                val rotX = scaledX * cos - scaledZ * sin
                val rotZ = scaledX * sin + scaledZ * cos

                corners.add(
                    Vec3(
                        worldCenterX + rotX,
                        worldCenterY + scaledY,
                        worldCenterZ + rotZ
                    )
                )
            }
        }

        val screenCorners = corners.mapNotNull { worldPos ->
            projectToScreen(worldPos, camera, projectionMatrix, modelViewStack, window)
        }

        if (screenCorners.isEmpty()) return null

        val allBehindCamera = screenCorners.all { it.z < 0 }
        if (allBehindCamera) return null

        if (screenCorners.isEmpty()) return null

        val minScreenX = (screenCorners.minOf { it.x }.toInt()).coerceAtLeast(0)
        val minScreenY = (screenCorners.minOf { it.y }.toInt()).coerceAtLeast(0)
        val maxScreenX = (screenCorners.maxOf { it.x }.toInt()).coerceAtMost(window.width)
        val maxScreenY = (screenCorners.maxOf { it.y }.toInt()).coerceAtMost(window.height)

        val width = (maxScreenX - minScreenX).coerceAtLeast(1)
        val height = (maxScreenY - minScreenY).coerceAtLeast(1)

        return ScissorBounds(
            minScreenX,
            window.height - maxScreenY,
            width,
            height
        )
    }

    private fun projectToScreen(
        worldPos: Vec3,
        camera: Camera,
        projectionMatrix: Matrix4f,
        modelViewStack: Matrix4fStack,
        window: Window
    ): Vec3? {
        val cameraPos = camera.position
        val relativePos = worldPos.subtract(cameraPos)

        val clipSpace = Vector4f(
            relativePos.x.toFloat(),
            relativePos.y.toFloat(),
            relativePos.z.toFloat(),
            1.0f
        )

        clipSpace.mul(modelViewStack)
        clipSpace.mul(projectionMatrix)

        val w = clipSpace.w()

        clipSpace.div(w)

        val screenX = (clipSpace.x() + 1.0f) * 0.5f * window.width
        val screenY = (1.0f - clipSpace.y()) * 0.5f * window.height

        return Vec3(screenX.toDouble(), screenY.toDouble(), w.toDouble())
    }

    private fun renderMirrorRoom(
        blockEntity: MirrorBlockEntity,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedOverlay: Int,
        facingYRot: Float
    ) {
        poseStack.pushPose()

        poseStack.translate(0.5, 0.0, 0.5)
        poseStack.mulPose(Axis.YP.rotationDegrees(-facingYRot))
        poseStack.scale(-1f, -1f, 1f)

        if (blockEntity.isSmallMirror) {
            poseStack.translate(6.0 / 16.0, 23.0 / 16.0, -7.0 / 16.0)
        }

        val mirrorZ = if (blockEntity.isSmallMirror) -1.0 / 16.0 else -8.0 / 16.0

        val yOffset = -2.0
        val zOffset = 0.5

        poseStack.translate(0.0, yOffset, mirrorZ + zOffset)

        renderRoomWalls(poseStack, bufferSource)

        renderZombieHead(
            poseStack,
            bufferSource,
            packedOverlay
        )

        poseStack.popPose()
    }

    private fun renderRoomWalls(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource
    ) {
        poseStack.pushPose()

        val buffer = bufferSource.getBuffer(RenderType.entitySolid(darkStoneTexture))
        val pose = poseStack.last()
        val matrix = pose.pose()
        val normal = pose

        val hw = 0.5f
        val depth = 1.0f
        val height = 2.0f

        val darkLight = LightTexture.pack(3, 3)

        addQuad(
            buffer, matrix, normal,
            -hw, 0f, -depth,
            hw, 0f, -depth,
            hw, height, -depth,
            -hw, height, -depth,
            0f, 0f, 1f, 1f,
            darkLight, OverlayTexture.NO_OVERLAY
        )

        addQuad(
            buffer, matrix, normal,
            -hw, 0f, 0f,
            -hw, 0f, -depth,
            -hw, height, -depth,
            -hw, height, 0f,
            0f, 1f, 1f, 0f,
            darkLight, OverlayTexture.NO_OVERLAY
        )

        addQuad(
            buffer, matrix, normal,
            hw, 0f, -depth,
            hw, 0f, 0f,
            hw, height, 0f,
            hw, height, -depth,
            0f, 1f, 1f, 0f,
            darkLight, OverlayTexture.NO_OVERLAY
        )

        addQuad(
            buffer, matrix, normal,
            -hw, 0f, 0f,
            hw, 0f, 0f,
            hw, 0f, -depth,
            -hw, 0f, -depth,
            0f, 0f, 1f, 1f,
            darkLight, OverlayTexture.NO_OVERLAY
        )

        addQuad(
            buffer, matrix, normal,
            -hw, height, -depth,
            hw, height, -depth,
            hw, height, 0f,
            -hw, height, 0f,
            0f, 0f, 1f, 1f,
            darkLight, OverlayTexture.NO_OVERLAY
        )

        poseStack.popPose()
    }

    private fun renderZombieHead(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedOverlay: Int
    ) {
        poseStack.pushPose()

        poseStack.translate(0.0, 1.0, -0.5)

        val time = (System.currentTimeMillis() % 36000) / 100.0f
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 2.0f))

        val bob = kotlin.math.sin(time * 0.1) * 0.05
        poseStack.translate(0.0, bob, 0.0)

        val scale = 0.5f
        poseStack.scale(scale, scale, scale)

        val buffer = bufferSource.getBuffer(
            WitcheryRenderTypes.MIRROR_PORTAL.apply(zombieHeadTexture)
        )

        zombieHeadModel.render(
            poseStack,
            buffer,
            LightTexture.FULL_BRIGHT,
            packedOverlay,
            -1
        )

        poseStack.popPose()
    }

    private fun addQuad(
        buffer: VertexConsumer,
        matrix: Matrix4f,
        normal: PoseStack.Pose,
        x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float,
        x3: Float, y3: Float, z3: Float,
        x4: Float, y4: Float, z4: Float,
        u1: Float, v1: Float, u2: Float, v2: Float,
        light: Int,
        overlay: Int
    ) {
        val v1x = x2 - x1
        val v1y = y2 - y1
        val v1z = z2 - z1
        val v2x = x3 - x1
        val v2y = y3 - y1
        val v2z = z3 - z1

        var nx = v1y * v2z - v1z * v2y
        var ny = v1z * v2x - v1x * v2z
        var nz = v1x * v2y - v1y * v2x

        val length = kotlin.math.sqrt(nx * nx + ny * ny + nz * nz)
        if (length > 0.001f) {
            nx /= length
            ny /= length
            nz /= length
        }

        buffer.addVertex(matrix, x1, y1, z1)
            .setColor(255, 255, 255, 255)
            .setUv(u1, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, nx, ny, nz)

        buffer.addVertex(matrix, x2, y2, z2)
            .setColor(255, 255, 255, 255)
            .setUv(u2, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, nx, ny, nz)

        buffer.addVertex(matrix, x3, y3, z3)
            .setColor(255, 255, 255, 255)
            .setUv(u2, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, nx, ny, nz)

        buffer.addVertex(matrix, x4, y4, z4)
            .setColor(255, 255, 255, 255)
            .setUv(u1, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, nx, ny, nz)
    }

    data class ScissorBounds(
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int
    )

    data class Tuple5(
        val v1: Double,
        val v2: Double,
        val v3: Double,
        val v4: Double,
        val v5: Double
    )
}