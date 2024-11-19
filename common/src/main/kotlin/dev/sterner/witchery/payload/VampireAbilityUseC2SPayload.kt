package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.vampire.VampireEventHandler
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player


class VampireAbilityUseC2SPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(ordinal: Int) : this(CompoundTag().apply {
        putInt("Ordinal", ordinal)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(buf: RegistryFriendlyByteBuf?) {
        buf?.writeNbt(nbt)
    }

    fun handleC2S(payload: VampireAbilityUseC2SPayload, context: NetworkManager.PacketContext?) {
        val player: Player? = context?.player
        val ordinal = payload.nbt.getInt("Ordinal")

        if (player != null) {

            VampireEventHandler.parseAbilityFromIndex(player, ordinal)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<VampireAbilityUseC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("vampire_use_ability"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, VampireAbilityUseC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> VampireAbilityUseC2SPayload(buf!!) }
            )
    }
}