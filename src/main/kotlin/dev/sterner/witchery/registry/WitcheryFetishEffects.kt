package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.FetishEffect
import dev.sterner.witchery.fetish.*
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.RegistryBuilder
import java.util.function.Supplier

object WitcheryFetishEffects {

    val ID = Witchery.id("fetish")

    val FETISH_REGISTRY_KEY: ResourceKey<Registry<FetishEffect>> = ResourceKey.createRegistryKey(ID)

    val FETISH_REGISTRY: Registry<FetishEffect> =
        RegistryBuilder(FETISH_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ID)
            .maxId(256)
            .create()

    val FETISH_EFFECTS: DeferredRegister<FetishEffect> = DeferredRegister.create(FETISH_REGISTRY, Witchery.MODID)

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