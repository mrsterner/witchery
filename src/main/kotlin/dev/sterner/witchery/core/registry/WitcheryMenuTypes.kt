package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.menu.AltarMenu
import dev.sterner.witchery.content.menu.DistilleryMenu
import dev.sterner.witchery.content.menu.OvenMenu
import dev.sterner.witchery.content.menu.SpinningWheelMenu
import net.minecraft.core.registries.Registries
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier


object WitcheryMenuTypes {

    val MENU_TYPES: DeferredRegister<MenuType<*>> = DeferredRegister.create(Registries.MENU, Witchery.MODID)

    val OVEN_MENU_TYPE = MENU_TYPES.register("oven_menu", Supplier {
        IMenuTypeExtension.create { windowId, inventory, data ->
            OvenMenu(windowId, inventory, data)
        }
    })

    val ALTAR_MENU_TYPE = MENU_TYPES.register("altar_menu", Supplier {
        IMenuTypeExtension.create(::AltarMenu)
    })

    val DISTILLERY_MENU_TYPE = MENU_TYPES.register("distillery_menu", Supplier {
        IMenuTypeExtension.create(::DistilleryMenu)
    })

    val SPINNING_WHEEL_MENU_TYPE =
        MENU_TYPES.register("spinning_wheel_menu", Supplier {
            IMenuTypeExtension.create(::SpinningWheelMenu)
        })
}