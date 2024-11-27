package dev.sterner.witchery.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.client.SleepingClientPlayerEntity
import dev.sterner.witchery.client.particle.ZzzData
import dev.sterner.witchery.entity.SleepingPlayerEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import kotlin.math.cos
import kotlin.math.sin


class SleepingPlayerEntityRenderer(context: EntityRendererProvider.Context) :
    EntityRenderer<SleepingPlayerEntity>(context) {

    private var sleepPlayer: SleepingClientPlayerEntity? = null

    override fun getTextureLocation(entity: SleepingPlayerEntity): ResourceLocation? {
        return null
    }



    override fun render(
        entity: SleepingPlayerEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
        poseStack.pushPose()
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.yRot))

        if (entity.isFaceplanted()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90f))
            poseStack.translate(0.0, -1.0, -2.01 / 16.0)
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90f))
            poseStack.translate(0.0, -1.0, 2.01 / 16.0)
        }

        val equipmentList = entity.getEquipment()
        sleepPlayer = SleepingClientPlayerEntity(
            entity.level() as ClientLevel,
            entity.entityData.get(SleepingPlayerEntity.RESOLVEABLE).gameProfile,
            equipmentList,
            entity.getSleepingModel()
        )
        sleepPlayer?.hurtTime = entity.entityData.get(SleepingPlayerEntity.HURT_TIME)
        sleepPlayer?.yHeadRotO = 0f
        sleepPlayer?.yHeadRot = 0f
        sleepPlayer?.let {
            val renderer = Minecraft.getInstance().entityRenderDispatcher.getRenderer(it)
            renderer.render(sleepPlayer, 0f, partialTick, poseStack, bufferSource, packedLight)
        }

        if (entity.level().random.nextDouble() < 0.05) {
            addZ(entity)
        }

        poseStack.popPose()
    }



    private fun addZ(player: SleepingPlayerEntity) {
        val pos = player.position()

        val headHeightOffset = 0.5

        val horizontalOffset = 0.6

        val yawRad = Math.toRadians(player.yRot.toDouble() - 90)

        val xOffset = horizontalOffset * cos(yawRad)
        val zOffset = horizontalOffset * sin(yawRad)

        player.level().addAlwaysVisibleParticle(
            ZzzData(1f),
            true,
            pos.x + xOffset + Mth.nextDouble(player.level().random, -0.1, 0.1),
            pos.y + headHeightOffset,
            pos.z + zOffset + Mth.nextDouble(player.level().random, -0.1, 0.1),
            0.0, 0.0, 0.0
        )
    }
}