package dev.sterner.witchery.registry

import dev.architectury.registry.menu.MenuRegistry
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.menu.AltarMenu
import dev.sterner.witchery.menu.DistilleryMenu
import dev.sterner.witchery.menu.OvenMenu
import net.minecraft.core.registries.Registries
import net.minecraft.world.inventory.MenuType


object WitcheryMenuTypes {

    val MENU_TYPES = DeferredRegister.create(Witchery.MODID, Registries.MENU)

    val OVEN_MENU_TYPE: RegistrySupplier<MenuType<OvenMenu>> = MENU_TYPES.register("oven_menu") {
        MenuRegistry.ofExtended { windowId, inventory, data ->
            OvenMenu(windowId, inventory, data)
        }
    }

    val ALTAR_MENU_TYPE = MENU_TYPES.register("altar_menu") {
        MenuRegistry.ofExtended(::AltarMenu)
    }

    val DISTILLERY_MENU_TYPE = MENU_TYPES.register("distillery_menu") {
        MenuRegistry.ofExtended(::DistilleryMenu)
    }
}