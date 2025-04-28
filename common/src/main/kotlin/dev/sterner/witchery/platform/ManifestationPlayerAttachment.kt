package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.util.RenderUtils
import dev.sterner.witchery.payload.SyncManifestationS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ChunkPos

object ManifestationPlayerAttachment {


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



    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(
                player.level(),
                player.blockPosition(),
                SyncManifestationS2CPacket(player, data)
            )
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

            val ID: ResourceLocation = Witchery.id("manifestation_player_data")
        }
    }
}