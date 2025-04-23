package dev.sterner.witchery.registry

import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.api.FetishEffect
import dev.sterner.witchery.curse.CurseOfCorruptPoppet
import dev.sterner.witchery.fetish.EmptyFetishEffect

object WitcheryEffigyEffects {

    val ID = Witchery.id("fetish_effect")

    val FETISH_EFFECTS: Registrar<FetishEffect> = RegistrarManager.get(Witchery.MODID).builder<FetishEffect>(ID)
        .syncToClients().build()

    val VOODOO_PROTECTION: RegistrySupplier<FetishEffect> = FETISH_EFFECTS.register(Witchery.id("voodoo_protection")) {
        EmptyFetishEffect()
    }

    val SENTINEL: RegistrySupplier<FetishEffect> = FETISH_EFFECTS.register(Witchery.id("sentinel")) {
        EmptyFetishEffect()
    }

    val DISORIENTATION: RegistrySupplier<FetishEffect> = FETISH_EFFECTS.register(Witchery.id("disorientation")) {
        EmptyFetishEffect()
    }

    val SHRIEKING: RegistrySupplier<FetishEffect> = FETISH_EFFECTS.register(Witchery.id("shrieking")) {
        EmptyFetishEffect()
    }

    val GHOST_WALKING: RegistrySupplier<FetishEffect> = FETISH_EFFECTS.register(Witchery.id("ghost_walking")) {
        EmptyFetishEffect()
    }

    fun init() {

    }
}