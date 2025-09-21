package dev.sterner.witchery.api.event

import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn


@OnlyIn(Dist.CLIENT)
interface ClientRawInputEvent {

    interface MouseScrolled {
        fun mouseScrolled(client: Minecraft?, amountX: Double, amountY: Double): EventResult?
    }

    companion object {
        val MOUSE_SCROLLED: Event<MouseScrolled> = EventFactory.createEventResult()
    }
}