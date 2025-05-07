package dev.sterner.witchery.platform.fabric

import com.google.common.base.Suppliers
import dev.architectury.registry.registries.RegistrarManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.Witchery.MODID
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import java.util.function.Supplier

object WitcheryAttributesImpl {

    val MANAGER: Supplier<RegistrarManager> = Suppliers.memoize {
        RegistrarManager.get(
            MODID
        )
    }

    var attributes = MANAGER.get().get(Registries.ATTRIBUTE)

    @JvmStatic
    fun getVAMPIRE_DRINK_SPEED(): Holder<Attribute> {
        return VAMPIRE_DRINK_SPEED
    }

    @JvmStatic
    val VAMPIRE_DRINK_SPEED = attributes.register(Witchery.id("vampire_drink_speed")) {
        RangedAttribute("attribute.name.witchery.vampire_drink_speed", 0.0, 0.0, 255.0).setSyncable(false)
    }

    @JvmStatic
    fun getVAMPIRE_BAT_FORM_DURATION(): Holder<Attribute> {
        return VAMPIRE_BAT_FORM_DURATION
    }

    @JvmStatic
    val VAMPIRE_BAT_FORM_DURATION = attributes.register(Witchery.id("vampire_bat_form_duration")) {
        RangedAttribute("attribute.name.witchery.vampire_bat_form_duration", 120.0 * 20, 0.0, 255.0 * 20).setSyncable(
            false
        )
    }

    @JvmStatic
    fun getVAMPIRE_SUN_RESISTANCE(): Holder<Attribute> {
        return VAMPIRE_SUN_RESISTANCE
    }

    @JvmStatic
    val VAMPIRE_SUN_RESISTANCE = attributes.register(Witchery.id("vampire_sun_resistance")) {
        RangedAttribute("attribute.name.witchery.vampire_sun_resistance", 100.0, 100.0, 512.0).setSyncable(false)
    }
}