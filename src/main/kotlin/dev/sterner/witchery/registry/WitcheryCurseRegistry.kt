package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.curse.*
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.RegistryBuilder
import java.util.function.Supplier


object WitcheryCurseRegistry {

    val ID = Witchery.id("curse")

    val CURSES_REGISTRY_KEY: ResourceKey<Registry<Curse>> = ResourceKey.createRegistryKey(ID)

    val CURSES_REGISTRY: Registry<Curse> =
        RegistryBuilder(CURSES_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ID)
            .maxId(256)
            .create()

    val CURSES: DeferredRegister<Curse> = DeferredRegister.create(CURSES_REGISTRY, Witchery.MODID)

    val CORRUPT_POPPET = CURSES.register("corrupt_poppet", Supplier { CurseOfCorruptPoppet() })
    val INSANITY = CURSES.register("insanity", Supplier { CurseOfInsanity() })
    val MISFORTUNE = CURSES.register("misfortune", Supplier { CurseOfMisfortune() })
    val OVERHEATING = CURSES.register("overheating", Supplier { CurseOfOverheating() })
    val SINKING = CURSES.register("sinking", Supplier { CurseOfSinking() })
    val WALKING_NIGHTMARE = CURSES.register("walking_nightmare", Supplier { CurseOfWalkingNightmare() })

}
