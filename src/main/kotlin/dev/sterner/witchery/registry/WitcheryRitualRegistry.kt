package dev.sterner.witchery.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.ritual.*
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.RegistryBuilder
import java.util.function.Supplier


object WitcheryRitualRegistry {

    val ID = Witchery.id("ritual")

    val RITUAL_REGISTRY_KEY: ResourceKey<Registry<Ritual>> = ResourceKey.createRegistryKey(ID)

    val RITUAL_REGISTRY: Registry<Ritual> =
        RegistryBuilder(RITUAL_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ID)
            .maxId(256)
            .create()

    val RITUALS: DeferredRegister<Ritual> = DeferredRegister.create(RITUAL_REGISTRY, Witchery.MODID)


    val EMPTY: DeferredHolder<Ritual, EmptyRitual> = RITUALS.register("empty", Supplier { EmptyRitual() })
    val PUSH_MOBS: DeferredHolder<Ritual, PushMobsRitual> = RITUALS.register("push_mobs", Supplier { PushMobsRitual() })
    val REMOVE_CURSE: DeferredHolder<Ritual, RemoveCurseRitual> =
        RITUALS.register("remove_curse", Supplier { RemoveCurseRitual() })
    val BIND_FAMILIAR: DeferredHolder<Ritual, BindFamiliarRitual> =
        RITUALS.register("bind_familiar", Supplier { BindFamiliarRitual() })
    val RESURRECT_FAMILIAR: DeferredHolder<Ritual, ResurrectFamiliarRitual> =
        RITUALS.register("resurrect_familiar", Supplier { ResurrectFamiliarRitual() })
    val BIND_SPECTRAL_CREATURES: DeferredHolder<Ritual, BindSpectralCreaturesRitual> =
        RITUALS.register("bind_spectral_creatures", Supplier { BindSpectralCreaturesRitual() })
    val ROT: DeferredHolder<Ritual, RotRitual> = RITUALS.register("rot", Supplier { RotRitual() })

    fun getById(id: ResourceLocation): Ritual? {
        val holder = RITUALS.entries.firstOrNull { it.id == id }
        return holder?.get()
    }

    /** Codec for serialization, safe for data gen */
    val CODEC: Codec<Ritual> = RecordCodecBuilder.create { instance ->
        instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter { it.id }
        ).apply(instance) { resourceLocation ->
            getById(resourceLocation) ?: EmptyRitual() // fallback during data gen
        }
    }
}
