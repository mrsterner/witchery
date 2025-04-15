package dev.sterner.witchery.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.ritual.*
import net.minecraft.resources.ResourceLocation


object WitcheryRitualRegistry {

    val ID = Witchery.id("ritual")

    val RITUALS: Registrar<Ritual> = RegistrarManager.get(Witchery.MODID).builder<Ritual>(ID)
        .syncToClients().build()

    val EMPTY: RegistrySupplier<EmptyRitual> = RITUALS.register(Witchery.id("empty")) {
        EmptyRitual()
    }

    val PUSH_MOBS: RegistrySupplier<PushMobsRitual> = RITUALS.register(Witchery.id("push_mobs")) {
        PushMobsRitual()
    }

    val BIND_FAMILIAR: RegistrySupplier<BindFamiliarRitual> = RITUALS.register(Witchery.id("bind_familiar")) {
        BindFamiliarRitual()
    }

    val RESURRECT_FAMILIAR: RegistrySupplier<ResurrectFamiliarRitual> =
        RITUALS.register(Witchery.id("resurrect_familiar")) {
            ResurrectFamiliarRitual()
        }

    val BIND_SPECTRAL_CREATURES = RITUALS.register(Witchery.id("bind_spectral_creatures")) {
        BindSpectralCreaturesRitual()
    }

    val CODEC: Codec<Ritual?> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<Ritual> ->
        instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter { ritual -> ritual.id }
        ).apply(instance) { resourceLocation ->
            RITUALS.get(resourceLocation)
        }
    }

    fun init() {

    }
}