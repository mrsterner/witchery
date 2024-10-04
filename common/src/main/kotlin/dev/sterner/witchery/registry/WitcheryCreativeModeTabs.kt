package dev.sterner.witchery.registry

import dev.architectury.registry.CreativeTabRegistry
import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryItems.GUIDEBOOK
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items

object WitcheryCreativeModeTabs {

    val TABS = DeferredRegister.create(Witchery.MODID, Registries.CREATIVE_MODE_TAB)

    val MAIN = TABS.register("main") {
        CreativeTabRegistry.create {
            it.title(Component.translatable("witchery.main"))
            it.icon { GUIDEBOOK.get().defaultInstance }
            it.displayItems { _, output ->
                output.accept(GUIDEBOOK.get())
            }
        }
    }
}