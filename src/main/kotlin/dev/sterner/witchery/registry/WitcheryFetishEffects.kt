package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.FetishEffect
import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.fetish.*
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object WitcheryFetishEffects {

    val ID = Witchery.id("fetish_effect")

    val FETISH_REGISTRY_KEY: ResourceKey<Registry<FetishEffect>> = ResourceKey.createRegistryKey(WitcheryRitualRegistry.ID)

    val FETISH_EFFECTS: DeferredRegister<FetishEffect> = DeferredRegister.create(FETISH_REGISTRY_KEY, Witchery.MODID)

    val VOODOO_PROTECTION = FETISH_EFFECTS.register("voodoo_protection", Supplier {
        EmptyFetishEffect()
    })

    val SENTINEL = FETISH_EFFECTS.register("sentinel", Supplier {
        SentinelFetishEffect()
    })

    val DISORIENTATION =
        FETISH_EFFECTS.register("disorientation", Supplier {
            DisorientationFetishEffect()
        })

    val SHRIEKING = FETISH_EFFECTS.register("shrieking", Supplier {
        ShriekingFetishEffect()
    })

    val GHOST_WALKING =
        FETISH_EFFECTS.register("ghost_walking", Supplier {
            GhostWalkingFetishEffect()
        })

    val SUMMON_DEATH = FETISH_EFFECTS.register("summon_death", Supplier {
        SummonDeathFetishEffect()
    })

    fun register() {

    }
}