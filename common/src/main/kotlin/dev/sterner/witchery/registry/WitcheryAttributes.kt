package dev.sterner.witchery.registry

import com.google.common.base.Suppliers
import dev.architectury.registry.registries.RegistrarManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.Witchery.MODID
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import java.util.function.Supplier


object WitcheryAttributes {

    val MANAGER: Supplier<RegistrarManager> = Suppliers.memoize {
        RegistrarManager.get(
            MODID
        )
    }

    var attributes = MANAGER.get().get(Registries.ATTRIBUTE)

    val VAMPIRE_DRINK_SPEED = attributes.register(Witchery.id("vampire_drink_speed")) {
        RangedAttribute("attribute.name.witchery.vampire_drink_speed", 0.0,0.0,255.0).setSyncable(true)
    }

    val VAMPIRE_BAT_FORM_DURATION = attributes.register(Witchery.id("vampire_bat_form_duration")) {
        RangedAttribute("attribute.name.witchery.vampire_bat_form_duration", 120.0,0.0,255.0).setSyncable(true)
    }

    val VAMPIRE_SUN_RESISTANCE = attributes.register(Witchery.id("vampire_sun_resistance")) {
        RangedAttribute("attribute.name.witchery.vampire_sun_resistance", 100.0,100.0,512.0).setSyncable(true)
    }
}