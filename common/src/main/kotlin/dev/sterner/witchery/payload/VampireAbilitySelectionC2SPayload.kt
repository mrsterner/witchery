package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.vampire.VampireAbilities
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player


class VampireAbilitySelectionC2SPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(index: Int) : this(CompoundTag().apply {
        putInt("Index", index)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(buf: RegistryFriendlyByteBuf?) {
        buf?.writeNbt(nbt)
    }

    fun handleC2S(payload: VampireAbilitySelectionC2SPayload, context: NetworkManager.PacketContext?) {
        val player: Player? = context?.player
        val index = payload.nbt.getInt("Index")

        if (player != null) {
            VampireAbilities.updateAbilityIndex(player, index)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<VampireAbilitySelectionC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("vampire_select_ability"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, VampireAbilitySelectionC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> VampireAbilitySelectionC2SPayload(buf) }
            )
    }
}