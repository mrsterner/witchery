package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.BansheeEntityModel
import dev.sterner.witchery.client.model.NightmareEntityModel
import dev.sterner.witchery.client.model.VampireEntityModel
import dev.sterner.witchery.client.model.WerewolfEntityModel
import dev.sterner.witchery.entity.BansheeEntity
import dev.sterner.witchery.entity.NightmareEntity
import dev.sterner.witchery.entity.VampireEntity
import dev.sterner.witchery.entity.WerewolfEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class NightmareEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<NightmareEntity, NightmareEntityModel>(
        context,
        NightmareEntityModel(context.bakeLayer(NightmareEntityModel.LAYER_LOCATION)),
        0.3f
    ) {

    override fun render(
        entity: NightmareEntity,
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

    override fun getTextureLocation(entity: NightmareEntity): ResourceLocation {
        return Witchery.id("textures/entity/nightmare.png")
    }
}