package dev.sterner.witchery.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncMiscS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object MiscPlayerAttachment {

    @JvmStatic
    fun getData(player: Player): MiscPlayerAttachment.Data {
        return player.getData(WitcheryDataAttachments.MISC_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: MiscPlayerAttachment.Data) {
        player.setData(WitcheryDataAttachments.MISC_PLAYER_DATA_ATTACHMENT, data)
        MiscPlayerAttachment.sync(player, data)
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
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncMiscS2CPayload(player, data))
        }
    }

    class Data(
        var isWitcheryAligned: Boolean = false,
        var isDeath: Boolean = false,
    ) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("isWitcheryAligned").forGetter { it.isWitcheryAligned },
                    Codec.BOOL.fieldOf("isDeath").forGetter { it.isDeath },
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("misc_player_data")
        }
    }
}