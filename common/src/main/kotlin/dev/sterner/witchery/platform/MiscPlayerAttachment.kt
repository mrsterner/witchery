package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncMiscS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object MiscPlayerAttachment {

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
     * This should declare a player a "Witch" so when a player has interacted enough with witchery to start being affected by its curses and rites.
     * This is an insurance to player who don't care about witchery not being absolutely wrecked by its rites, poppets and curses.
     *
     * Note: What exactly constitutes as being a witch is not yet defined, maybe when a player has a certain amount of witchery advancements
     */
    fun setWitcheryAligned(player: Player, aligned: Boolean) {
        val old = getData(player)
        old.isWitcheryAligned = aligned
        setData(player, old)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncMiscS2CPacket(player, data))
        }
    }

    class Data(var isWitcheryAligned: Boolean = false) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("isWitcheryAligned").forGetter { it.isWitcheryAligned },
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("misc_player_data")
        }
    }
}