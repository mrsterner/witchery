package dev.sterner.witchery.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.block.poppet.PoppetBlockEntity
import dev.sterner.witchery.client.model.poppet.ArmorPoppetModel
import dev.sterner.witchery.client.model.poppet.HungerPoppetModel
import dev.sterner.witchery.client.model.poppet.VampiricPoppetModel
import dev.sterner.witchery.client.model.poppet.VoodooPoppetModel
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.properties.BlockStateProperties


class PoppetBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<PoppetBlockEntity> {

    val vampModel = VampiricPoppetModel(ctx.bakeLayer(VampiricPoppetModel.LAYER_LOCATION))
    val hungModel = HungerPoppetModel(ctx.bakeLayer(HungerPoppetModel.LAYER_LOCATION))
    val armorModel = ArmorPoppetModel(ctx.bakeLayer(ArmorPoppetModel.LAYER_LOCATION))
    val voodooModel = VoodooPoppetModel(ctx.bakeLayer(VoodooPoppetModel.LAYER_LOCATION))

    override fun render(
        blockEntity: PoppetBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5, 1.6, 0.5)
        val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val dirYRot = dir.toYRot()
        poseStack.mulPose(Axis.YP.rotationDegrees(-dirYRot))
        poseStack.scale(-1f, -1f, 1f)
        if (blockEntity.poppetItemStack.`is`(WitcheryItems.VAMPIRIC_POPPET.get())) {
            this.vampModel.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/poppet_vamp.png"))),
                packedLight,
                packedOverlay
            )
        }
        if (blockEntity.poppetItemStack.`is`(WitcheryItems.ARMOR_PROTECTION_POPPET.get())) {
            this.armorModel.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/poppet_armor.png"))),
                packedLight,
                packedOverlay
            )
        }
        if (blockEntity.poppetItemStack.`is`(WitcheryItems.HUNGER_PROTECTION_POPPET.get())) {
            this.hungModel.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/poppet_hunger.png"))),
                packedLight,
                packedOverlay
            )
        }
        if (blockEntity.poppetItemStack.`is`(WitcheryItems.VOODOO_POPPET.get())) {
            this.voodooModel.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.entityCutout(Witchery.id("textures/block/poppet_voodoo.png"))),
                packedLight,
                packedOverlay
            )
        }

        poseStack.popPose()
    }
}