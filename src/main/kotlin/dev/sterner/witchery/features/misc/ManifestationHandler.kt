package dev.sterner.witchery.features.misc

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.core.data_attachment.ManifestationPlayerAttachment
import dev.sterner.witchery.core.data_attachment.teleport.TeleportRequest
import dev.sterner.witchery.core.util.RenderUtils
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ChunkPos

object ManifestationHandler {

    const val MAX_TIME = 2400


    /**
     * True if the player may use a Spirit Portal back to the material world as a ghost
     */
    fun setHasRiteOfManifestation(player: Player, hasRite: Boolean) {
        val old = ManifestationPlayerAttachment.getData(player)
        old.hasRiteOfManifestation = hasRite
        ManifestationPlayerAttachment.setData(player, old)
        WitcheryApi.makePlayerWitchy(player)
    }

    fun setManifestationTimer(player: Player) {
        val data = ManifestationPlayerAttachment.getData(player)
        data.manifestationTimer = MAX_TIME
        ManifestationPlayerAttachment.setData(player, data)
    }

    fun tick(server: MinecraftServer) {
        for (player in server.playerList.players) {
            val data = ManifestationPlayerAttachment.getData(player)

            if (data.manifestationTimer > 0) {
                data.manifestationTimer -= 1

                player.armorSlots.forEach { stack ->
                    if (!stack.isEmpty) {
                        player.drop(stack.split(stack.count), false)
                    }
                }

                if (data.manifestationTimer <= 0) {
                    val overworld = server.overworld()
                    val sleepingData = SleepingPlayerHandler.getPlayerFromSleeping(player.uuid, overworld)
                    player.inventory.dropAll()
                    if (sleepingData != null) {
                        val chunkPos = ChunkPos(sleepingData.pos)
                        overworld.setChunkForced(chunkPos.x, chunkPos.z, true)

                        TeleportQueueHandler.addRequest(
                            overworld,
                            TeleportRequest(
                                player = player.uuid,
                                pos = sleepingData.pos,
                                chunkPos = chunkPos,
                                player.level().gameTime
                            )
                        )
                    }
                }
                ManifestationPlayerAttachment.setData(player, data)
            }
        }
    }

    fun renderHud(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker?) {
        val minecraft = Minecraft.getInstance()
        val clientPlayer = minecraft.player ?: return

        val data = ManifestationPlayerAttachment.getData(clientPlayer)
        if (data.manifestationTimer <= 0) return

        val scaledY = minecraft.window.guiScaledHeight
        val chargePercentage = data.manifestationTimer.toFloat() / MAX_TIME

        RenderUtils.blitWithAlpha(
            guiGraphics.pose(),
            Witchery.id("textures/gui/zzz_meter_overlay.png"),
            10 + 18,
            scaledY / 2 - (24 / 2),
            0f,
            0f,
            12,
            24,
            12,
            24,
            1f
        )

        val overlayHeight = ((1f - chargePercentage) * 24).toInt()
        RenderUtils.blitWithAlpha(
            guiGraphics.pose(),
            Witchery.id("textures/gui/zzz_meter.png"),
            10 + 18,
            scaledY / 2 - (24 / 2),
            0f,
            0f,
            12,
            overlayHeight,
            12,
            24,
            1f
        )
    }
}