package dev.sterner.witchery.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.core.api.entity.PlayerShellEntity
import dev.sterner.witchery.client.SleepingClientPlayerEntity
import dev.sterner.witchery.client.particle.ZzzData
import dev.sterner.witchery.content.entity.player_shell.SleepingPlayerEntity
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
    EntityRenderer<PlayerShellEntity>(context) {

    var sleepPlayer: SleepingClientPlayerEntity? = null

    override fun getTextureLocation(entity: PlayerShellEntity): ResourceLocation? = null

    override fun render(
        entity: PlayerShellEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.yRot))

        if (entity is SleepingPlayerEntity) {
            if (entity.isFaceplanted()) {
                poseStack.mulPose(Axis.XP.rotationDegrees(90f))
                poseStack.translate(0.0, -1.0, -2.01 / 16.0)
            } else {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90f))
                poseStack.translate(0.0, -1.0, 2.01 / 16.0)
            }
        }

        if (sleepPlayer == null) {
            val resolvable = entity.entityData.get(PlayerShellEntity.RESOLVEABLE)
            val gameProfile = resolvable.gameProfile
            sleepPlayer = SleepingClientPlayerEntity(
                entity.level() as ClientLevel,
                gameProfile,
                entity.getEquipment(),
                entity.getModel()
            )
        }

        sleepPlayer?.hurtTime = entity.entityData.get(PlayerShellEntity.HURT_TIME)
        if (entity is SleepingPlayerEntity) {
            sleepPlayer?.yHeadRotO = 0f
            sleepPlayer?.yHeadRot = 0f
        } else {
            sleepPlayer?.yHeadRotO = entity.yHeadRot
            sleepPlayer?.yHeadRot = entity.yHeadRot
        }

        sleepPlayer?.let {
            Minecraft.getInstance().entityRenderDispatcher.getRenderer(it)
                .render(it, 0f, partialTick, poseStack, bufferSource, packedLight)
        }

        if (entity is SleepingPlayerEntity && entity.level().random.nextDouble() < 0.05) {
            addZ(entity)
        }

        poseStack.popPose()
    }

    private fun addZ(player: PlayerShellEntity) {
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