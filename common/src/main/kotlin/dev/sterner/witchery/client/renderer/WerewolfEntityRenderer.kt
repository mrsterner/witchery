package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.BansheeEntityModel
import dev.sterner.witchery.client.model.VampireEntityModel
import dev.sterner.witchery.client.model.WerewolfEntityModel
import dev.sterner.witchery.entity.BansheeEntity
import dev.sterner.witchery.entity.VampireEntity
import dev.sterner.witchery.entity.WerewolfEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class WerewolfEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<WerewolfEntity, WerewolfEntityModel>(
        context,
        WerewolfEntityModel(context.bakeLayer(WerewolfEntityModel.LAYER_LOCATION)),
        0.6f
    ) {

    override fun render(
        entity: WerewolfEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
        poseStack.popPose()
    }

    override fun getTextureLocation(entity: WerewolfEntity): ResourceLocation {
        return Witchery.id("textures/entity/werewolf_grey.png")
    }
}