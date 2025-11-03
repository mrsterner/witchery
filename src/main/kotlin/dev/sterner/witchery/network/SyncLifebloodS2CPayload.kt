package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.features.lifeblood.LifebloodPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncLifebloodS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: LifebloodPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        LifebloodPlayerAttachment.Data.CODEC.encodeStart(NbtOps.INSTANCE, data).resultOrPartial().let {
            put("lifeBlood", it.get())
        }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()

        val id = nbt.getUUID("Id")

        val dataTag = nbt.getCompound("lifeBlood")
        val lifebloodData = LifebloodPlayerAttachment.Data.CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null && lifebloodData.isPresent) {
                LifebloodPlayerAttachment.setData(player, lifebloodData.get())
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncLifebloodS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_lifeblood_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncLifebloodS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncLifebloodS2CPayload(buf) }
            )
    }
}