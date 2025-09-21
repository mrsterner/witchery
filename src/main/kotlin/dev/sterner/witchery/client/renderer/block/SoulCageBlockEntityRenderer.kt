package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.soul_cage.SoulCageBlockEntity
import dev.sterner.witchery.client.model.GlassContainerModel
import dev.sterner.witchery.util.RenderUtils
import net.minecraft.client.model.VillagerModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3

open class SoulCageBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<SoulCageBlockEntity> {

    val model = GlassContainerModel(ctx.bakeLayer(GlassContainerModel.LAYER_LOCATION))
    val villagerHead = VillagerModel<Villager>(ctx.bakeLayer(ModelLayers.VILLAGER))

    private val outerTexture = Witchery.id("textures/block/glass_container.png")
    private val innerTexture = Witchery.id("textures/block/glass_container_inside.png")
    val villagerSkin = ResourceLocation.withDefaultNamespace("textures/entity/villager/villager.png")

    override fun shouldRenderOffScreen(blockEntity: SoulCageBlockEntity): Boolean {
        return true
    }

    override fun shouldRender(blockEntity: SoulCageBlockEntity, cameraPos: Vec3): Boolean {
        return Vec3.atCenterOf(blockEntity.blockPos).multiply(1.0, 0.0, 1.0)
            .closerThan(cameraPos.multiply(1.0, 0.0, 1.0), this.viewDistance.toDouble())
    }

    override fun render(
        blockEntity: SoulCageBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val lit = blockEntity.blockState.getValue(BlockStateProperties.LIT)
        poseStack.pushPose()
        poseStack.translate(0.5, -0.25, 0.5)
        poseStack.scale(0.85f, 0.85f, 0.85f)

        val renderTypeOuter =
            if (lit) RenderType.entityTranslucentEmissive(outerTexture) else RenderType.entityTranslucent(outerTexture)
        val renderTypeInner = if (lit) RenderType.eyes(innerTexture) else RenderType.entityTranslucent(innerTexture)

        if (blockEntity.hasSoul) {
            val headRotation = calculateHeadRotation(partialTick, blockEntity)

            renderVillagerHead(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(villagerSkin)),
                packedLight,
                packedOverlay,
                headRotation
            )

        }

        model.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(renderTypeOuter),
            packedLight,
            packedOverlay
        )

        poseStack.scale(0.9f, 0.9f, 0.9f)
        poseStack.translate(0.0, 0.1, 0.0)

        model.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(renderTypeInner),
            packedLight,
            packedOverlay
        )

        poseStack.popPose()

        if (blockEntity.isProcessing()) {
            val fadeProgress = (blockEntity.getAnimationTime() / SoulCageBlockEntity.TOTAL_DURATION)
                .coerceIn(0f, 1f)

            RenderUtils.renderGlowBoxEffect11(fadeProgress, poseStack, bufferSource)
        }
    }

    /**
     * Get the smoothly interpolated head rotation from the block entity
     */
    private fun calculateHeadRotation(partialTick: Float, blockEntity: SoulCageBlockEntity): Pair<Float, Float> {
        return blockEntity.getInterpolatedRotation(partialTick)
    }

    private fun renderVillagerHead(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        rotation: Pair<Float, Float>
    ) {
        poseStack.pushPose()
        poseStack.translate(0.0, 1.0, 0.0)
        poseStack.scale(-1.0f, -1.0f, 1.0f)
        poseStack.scale(0.5f, 0.5f, 0.5f)

        // Set head rotation
        val headPart = villagerHead.root().getChild("head")
        headPart.xRot = Math.toRadians(rotation.second.toDouble()).toFloat()
        headPart.yRot = Math.toRadians(rotation.first.toDouble()).toFloat()

        // Make only the head visible
        villagerHead.root().getChild("body").visible = false
        villagerHead.root().getChild("right_leg").visible = false
        villagerHead.root().getChild("left_leg").visible = false
        villagerHead.root().getChild("arms").visible = false
        villagerHead.root().getChild("head").visible = true

        villagerHead.renderToBuffer(
            poseStack,
            buffer,
            packedLight,
            packedOverlay
        )
        poseStack.popPose()
    }
}