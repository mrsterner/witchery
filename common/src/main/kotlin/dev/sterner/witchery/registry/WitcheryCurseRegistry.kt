package dev.sterner.witchery.registry

import com.google.common.base.Suppliers
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.curse.*
import dev.sterner.witchery.ritual.BindFamiliarRitual
import dev.sterner.witchery.ritual.EmptyRitual
import dev.sterner.witchery.ritual.PushMobsRitual
import dev.sterner.witchery.ritual.ResurrectFamiliarRitual
import io.wispforest.accessories.Accessories.MODID
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import java.util.function.Supplier


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

    val WALKING_NIGHTMARE: RegistrySupplier<CurseOfWalkingNightmare> = CURSES.register(Witchery.id("walking_nightmare")) {
        CurseOfWalkingNightmare()
    }

    fun init(){

    }
}