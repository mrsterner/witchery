package dev.sterner.witchery.network

import com.klikli_dev.modonomicon.client.gui.BookGuiManager
import com.klikli_dev.modonomicon.client.gui.book.BookAddress
import com.klikli_dev.modonomicon.data.BookDataManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.GuideBookItem
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

class OpenLecternGuidebookS2CPayload() : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this()

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {}

    fun handleOnClient(ctx: IPayloadContext) {
        ctx.enqueueWork {
            val book = BookDataManager.get().getBook(GuideBookItem.ID)
            BookGuiManager.get().openBook(BookAddress.defaultFor(book))
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<OpenLecternGuidebookS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("open_lectern_guidebook"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, OpenLecternGuidebookS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { OpenLecternGuidebookS2CPayload() }
            )
    }
}