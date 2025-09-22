package dev.sterner.witchery.client.renderer.block

import com.mojang.authlib.GameProfile
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.phylactery.PhylacteryBlockEntity
import dev.sterner.witchery.client.model.PhylacteryEtherCoreModel
import dev.sterner.witchery.client.model.PhylacteryEtherModel
import dev.sterner.witchery.data_attachment.transformation.PhylacteryLevelDataAttachment
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryRenderTypes
import net.minecraft.client.Minecraft
import net.minecraft.client.model.SkullModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import java.awt.Color
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap




class PhylacteryBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<PhylacteryBlockEntity> {

    private val skullModel = SkullModel(ctx.bakeLayer(ModelLayers.PLAYER_HEAD))
    private val minecraft = Minecraft.getInstance()

    companion object {
        private val skinCache = ConcurrentHashMap<UUID, ResourceLocation>()
        private val loadingProfiles = ConcurrentHashMap<UUID, Boolean>()

        fun clearCache() {
            skinCache.clear()
            loadingProfiles.clear()
        }
    }

    override fun render(
        blockEntity: PhylacteryBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val ownerUUID = blockEntity.ownerUUID ?: return
        val ownerName = blockEntity.ownerName ?: "Unknown"

        val skinTexture = getOrLoadSkinTexture(ownerUUID, ownerName)

        if (!blockEntity.hasSoul) return

        renderPlayerHead(
            poseStack,
            bufferSource,
            packedLight,
            skinTexture,
            blockEntity,
            partialTick
        )
    }

    private fun getOrLoadSkinTexture(uuid: UUID, name: String): ResourceLocation {
        skinCache[uuid]?.let { return it }

        if (loadingProfiles[uuid] == true) {
            return DefaultPlayerSkin.get(uuid).texture()
        }

        loadingProfiles[uuid] = true

        val gameProfile = GameProfile(uuid, name)
        val skinManager = minecraft.skinManager

        skinManager.getOrLoad(gameProfile).thenAccept { playerSkin: PlayerSkin ->
            skinCache[uuid] = playerSkin.texture()
            loadingProfiles.remove(uuid)
        }

        return skinCache[uuid] ?: DefaultPlayerSkin.get(uuid).texture()
    }

    private fun renderPlayerHead(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        skinTexture: ResourceLocation,
        blockEntity: PhylacteryBlockEntity,
        partialTick: Float
    ) {
        poseStack.pushPose()

        poseStack.translate(0.5, 0.5, 0.5)

        val time = blockEntity.level?.gameTime ?: 0L
        val floatOffset = Mth.sin((time + partialTick) * 0.1f) * 0.05f
        poseStack.translate(0.0, floatOffset.toDouble(), 0.0)

        val rotation = (time + partialTick) * 2f
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation))

        poseStack.scale(0.5f, 0.5f, 0.5f)
        poseStack.scale(-1.0f, -1.0f, 1.0f)

        val renderType =  WitcheryRenderTypes.GHOST_ADDITIVE.apply(skinTexture)

        val buffer = bufferSource.getBuffer(renderType)

        val color = if (blockEntity.hasSoul) {
            val pulse = Mth.sin((time + partialTick) * 0.2f) * 0.15f + 0.85f
            Vec3(pulse.toDouble(), pulse.toDouble(), 1.0)
        } else {
            Vec3(1.0, 1.0, 1.0)
        }

        skullModel.renderToBuffer(
            poseStack,
            buffer,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            Color(color.x.toFloat(), color.y.toFloat(), color.z.toFloat()).rgb
        )

        poseStack.popPose()
    }
}