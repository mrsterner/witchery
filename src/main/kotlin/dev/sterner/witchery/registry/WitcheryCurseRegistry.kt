package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.curse.CurseOfCorruptPoppet
import dev.sterner.witchery.curse.CurseOfInsanity
import dev.sterner.witchery.curse.CurseOfMisfortune
import dev.sterner.witchery.curse.CurseOfOverheating
import dev.sterner.witchery.curse.CurseOfSinking
import dev.sterner.witchery.curse.CurseOfWalkingNightmare
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier


object WitcheryCurseRegistry {

    val ID = Witchery.id("curse")

    val CURSE_REGISTRY_KEY: ResourceKey<Registry<Curse>> = ResourceKey.createRegistryKey(ID)

    val CURSES: DeferredRegister<Curse> = DeferredRegister.create(CURSE_REGISTRY_KEY, Witchery.MODID)

    val CORRUPT_POPPET = CURSES.register("corrupt_poppet", Supplier { CurseOfCorruptPoppet() })
    val INSANITY = CURSES.register("insanity", Supplier { CurseOfInsanity() })
    val MISFORTUNE = CURSES.register("misfortune", Supplier { CurseOfMisfortune() })
    val OVERHEATING = CURSES.register("overheating", Supplier { CurseOfOverheating() })
    val SINKING = CURSES.register("sinking", Supplier { CurseOfSinking() })
    val WALKING_NIGHTMARE = CURSES.register("walking_nightmare", Supplier { CurseOfWalkingNightmare() })

}
