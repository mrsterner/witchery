package dev.sterner.witchery.features.spirit_world

import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.core.api.TeleportRequest
import dev.sterner.witchery.features.misc.TeleportQueueHandler
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


}