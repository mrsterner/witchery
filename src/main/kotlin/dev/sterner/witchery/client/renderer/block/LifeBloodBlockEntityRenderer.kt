package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.LifeBloodPlantModel
import dev.sterner.witchery.content.block.life_blood.LifeBloodBlock
import dev.sterner.witchery.content.block.life_blood.LifeBloodBlockEntity
import dev.sterner.witchery.core.registry.WitcheryRenderTypes
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.CaveVinesPlantBlock

class LifeBloodBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<LifeBloodBlockEntity> {

    val mainTexture = Witchery.id("textures/block/life_blood_v2.png")
    val plantTexture = Witchery.id("textures/block/life_blood_plant_v2.png")

    val main = LifeBloodPlantModel(ctx.bakeLayer(LifeBloodPlantModel.LAYER_LOCATION))

    companion object {
        private const val RESCALE_FACTOR = 1.3f
    }

    override fun render(
        blockEntity: LifeBloodBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val berry = blockEntity.blockState.getValue(CaveVinesPlantBlock.BERRIES)
        val isBottom = blockEntity.blockState.block is LifeBloodBlock

        poseStack.pushPose()
        poseStack.translate(0.5, 0.0, 0.5)
        poseStack.scale(RESCALE_FACTOR, 1f, RESCALE_FACTOR)
        poseStack.translate(- 0.65 / RESCALE_FACTOR, 0.0, -0.65 / RESCALE_FACTOR)
        poseStack.scale(-1f, -1f, 1f)
        poseStack.translate(-0.5, -0.5, 0.5)

        if (isBottom) {
            main.cross.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucentCull(mainTexture)),
                packedLight,
                packedOverlay,
                -1
            )
            if (berry) {
                main.glow.render(
                    poseStack,
                    bufferSource.getBuffer(WitcheryRenderTypes.LIFE.apply(mainTexture)),
                    packedLight,
                    packedOverlay,
                    -1
                )
            }
        } else {
            main.cross.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucentCull(plantTexture)),
                packedLight,
                packedOverlay,
                -1
            )
            if (berry) {
                main.glow.render(
                    poseStack,
                    bufferSource.getBuffer(WitcheryRenderTypes.LIFE.apply(plantTexture)),
                    packedLight,
                    packedOverlay,
                    -1
                )
            }
        }

        poseStack.popPose()
    }
}