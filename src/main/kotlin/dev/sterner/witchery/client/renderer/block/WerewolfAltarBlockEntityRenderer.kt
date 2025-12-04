package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.WerewolfAltarModel
import dev.sterner.witchery.content.block.werewolf_altar.WerewolfAltarBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB

open class WerewolfAltarBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<WerewolfAltarBlockEntity> {

    private val model = WerewolfAltarModel(ctx.bakeLayer(WerewolfAltarModel.LAYER_LOCATION))

    override fun getRenderBoundingBox(blockEntity: WerewolfAltarBlockEntity): AABB {
        val pos = blockEntity.blockPos
        return AABB(
            pos.x - 2.0,
            pos.y - 2.0,
            pos.z - 2.0,
            pos.x + 2.0,
            pos.y + 2.0,
            pos.z + 2.0
        )
    }

    override fun render(
        blockEntity: WerewolfAltarBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()

        poseStack.translate(0.5, 1.5, 0.5)
        val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val dirYRot = dir.toYRot()
        poseStack.mulPose(Axis.YP.rotationDegrees(-dirYRot))
        poseStack.scale(-1f, -1f, 1f)

        model.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/block/werewolf_stone.png"))),
            packedLight,
            packedOverlay
        )

        if (!blockEntity.items.isEmpty()) {
            val item = blockEntity.items.first()
            poseStack.translate(-0.35, 0.4, -0.9)
            Minecraft.getInstance().itemRenderer.renderStatic(
                item,
                ItemDisplayContext.GROUND,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                blockEntity.level,
                432423
            )
        }

        poseStack.popPose()
    }
}