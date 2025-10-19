package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.curse.*
import dev.sterner.witchery.features.curse.CurseOfBefuddlement
import dev.sterner.witchery.features.curse.CurseOfCorruptPoppet
import dev.sterner.witchery.features.curse.CurseOfFragility
import dev.sterner.witchery.features.curse.CurseOfHunger
import dev.sterner.witchery.features.curse.CurseOfInsanity
import dev.sterner.witchery.features.curse.CurseOfMisfortune
import dev.sterner.witchery.features.curse.CurseOfOverheating
import dev.sterner.witchery.features.curse.CurseOfSinking
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.neoforged.bus.api.IEventBus
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

    private val CURSES: DeferredRegister<Curse> = DeferredRegister.create(CURSES_REGISTRY, Witchery.MODID)

    val CORRUPT_POPPET = CURSES.register("corrupt_poppet", Supplier { CurseOfCorruptPoppet() })
    val FRAGILITY = CURSES.register("fragility", Supplier { CurseOfFragility() })
    val HUNGER = CURSES.register("hunger", Supplier { CurseOfHunger() })
    val BEFUDDLEMENT = CURSES.register("befuddlement", Supplier { CurseOfBefuddlement() })
    val INSANITY = CURSES.register("insanity", Supplier { CurseOfInsanity() })
    val MISFORTUNE = CURSES.register("misfortune", Supplier { CurseOfMisfortune() })
    val OVERHEATING = CURSES.register("overheating", Supplier { CurseOfOverheating() })
    val SINKING = CURSES.register("sinking", Supplier { CurseOfSinking() })

    fun register(modEventBus: IEventBus) {
        CURSES.register(modEventBus)
    }
}
