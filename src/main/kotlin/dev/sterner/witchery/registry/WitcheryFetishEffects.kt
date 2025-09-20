package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.FetishEffect
import dev.sterner.witchery.fetish.*

object WitcheryFetishEffects {

    val ID = Witchery.id("fetish_effect")

    val FETISH_EFFECTS: Registrar<FetishEffect> = RegistrarManager.get(Witchery.MODID).builder<FetishEffect>(ID)
        .syncToClients().build()

    val VOODOO_PROTECTION: RegistrySupplier<FetishEffect> = FETISH_EFFECTS.register(Witchery.id("voodoo_protection")) {
        EmptyFetishEffect()
    }

    val SENTINEL: RegistrySupplier<SentinelFetishEffect> = FETISH_EFFECTS.register(Witchery.id("sentinel")) {
        SentinelFetishEffect()
    }

    val DISORIENTATION: RegistrySupplier<DisorientationFetishEffect> =
        FETISH_EFFECTS.register(Witchery.id("disorientation")) {
            DisorientationFetishEffect()
        }

    val SHRIEKING: RegistrySupplier<ShriekingFetishEffect> = FETISH_EFFECTS.register(Witchery.id("shrieking")) {
        ShriekingFetishEffect()
    }

    val GHOST_WALKING: RegistrySupplier<GhostWalkingFetishEffect> =
        FETISH_EFFECTS.register(Witchery.id("ghost_walking")) {
            GhostWalkingFetishEffect()
        }

    val SUMMON_DEATH: RegistrySupplier<SummonDeathFetishEffect> = FETISH_EFFECTS.register(Witchery.id("summon_death")) {
        SummonDeathFetishEffect()
    }

    fun register() {

    }
}