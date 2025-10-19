package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.BroomEntity
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncOwlAbilityS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, bl: Boolean) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        putBoolean("bl", bl)
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
        val bl = nbt.getBoolean("bl")

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null && player.vehicle is BroomEntity) {
                val broom = player.vehicle as BroomEntity
                broom.hasFamiliar = bl
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncOwlAbilityS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_owl_ability"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncOwlAbilityS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncOwlAbilityS2CPayload(buf) }
            )
    }
}