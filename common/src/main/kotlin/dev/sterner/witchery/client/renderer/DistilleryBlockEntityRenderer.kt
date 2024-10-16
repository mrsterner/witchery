package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.architectury.fluid.FluidStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.cauldron.CauldronBlockEntity
import dev.sterner.witchery.block.distillery.DistilleryBlockEntity
import dev.sterner.witchery.client.model.JarModel
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


class DistilleryBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<DistilleryBlockEntity> {

    var jarModel = JarModel(ctx.bakeLayer(JarModel.LAYER_LOCATION))

    override fun render(
        blockEntity: DistilleryBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val jarTexture = RenderType.entityCutout(Witchery.id("textures/block/jar_block.png"))
        val buffer = bufferSource.getBuffer(jarTexture)

        val offsets = listOf(
            Pair(6.2f / 16f, 0f),    // +X face
            Pair(-6.2f / 16f, 0f),   // -X face
            Pair(0f, 6.2f / 16f),    // +Z face
            Pair(0f, -6.2f / 16f)    // -Z face
        )

        for (offset in offsets) {
            poseStack.pushPose()

            poseStack.translate(0.5 + offset.first, 0.0, 0.5 + offset.second)
            poseStack.scale(-1.0f, -1.0f, 1.0f)
            poseStack.translate(0.0, -1.85, 0.0)

            jarModel.renderToBuffer(
                poseStack,
                buffer,
                packedLight,
                packedOverlay
            )

            poseStack.popPose()
        }
    }
}
