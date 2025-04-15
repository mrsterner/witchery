package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.particle.ColorBubbleData
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.util.Mth

class CauldronPoofS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(pos: BlockPos, color: Int) : this(
        CompoundTag().apply {
            putInt("x", pos.x)
            putInt("y", pos.y)
            putInt("z", pos.z)
            putInt("color", color)
        }
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {
        friendlyByteBuf?.writeNbt(nbt)
    }

    fun handleS2C(payload: CauldronPoofS2CPacket, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()
        val pos = BlockPos(payload.nbt.getInt("x"), payload.nbt.getInt("y"), payload.nbt.getInt("z"))
        val color = nbt.getInt("color")
        client.execute {

            for (i in 0..32) {
                client.level!!.addAlwaysVisibleParticle(
                    ColorBubbleData(
                        ((color shr 16) and 0xff) / 255.0f,
                        ((color shr 8) and 0xff) / 255.0f,
                        (color and 0xff) / 255.0f
                    ),
                    true,
                    pos.x + 0.5 + Mth.nextDouble(client.level!!.random, -0.25, 0.25),
                    (pos.y + 1.0),
                    pos.z + 0.5 + Mth.nextDouble(client.level!!.random, -0.25, 0.25),
                    0.0, 0.2, 0.0
                )
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<CauldronPoofS2CPacket> =
            CustomPacketPayload.Type(Witchery.id("cauldron_smoke"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, CauldronPoofS2CPacket> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> CauldronPoofS2CPacket(buf) }
            )
    }
}