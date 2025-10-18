package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.block.ancient_tablet.AncientTabletBlockEntity
import dev.sterner.witchery.client.model.AltarBlockEntityModel
import dev.sterner.witchery.client.model.AltarClothBlockEntityModel
import dev.sterner.witchery.client.model.AncientTabletModel
import dev.sterner.witchery.client.model.ChainModel
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

    private val model = AncientTabletModel(ctx.bakeLayer(AncientTabletModel.LAYER_LOCATION))

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
        val textureLocation = Witchery.id("textures/block/ancient_tablet.png")

        poseStack.pushPose()

        val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

        when (dir) {
            Direction.NORTH -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(0f))
                poseStack.translate(-0.5, 1.5, 0.5)
            }
            Direction.SOUTH -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(180f))
                poseStack.translate(-1.5, 1.5, -0.5)
            }
            Direction.WEST -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(90f))
                poseStack.translate(-1.5, 1.5, 0.5)
            }
            Direction.EAST -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(270f))
                poseStack.translate(-0.5, 1.5, -0.5)
            }
            else -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(0f))
                poseStack.translate(-0.5, 1.5, 0.5)
            }
        }

        poseStack.scale(-1f, -1f, 1f)
        val vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(textureLocation))

        model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, -1)

        poseStack.popPose()
    }
}