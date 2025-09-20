package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.affliction.AfflictionAbilityHandler
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.handling.IPayloadContext


class AfflictionAbilitySelectionC2SPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(index: Int) : this(CompoundTag().apply {
        putInt("Index", index)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(nbt)
    }

    fun handleOnServer(ctx: IPayloadContext) {
        val player: Player? = ctx.player()
        val index = nbt.getInt("Index")

        if (player != null) {
            AfflictionAbilityHandler.updateAbilityIndex(player, index)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<AfflictionAbilitySelectionC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("affliction_select_ability"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, AfflictionAbilitySelectionC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> AfflictionAbilitySelectionC2SPayload(buf) }
            )
    }
}