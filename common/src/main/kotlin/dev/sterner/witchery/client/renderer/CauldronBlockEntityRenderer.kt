package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.architectury.fluid.FluidStack
import dev.sterner.witchery.block.cauldron.CauldronBlockEntity
import dev.sterner.witchery.client.particle.ColorBubbleData
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.resources.model.Material
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Matrix4f


class CauldronBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<CauldronBlockEntity> {

    override fun render(
        blockEntity: CauldronBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        if (blockEntity.fluidTank.fluidStorage.getAmount() > 0) {
            val le: Double =
                blockEntity.fluidTank.fluidStorage.getAmount().toDouble() / FluidStack.bucketAmount().toDouble() * 3
            renderWater(poseStack, bufferSource, le.toInt(), blockEntity.color, packedLight, packedOverlay)

            if (blockEntity.blockState.getValue(BlockStateProperties.LIT)) {
                if (blockEntity.level != null && blockEntity.level!!.random.nextDouble() < 0.25) {
                    addBubble(blockEntity, blockEntity.color, le)
                }
            }
        }
    }

    private fun addBubble(cauldron: CauldronBlockEntity, color: Int, fluidLevel: Double) {
        val pos = cauldron.blockPos
        cauldron.level!!.addAlwaysVisibleParticle(
            ColorBubbleData(
                ((color shr 16) and 0xff) / 255.0f,
                ((color shr 8) and 0xff) / 255.0f,
                (color and 0xff) / 255.0f
            ),
            true,
            pos.x + 0.5 + Mth.nextDouble(cauldron.level!!.random, -0.25, 0.25),
            (pos.y + (fluidLevel * 0.25)),
            pos.z + 0.5 + Mth.nextDouble(cauldron.level!!.random, -0.25, 0.25),
            0.0, 0.0, 0.0
        )
    }

    private fun renderWater(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        fluidLevel: Int,
        color: Int,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.0, (fluidLevel - 3.0) / 4, 0.0)
        poseStack.translate(0.0, 1.0, 0.0)
        val material = WATER_STILL.sprite()
        val maxV: Float = (material.v1 - material.v0) * 0.125f
        val minV: Float = (material.v1 - material.v0) * 0.875f
        val red = (color shr 16) and 0xFF
        val green = (color shr 8) and 0xFF
        val blue = color and 0xFF
        val vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(material.atlasLocation()))
        val mat = poseStack.last().pose()

        addVertex(
            vertexConsumer,
            mat,
            0.125f,
            0f,
            0.875f,
            material.u0,
            material.v0 + maxV,
            red,
            green,
            blue,
            packedLight,
            packedOverlay
        )
        addVertex(
            vertexConsumer,
            mat,
            0.875f,
            0f,
            0.875f,
            material.u1,
            material.v0 + maxV,
            red,
            green,
            blue,
            packedLight,
            packedOverlay
        )
        addVertex(
            vertexConsumer,
            mat,
            0.875f,
            0f,
            0.125f,
            material.u1,
            material.v0 + minV,
            red,
            green,
            blue,
            packedLight,
            packedOverlay
        )
        addVertex(
            vertexConsumer,
            mat,
            0.125f,
            0f,
            0.125f,
            material.u0,
            material.v0 + minV,
            red,
            green,
            blue,
            packedLight,
            packedOverlay
        )
        poseStack.popPose()
    }

    private fun addVertex(
        vertexConsumer: VertexConsumer,
        mat: Matrix4f,
        x: Float,
        y: Float,
        z: Float,
        u: Float,
        v: Float,
        red: Int,
        green: Int,
        blue: Int,
        packedLight: Int,
        packedOverlay: Int
    ) {
        vertexConsumer.addVertex(mat, x, y, z)
            .setColor(red, green, blue, 255)
            .setUv(u, v)
            .setLight(packedLight)
            .setOverlay(packedOverlay)
            .setNormal(1f, 1f, 1f)
    }

    companion object {
        val WATER_STILL: Material =
            Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_still"))
    }
}