package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncManifestationS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ChunkPos

object PlayerManifestationDataAttachment {

    @ExpectPlatform
    @JvmStatic
    fun getData(player: Player): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(player: Player, data: Data) {
        throw AssertionError()
    }

    /**
     * True if the player may use a Spirit Portal back to the material world as a ghost
     */
    fun setHasRiteOfManifestation(player: Player, hasRite: Boolean) {
        val old = getData(player)
        old.hasRiteOfManifestation = hasRite
        setData(player, old)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncManifestationS2CPacket(player, data))
        }
    }

    fun setManifestationTimer(player: Player) {
        val data = getData(player)
        data.manifestationTimer = 2400
        setData(player, data)
    }

    fun tick(server: MinecraftServer) {
        for (player in server.playerList.players) {
            val data = getData(player)

            if (data.manifestationTimer > 0) {
                data.manifestationTimer -= 1

                player.armorSlots.forEach { stack ->
                    if (!stack.isEmpty) {
                        player.drop(stack.split(stack.count), false)
                    }
                }

                if (data.manifestationTimer <= 0) {
                    val overworld = server.overworld()
                    val sleepingData = SleepingPlayerLevelAttachment.getPlayerFromSleeping(player.uuid, overworld)
                    player.inventory.dropAll()
                    if (sleepingData != null) {
                        val chunkPos = ChunkPos(sleepingData.pos)
                        overworld.setChunkForced(chunkPos.x, chunkPos.z, true)

                        TeleportQueueLevelAttachment.addRequest(
                            overworld,
                            TeleportRequest(
                                player = player.uuid,
                                pos = sleepingData.pos,
                                chunkPos = chunkPos
                            )
                        )
                    }
                }
                setData(player, data)
            }
        }
    }

    class Data(var hasRiteOfManifestation: Boolean = false, var manifestationTimer: Int = 0) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("hasRiteOfManifestation").forGetter { it.hasRiteOfManifestation },
                    Codec.INT.fieldOf("manifestationTimer").forGetter { it.manifestationTimer }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("misc_player_data")
        }
    }
}