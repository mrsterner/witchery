package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext


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

    fun handleOnServer(ctx: IPayloadContext) {
        AfflictionAbilityHandler.updateSelectedAbilities(ctx.player(), abilities)
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