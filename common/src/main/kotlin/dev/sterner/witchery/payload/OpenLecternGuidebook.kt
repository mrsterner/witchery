package dev.sterner.witchery.payload

import com.klikli_dev.modonomicon.client.gui.BookGuiManager
import com.klikli_dev.modonomicon.client.gui.book.BookAddress
import com.klikli_dev.modonomicon.data.BookDataManager
import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.GuideBookItem
import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class OpenLecternGuidebook() : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this()

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {}

    fun handleS2C(payload: OpenLecternGuidebook, context: NetworkManager.PacketContext) {
        context.queue {
            val book = BookDataManager.get().getBook(GuideBookItem.ID)
            BookGuiManager.get().openBook(BookAddress.defaultFor(book))
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<OpenLecternGuidebook> =
            CustomPacketPayload.Type(Witchery.id("open_lectern_guidebook"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, OpenLecternGuidebook> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { OpenLecternGuidebook() }
            )
    }
}