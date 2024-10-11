package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.GuideBookItem
import dev.sterner.witchery.item.MutandisItem
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item


object WitcheryItems {

    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(Witchery.MODID, Registries.ITEM)

    val MUTANDIS = ITEMS.register("mutandis") {
        MutandisItem(Item.Properties())
    }

    val GUIDEBOOK: RegistrySupplier<GuideBookItem> = ITEMS.register("guidebook") {
        GuideBookItem(Item.Properties())
    }
}