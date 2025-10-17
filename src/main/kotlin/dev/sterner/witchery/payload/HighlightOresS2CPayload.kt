package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.OreHighlightRenderer
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class HighlightOresS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(positions: List<BlockPos>, duration: Int) : this(
        CompoundTag().apply {
            val posArray = IntArray(positions.size * 3)
            positions.forEachIndexed { index, pos ->
                posArray[index * 3] = pos.x
                posArray[index * 3 + 1] = pos.y
                posArray[index * 3 + 2] = pos.z
            }
            putIntArray("positions", posArray)
            putInt("duration", duration)
        }
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        client.execute {
            val posArray = nbt.getIntArray("positions")
            val duration = nbt.getInt("duration")
            val positions = mutableListOf<BlockPos>()

            for (i in posArray.indices step 3) {
                if (i + 2 < posArray.size) {
                    positions.add(BlockPos(posArray[i], posArray[i + 1], posArray[i + 2]))
                }
            }

            OreHighlightRenderer.addHighlightedOres(positions, duration)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<HighlightOresS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("highlight_ores"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, HighlightOresS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> HighlightOresS2CPayload(buf) }
            )
    }
}