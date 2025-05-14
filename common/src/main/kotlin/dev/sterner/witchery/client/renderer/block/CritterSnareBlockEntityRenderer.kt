package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.block.critter_snare.CritterSnareBlock
import dev.sterner.witchery.block.critter_snare.CritterSnareBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ambient.Bat
import net.minecraft.world.entity.monster.Silverfish
import net.minecraft.world.entity.monster.Slime

class CritterSnareBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<CritterSnareBlockEntity> {

    override fun render(
        blockEntity: CritterSnareBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {

        poseStack.pushPose()
        poseStack.translate(0.5, 0.2, 0.5)
        poseStack.scale(0.5f, 0.5f, 0.5f)

        if (blockEntity.blockState.hasProperty(CritterSnareBlock.CAPTURED_STATE) && blockEntity.blockState.getValue(
                CritterSnareBlock.CAPTURED_STATE
            ) != CritterSnareBlock.CapturedEntity.NONE
        ) {
            val state = blockEntity.blockState.getValue(CritterSnareBlock.CAPTURED_STATE)
            val entityType = when (state) {
                CritterSnareBlock.CapturedEntity.BAT -> {
                    EntityType.BAT
                }

                CritterSnareBlock.CapturedEntity.SLIME -> {
                    EntityType.SLIME
                }

                else -> {
                    EntityType.SILVERFISH
                }
            }
            val entity = entityType.create(blockEntity.level!!)

            if (entity != null) {
                if (entity is Slime) {
                    entity.setSize(1, false)
                }
                if (entity is Bat) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(180f))
                    poseStack.translate(0.0, -0.6, 0.0)
                    entity.isResting = true
                    entity.flyAnimationState.stop()
                    entity.restAnimationState.startIfStopped(0)
                }
                if (entity is Silverfish) {
                    entity.setYBodyRot(0f)
                    entity.tickCount = 0
                }
                entity.yBodyRot = 0f
                entity.yBodyRotO = 0f
                entity.yRotO = 0f
                entity.yRot = 0f
                entity.yHeadRot = 0f
                entity.yHeadRotO = 0f
                Minecraft.getInstance().entityRenderDispatcher.getRenderer(entity)
                    .render(entity, 0f, 0f, poseStack, bufferSource, packedLight)
            }
        }

        poseStack.popPose()
    }
}
