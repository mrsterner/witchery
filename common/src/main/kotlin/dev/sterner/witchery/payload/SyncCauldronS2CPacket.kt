package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.cauldron.CauldronBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class SyncCauldronS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(pos: BlockPos) : this(
        CompoundTag().apply {
            putInt("x", pos.x)
            putInt("y", pos.y)
            putInt("z", pos.z)
        }
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {
        friendlyByteBuf?.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncCauldronS2CPacket, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()
        val pos = BlockPos(payload.nbt.getInt("x"), payload.nbt.getInt("y"), payload.nbt.getInt("z"))
        client.execute {
            if (client.level?.getBlockEntity(pos) is CauldronBlockEntity) {
                (client.level?.getBlockEntity(pos) as CauldronBlockEntity).resetCauldronPartial()
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncCauldronS2CPacket> =
            CustomPacketPayload.Type(Witchery.id("cauldron_sync"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, SyncCauldronS2CPacket> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncCauldronS2CPacket(buf!!) }
            )
    }
}