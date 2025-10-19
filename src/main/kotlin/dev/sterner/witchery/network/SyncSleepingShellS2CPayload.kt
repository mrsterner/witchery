package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.entity.PlayerShellEntity
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.item.component.ResolvableProfile

class SyncSleepingShellS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(shell: PlayerShellEntity) : this(CompoundTag().apply {
        putInt("Id", shell.id)
        ResolvableProfile.CODEC.encodeStart(NbtOps.INSTANCE, shell.data.resolvableProfile).resultOrPartial().let {
            put("ResolvableProfile", it.get())
        }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val resolvableProfile = ResolvableProfile.CODEC.parse(NbtOps.INSTANCE, nbt.get("ResolvableProfile"))
        val id = nbt.getInt("Id")
        client.execute {
            val entity = client.level?.getEntity(id)
            if (entity is PlayerShellEntity) {
                entity.entityData.set(PlayerShellEntity.RESOLVEABLE, resolvableProfile.result().get())
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncSleepingShellS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_sleeping_shell"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncSleepingShellS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncSleepingShellS2CPayload(buf) }
            )
    }
}
