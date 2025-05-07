package dev.sterner.witchery.platform

import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.core.Holder
import net.minecraft.world.entity.ai.attributes.Attribute


object WitcheryAttributes {

    @JvmStatic
    @get:ExpectPlatform
    val VAMPIRE_DRINK_SPEED: Holder<Attribute>
        get() {
            throw AssertionError()
        }

    @JvmStatic
    @get:ExpectPlatform
    val VAMPIRE_BAT_FORM_DURATION: Holder<Attribute>
        get() {
            throw AssertionError()
        }

    @JvmStatic
    @get:ExpectPlatform
    val VAMPIRE_SUN_RESISTANCE: Holder<Attribute>
        get() {
            throw AssertionError()
        }
}