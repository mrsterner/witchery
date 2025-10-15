package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.LeonardsUrnItem
import dev.sterner.witchery.item.QuartzSphereItem
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.neoforged.neoforge.network.handling.IPayloadContext

class SelectUrnPotionC2SPayload(val index: Int) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readInt())

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    private fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeInt(index)
    }

    fun handleOnServer(ctx: IPayloadContext) {
        val player = ctx.player() as? ServerPlayer ?: return
        val heldItem = player.mainHandItem

        if (heldItem.item !is QuartzSphereItem) return

        val urn = LeonardsUrnItem.findUrn(player) ?: return
        val potions = LeonardsUrnItem.getStoredPotions(urn)

        if (index >= 0 && index < potions.size) {
            val selectedPotion = potions[index]
            QuartzSphereItem.setLoadedPotion(heldItem, selectedPotion.copy())

            player.playSound(
                SoundEvents.EXPERIENCE_ORB_PICKUP,
                1.0f,
                1.2f
            )
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SelectUrnPotionC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("select_urn_potion"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SelectUrnPotionC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SelectUrnPotionC2SPayload(buf) }
            )
    }
}