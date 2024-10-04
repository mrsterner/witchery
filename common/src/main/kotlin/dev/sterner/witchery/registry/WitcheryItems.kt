package dev.sterner.witchery.registry

import dev.architectury.core.item.ArchitecturyBucketItem
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.GuideBookItem
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items


object WitcheryItems {

    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(Witchery.MODID, Registries.ITEM)

    val GUIDEBOOK: RegistrySupplier<GuideBookItem> = ITEMS.register("guidebook") {
        GuideBookItem(Item.Properties())
    }
}