package dev.sterner.witchery.registry

import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.curse.*


object WitcheryCurseRegistry {

    val ID = Witchery.id("curse")

    val CURSES: Registrar<Curse> = RegistrarManager.get(Witchery.MODID).builder<Curse>(ID)
        .syncToClients().build()

    val CORRUPT_POPPET: RegistrySupplier<CurseOfCorruptPoppet> = CURSES.register(Witchery.id("corrupt_poppet")) {
        CurseOfCorruptPoppet()
    }

    val INSANITY: RegistrySupplier<CurseOfInsanity> = CURSES.register(Witchery.id("insanity")) {
        CurseOfInsanity()
    }

    val MISFORTUNE: RegistrySupplier<CurseOfMisfortune> = CURSES.register(Witchery.id("misfortune")) {
        CurseOfMisfortune()
    }

    val OVERHEATING: RegistrySupplier<CurseOfOverheating> = CURSES.register(Witchery.id("overheating")) {
        CurseOfOverheating()
    }

    val SINKING: RegistrySupplier<CurseOfSinking> = CURSES.register(Witchery.id("sinking")) {
        CurseOfSinking()
    }

    val WALKING_NIGHTMARE: RegistrySupplier<CurseOfWalkingNightmare> =
        CURSES.register(Witchery.id("walking_nightmare")) {
            CurseOfWalkingNightmare()
        }

    fun init() {

    }
}