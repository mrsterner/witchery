package dev.sterner.witchery.platform

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.entity.SleepingPlayerEntity
import dev.sterner.witchery.entity.SleepingPlayerEntity.Companion.replaceWithPlayer
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.ChunkPos
import java.util.UUID

data class TeleportRequest(
    val player: UUID,
    val pos: BlockPos,
    val chunkPos: ChunkPos
) {
    fun execute(minecraftServer: MinecraftServer): Boolean {
        val serverPlayer = minecraftServer.playerList.getPlayer(player)
        if (serverPlayer != null) {
            val overworld = minecraftServer.overworld()
            serverPlayer.teleportTo(overworld, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, setOf(), serverPlayer.yRot, serverPlayer.xRot)
            val sleepingData = SleepingPlayerLevelAttachment.getPlayerFromSleeping(serverPlayer.uuid, overworld)
            val sleepingPlayer = overworld.getEntity(sleepingData!!.uuid)
            replaceWithPlayer(serverPlayer, sleepingPlayer as SleepingPlayerEntity)

            return true
        }
        return false
    }

    companion object {
        val CODEC: Codec<TeleportRequest> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codecs.UUID.fieldOf("playerUUID").forGetter { it.player },
                BlockPos.CODEC.fieldOf("pos").forGetter { it.pos },
                Codec.LONG.fieldOf("chunkPos").forGetter { it.chunkPos.toLong() }
            ).apply(instance) { playerUUID, pos, chunkPos ->
                TeleportRequest(playerUUID, pos, ChunkPos(chunkPos))
            }
        }
    }
}