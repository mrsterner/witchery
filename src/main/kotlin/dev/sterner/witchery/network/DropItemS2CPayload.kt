package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.neoforged.neoforge.network.handling.IPayloadContext

class DropItemS2CPayload() : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this()

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {}

    fun handleOnClient(ctx: IPayloadContext) {
        val client = Minecraft.getInstance()
        client.execute {
            val player = client.player ?: return@execute

            if (!player.isSpectator && player.drop(false)) {
                player.swing(InteractionHand.MAIN_HAND)
                player.playSound(SoundEvents.ARMADILLO_SCUTE_DROP)
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<DropItemS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("drop_item"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, DropItemS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { DropItemS2CPayload() }
            )
    }
}