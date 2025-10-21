package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.DistilleryGemModel
import dev.sterner.witchery.client.model.LifeBloodPlantModel
import dev.sterner.witchery.content.block.life_blood.LifeBloodBlock
import dev.sterner.witchery.content.block.life_blood.LifeBloodBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CaveVinesPlantBlock

class LifeBloodBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<LifeBloodBlockEntity> {

    val mainTexture = Witchery.id("textures/block/life_blood.png")
    val plantTexture = Witchery.id("textures/block/life_blood_plant.png")

    val main = LifeBloodPlantModel(ctx.bakeLayer(LifeBloodPlantModel.LAYER_LOCATION))

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
        poseStack.scale(-1f, -1f, 1f)
        if (isBottom) {
            main.glow.visible = berry
            main.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(mainTexture)), packedLight, packedOverlay, -1)
        } else {
            main.glow.visible = berry
            main.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(plantTexture)), packedLight, packedOverlay, -1)
        }

        poseStack.popPose()
    }
}