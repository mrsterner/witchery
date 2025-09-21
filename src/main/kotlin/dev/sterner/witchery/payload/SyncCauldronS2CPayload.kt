package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.cauldron.CauldronBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class SyncCauldronS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(pos: BlockPos, full: Boolean) : this(
        CompoundTag().apply {
            putInt("x", pos.x)
            putInt("y", pos.y)
            putInt("z", pos.z)
            putBoolean("full", full)
        }
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val pos = BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"))
        val full = nbt.getBoolean("full")
        client.execute {
            if (client.level?.getBlockEntity(pos) is CauldronBlockEntity) {
                (client.level?.getBlockEntity(pos) as CauldronBlockEntity).resetCauldronPartial()
                if (full) {
                    (client.level?.getBlockEntity(pos) as CauldronBlockEntity).fullReset()
                }
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncCauldronS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("cauldron_sync"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncCauldronS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncCauldronS2CPayload(buf) }
            )
    }
}