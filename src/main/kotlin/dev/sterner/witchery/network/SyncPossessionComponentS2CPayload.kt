package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.features.curse.CursePlayerAttachment
import dev.sterner.witchery.features.possession.PossessionComponentAttachment
import dev.sterner.witchery.network.SyncCurseS2CPayload
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

class SyncPossessionComponentS2CPayload(
    val nbt: CompoundTag
) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: PossessionComponentAttachment.PossessionData) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        PossessionComponentAttachment.PossessionData.CODEC.encodeStart(NbtOps.INSTANCE, data).resultOrPartial().let {
            put("data", it.get())
        }
    })


    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val level = client.level ?: return

        val id = nbt.getUUID("Id")

        val player = client.level?.getPlayerByUUID(id)
        val dataTag = nbt.getCompound("data")
        val data = PossessionComponentAttachment.PossessionData.CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()


        if (player is Player && data.isPresent) {
            client.execute {
                PossessionComponentAttachment.setPossessionData(player, data.get())

                if (player == client.player) {
                    val possessedEntity = if (data.get().possessedEntityId != -1) {
                        level.getEntity(data.get().possessedEntityId)
                    } else null

                    if (client.options.cameraType.isFirstPerson) {
                        updateCamera(client.player, possessedEntity ?: player)
                    }
                }
            }
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<SyncPossessionComponentS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_possession_component"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncPossessionComponentS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncPossessionComponentS2CPayload(buf) }
            )

        private fun updateCamera(player: Player?, cameraEntity: Entity?) {
            val mc: Minecraft = Minecraft.getInstance()
            if (mc.options.cameraType.isFirstPerson && player === mc.player) {
                mc.gameRenderer.checkEntityPostEffect(cameraEntity)
            }
        }
    }
}