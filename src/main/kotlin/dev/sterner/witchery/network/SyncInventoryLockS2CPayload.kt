package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.features.misc.InventoryLockPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player


class SyncInventoryLockS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    constructor(player: Player, data: InventoryLockPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        InventoryLockPlayerAttachment.Data.CODEC.encodeStart(NbtOps.INSTANCE, data).resultOrPartial().let {
            put("playerInventoryLock", it.get())
        }
    })

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val dataTag = nbt.getCompound("playerInventoryLock")
        val playerData = InventoryLockPlayerAttachment.Data.CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()

        val id = nbt.getUUID("Id")
        val player = client.level?.getPlayerByUUID(id)
        client.execute {
            if (player != null && playerData.isPresent) {
                InventoryLockPlayerAttachment.setData(player, playerData.get())
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncInventoryLockS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_inventory_lock"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncInventoryLockS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncInventoryLockS2CPayload(buf) }
            )
    }
}