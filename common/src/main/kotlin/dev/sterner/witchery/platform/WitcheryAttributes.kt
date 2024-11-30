package dev.sterner.witchery.platform

import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.BoneNeedleItem
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.RangedAttribute


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