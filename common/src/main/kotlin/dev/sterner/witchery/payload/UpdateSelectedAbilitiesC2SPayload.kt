package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.affliction.AfflictionAbilityHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload


class UpdateSelectedAbilitiesC2SPayload(
    val abilities: List<String>
) : CustomPacketPayload {

    constructor(buf: FriendlyByteBuf) : this(
        buf.readList { it.readUtf() }
    )

    fun write(buf: FriendlyByteBuf) {
        buf.writeCollection(abilities) { b, ability -> b.writeUtf(ability) }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    fun handleC2S(payload: UpdateSelectedAbilitiesC2SPayload, context: NetworkManager.PacketContext) {
        AfflictionAbilityHandler.updateSelectedAbilities(context.player, payload.abilities)
    }

    companion object {
        val ID = CustomPacketPayload.Type<UpdateSelectedAbilitiesC2SPayload>(
            Witchery.id("update_selected_abilities")
        )

        val STREAM_CODEC: StreamCodec<in FriendlyByteBuf, UpdateSelectedAbilitiesC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> UpdateSelectedAbilitiesC2SPayload(buf) }
            )
    }
}