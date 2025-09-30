package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.block.ancient_tablet.AncientTabletBlockEntity
import dev.sterner.witchery.client.model.AltarBlockEntityModel
import dev.sterner.witchery.client.model.AltarClothBlockEntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import org.joml.Vector3f

class AncientTabletBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<AncientTabletBlockEntity> {

    override fun getRenderBoundingBox(blockEntity: AncientTabletBlockEntity): AABB {
        val pos = blockEntity.blockPos
        return AABB(
            pos.x - 1.5,
            pos.y - 0.0,
            pos.z - 1.5,
            pos.x + 2.5,
            pos.y + 2.5,
            pos.z + 2.5
        )
    }

    override fun render(
        blockEntity: AncientTabletBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val textureLocation = Witchery.id("textures/entity/stone_tablet_${blockEntity.textureId}.png")

        poseStack.pushPose()

        val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

        val (transX, transZ) = when (dir) {
            Direction.NORTH -> Pair(-1.0, 0.0)
            Direction.EAST -> Pair(1.0, -1.0)
            Direction.SOUTH -> Pair(2.0, 1.0)
            Direction.WEST -> Pair(0.0, 2.0)
            else -> Pair(0.0, 0.0)
        }

        poseStack.translate(transX, 0.0, transZ)

        val rotation = when (dir) {
            Direction.NORTH -> 0f
            Direction.EAST -> 270f
            Direction.SOUTH -> 180f
            Direction.WEST -> 90f
            else -> 0f
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation))

        poseStack.translate(0.0, 0.0, 0.01)

        val vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(textureLocation))

        renderQuad(poseStack, vertexConsumer, packedLight, packedOverlay)

        poseStack.popPose()
    }

    private fun renderQuad(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val pose = poseStack.last()
        val matrix = pose.pose()
        val normalMatrix = pose.normal()

        val width = 2.0f
        val height = 2.0f

        val normalX = 0f
        val normalY = 0f
        val normalZ = 1f
        val normal = normalMatrix.transform(Vector3f(normalX, normalY, normalZ))

        // Bottom-left vertex
        vertexConsumer
            .addVertex(matrix, 0f, 0f, 0f)
            .setColor(255, 255, 255, 255)
            .setUv(0f, 1f)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(normal.x(), normal.y(), normal.z())

        // Bottom-right vertex
        vertexConsumer
            .addVertex(matrix, width, 0f, 0f)
            .setColor(255, 255, 255, 255)
            .setUv(1f, 1f)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(normal.x(), normal.y(), normal.z())

        // Top-right vertex
        vertexConsumer
            .addVertex(matrix, width, height, 0f)
            .setColor(255, 255, 255, 255)
            .setUv(1f, 0f)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(normal.x(), normal.y(), normal.z())

        // Top-left vertex
        vertexConsumer
            .addVertex(matrix, 0f, height, 0f)
            .setColor(255, 255, 255, 255)
            .setUv(0f, 0f)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(normal.x(), normal.y(), normal.z())
    }

}