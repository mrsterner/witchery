package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.DebugAABBRenderer
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.phys.AABB

class DebugAABBRenderS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(aabb: AABB, color: Int, durationTicks: Int) : this(
        CompoundTag().apply {
            putDouble("minX", aabb.minX)
            putDouble("minY", aabb.minY)
            putDouble("minZ", aabb.minZ)
            putDouble("maxX", aabb.maxX)
            putDouble("maxY", aabb.maxY)
            putDouble("maxZ", aabb.maxZ)
            putInt("color", color)
            putInt("duration", durationTicks)
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

        val aabb = AABB(
            nbt.getDouble("minX"),
            nbt.getDouble("minY"),
            nbt.getDouble("minZ"),
            nbt.getDouble("maxX"),
            nbt.getDouble("maxY"),
            nbt.getDouble("maxZ")
        )
        val color = nbt.getInt("color")
        val duration = nbt.getInt("duration")

        client.execute {
            DebugAABBRenderer.addAABB(aabb, color, duration)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<DebugAABBRenderS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("debug_aabb_render"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, DebugAABBRenderS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> DebugAABBRenderS2CPayload(buf) }
            )
    }
}