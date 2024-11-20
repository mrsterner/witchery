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
        RangedAttribute("attribute.name.witchery.vampire_drink_speed", 0.0,0.0,250.0).setSyncable(true)
    }
}