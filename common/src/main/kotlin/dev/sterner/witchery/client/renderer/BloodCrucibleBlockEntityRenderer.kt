package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.blood_crucible.BloodCrucibleBlockEntity
import dev.sterner.witchery.client.model.BloodCrucibleModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.Material
import org.joml.Matrix4f
import kotlin.math.sin

class BloodCrucibleBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<BloodCrucibleBlockEntity> {

    val model = BloodCrucibleModel(ctx.bakeLayer(BloodCrucibleModel.LAYER_LOCATION))

    override fun render(
        blockEntity: BloodCrucibleBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {

        poseStack.pushPose()
        poseStack.scale(-1.0f, -1.0f, 1.0f)
        poseStack.translate(-0.5, -1.5, 0.5)
        model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(Witchery.id("textures/block/vampire_altar.png"))), packedLight, packedOverlay, -1)
        poseStack.popPose()

        if (blockEntity.bloodPercent() > 0) {
            renderBlood(blockEntity, poseStack, bufferSource, blockEntity.bloodPercent(), packedLight, packedOverlay)
            
        }
    }

    private fun renderBlood(
        blockEntity: BloodCrucibleBlockEntity,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        bloodPercent: Double,
        packedLight: Int,
        packedOverlay: Int
    ) {
        if (blockEntity.bloodPercent() > 0) {
            poseStack.pushPose()
            poseStack.translate(0.5,0.62 + (0.2 * bloodPercent),0.5)
            poseStack.scale(0.85f, 1f, 0.85f)
            val level = blockEntity.level
            if (level != null) {
                val gameTime = level.gameTime
                val cycleTime = gameTime % 240
                val normalizedTime = cycleTime / 240.0

                val waveAmplitude = 0.01
                val heightOffset = sin(normalizedTime * Math.PI * 2) * waveAmplitude

                poseStack.translate(0.0, heightOffset, 0.0)
            }

            val sprite: TextureAtlasSprite = BLOOD.sprite()
        
        val red: Int = (BLOOD_COLOR shr 16) and 0xFF
        val green: Int = (BLOOD_COLOR shr 8) and 0xFF
        val blue: Int = BLOOD_COLOR and 0xFF

        val mat: Matrix4f = poseStack.last().pose()
        val vertexConsumer: VertexConsumer = buffer.getBuffer(RenderType.entityTranslucentCull(sprite.atlasLocation()))

        val quadSize = 5f / 16.0f

        vertexConsumer.addVertex(mat, -quadSize, 0f, quadSize).setColor(red, green, blue, 255)
            .setUv(sprite.u0, sprite.v0).setLight(packedLight).setOverlay(packedOverlay).setNormal(0f, 1f, 0f)
        vertexConsumer.addVertex(mat, quadSize, 0f, quadSize).setColor(red, green, blue, 255)
            .setUv(sprite.u1, sprite.v0).setLight(packedLight).setOverlay(packedOverlay).setNormal(0f, 1f, 0f)
        vertexConsumer.addVertex(mat, quadSize, 0f, -quadSize).setColor(red, green, blue, 255)
            .setUv(sprite.u1, sprite.v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0f, 1f, 0f)
        vertexConsumer.addVertex(mat, -quadSize, 0f, -quadSize).setColor(red, green, blue, 255)
            .setUv(sprite.u0, sprite.v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0f, 1f, 0f)

        poseStack.popPose()
    }
}
    companion object {
        private var BLOOD: Material = Material(LOCATION_BLOCKS, Witchery.id("block/blood_fluid"))
        const val BLOOD_COLOR: Int = 0xff0000
    }
}